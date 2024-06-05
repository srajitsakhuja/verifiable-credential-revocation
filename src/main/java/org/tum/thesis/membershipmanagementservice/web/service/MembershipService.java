package org.tum.thesis.membershipmanagementservice.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.tum.thesis.membershipmanagementservice.store.MembershipRepository;
import org.tum.thesis.membershipmanagementservice.store.respository.InvalidVcRepository;
import org.tum.thesis.membershipmanagementservice.store.respository.ValidVcRepository;
import org.tum.thesis.utils.filter.cbf.CascadingBloomFilter;
import org.tum.thesis.utils.filter.cbf.CascadingFilter;
import org.tum.thesis.utils.filter.data.VerifiableCredential;
import org.tum.thesis.utils.ipfs.IpfsClient;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MembershipService {
//    Name: k51qzi5uqu5dhs4mze0mjps995qd7rtd8hfi3e1qd27689y0aik76lp2zctxej
//    key: CAESQLO0p83GgXkCYBJhO+VVCmMS1O0c81JKYaobMn/MS2lUQCC/0iZ8L3O3o4pP3dM1GNT++MGN5E7vwPXmbzQ8pls=
    private static final String IPNS_KEY = "CAESQLO0p83GgXkCYBJhO+VVCmMS1O0c81JKYaobMn/MS2lUQCC/0iZ8L3O3o4pP3dM1GNT++MGN5E7vwPXmbzQ8pls=";

    private static final String NO_REVOKED_VC_FILTER_VALUE = "init";


    MembershipRepository repository;

    ValidVcRepository validVcRepository;

    InvalidVcRepository invalidVcRepository;
    IpfsClient ipfsClient;

    @Autowired
    MembershipService(MembershipRepository repository, IpfsClient ipfsClient, ValidVcRepository validVcRepository, InvalidVcRepository invalidVcRepository) {
        this.repository = repository;
        this.ipfsClient = ipfsClient;
        this.validVcRepository = validVcRepository;
        this.invalidVcRepository = invalidVcRepository;
    }

    public boolean addVerifiableCredential(String vc) throws IOException {
        if (vc == null) {
            return false;
        }
//        boolean isValid = VcValidityChecker.isValid(vc);

//        if (!isValid) {
//            return false;
//        }
        // TODO: Add additional check that the VC belongs to the company
        // TODO: Add additional check that the VC is not already there in the valid/invalid sets
        repository.addVerifiableCredential(vc);

        if (!invalidVcRepository.listAllIds().isEmpty()) {
            generateFiler(validVcRepository.listAllIds(), invalidVcRepository.listAllIds());
        }
        return true;
    }

    public boolean revokeVerifiableCredential(String vc) throws IOException {
//        boolean isValid = VcValidityChecker.isValid(vc);
//
//        if (!isValid) {
//            return false;
//        }
        repository.revokeVerifiableCredential(vc);

        generateFiler(validVcRepository.listAllIds(), invalidVcRepository.listAllIds());

        // TODO: Add additional check that the VC belongs to the company
        // TODO: Add additional check that it is a revocationVc
        return true;
    }

    public boolean unrevokeVerifiableCredential(String vc) throws IOException {
//        boolean isValid = VcValidityChecker.isValid(vc);
//
//        if (!isValid) {
//            return false;
//        }
        repository.unrevokeVerifiableCredential(vc);

        generateFiler(validVcRepository.listAllIds(), invalidVcRepository.listAllIds());

        // TODO: Add additional check that the VC belongs to the company
        // TODO: Add additional check that it is an unrevocationVc

        return true;
    }

    public Set<String> listVerifiableCredentials() {
        return repository.listVerifiableCredentials();
    }

    public String generateFiler(Set<?> validVcIdsList, Set<?> invalidVcIdsList) throws IOException {
        if (invalidVcIdsList.isEmpty()) {
            return ipfsClient.clear(IPNS_KEY);
        }
        Set<VerifiableCredential<?>> validVcs = convert(validVcIdsList);
        Set<VerifiableCredential<?>> invalidVcs = convert(invalidVcIdsList);

        CascadingFilter cascadingFilter = new CascadingBloomFilter();
        cascadingFilter.generate(validVcs, invalidVcs);
        byte[] payload = SerializationUtils.serialize(cascadingFilter);

//        Uncomment to debug the payload being written to IPFS
//        System.out.println(Arrays.toString(payload));
        return ipfsClient.write(IPNS_KEY, payload);
    }

    private Set<VerifiableCredential<?>> convert(Set<?> ids) {
        return ids.stream().map(VerifiableCredential::new).collect(Collectors.toSet());
    }
}
