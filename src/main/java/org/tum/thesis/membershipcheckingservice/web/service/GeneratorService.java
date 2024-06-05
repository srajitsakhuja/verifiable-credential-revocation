package org.tum.thesis.membershipcheckingservice.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.tum.thesis.utils.filter.cbf.CascadingBloomFilter;
import org.tum.thesis.utils.filter.cbf.CascadingFilter;
import org.tum.thesis.utils.filter.data.VerifiableCredential;
import org.tum.thesis.utils.ipfs.IpfsClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GeneratorService {
    IpfsClient ipfsClient;

    @Autowired
    GeneratorService(IpfsClient ipfsClient) {
        this.ipfsClient = ipfsClient;
    }

    public String generateFiler(List<?> validVcList, List<?> invalidVcList) throws IOException {
        Set<VerifiableCredential<?>> validVcs = convert(validVcList);
        Set<VerifiableCredential<?>> invalidVcs = convert(invalidVcList);

        CascadingFilter cascadingFilter = new CascadingBloomFilter();
        cascadingFilter.generate(validVcs, invalidVcs);

        byte[] payload = SerializationUtils.serialize(cascadingFilter);
        System.out.println(Arrays.toString(payload));
        return "done";
//        return ipfsClient.write(payload);
    }

    private Set<VerifiableCredential<?>> convert(List<?> ids) {
        return ids.stream().map(VerifiableCredential::new).collect(Collectors.toSet());
    }
}
