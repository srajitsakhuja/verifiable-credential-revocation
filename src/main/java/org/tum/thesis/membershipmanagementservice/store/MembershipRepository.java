package org.tum.thesis.membershipmanagementservice.store;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tum.thesis.membershipmanagementservice.store.model.InvalidVc;
import org.tum.thesis.membershipmanagementservice.store.model.ValidVc;
import org.tum.thesis.membershipmanagementservice.store.respository.InvalidVcRepository;
import org.tum.thesis.membershipmanagementservice.store.respository.ValidVcRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MembershipRepository {
    private static final String ID_KEY = "id";

    ValidVcRepository validVcRepository;
    InvalidVcRepository invalidVcRepository;

    @Autowired
    MembershipRepository(ValidVcRepository validVcRepository, InvalidVcRepository invalidVcRepository) {
        this.validVcRepository = validVcRepository;
        this.invalidVcRepository = invalidVcRepository;
    }

    public void addVerifiableCredential(String vc) {
        String vcId;

        try {
            vcId = extractId(vc);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        validVcRepository.save(new ValidVc(vcId, vc));
    }

    public boolean revokeVerifiableCredential(String revocationVc) {
        String vcId;

        try {
            vcId = extractId(revocationVc);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Optional<ValidVc> vc = validVcRepository.findById(vcId);
        if (vc.isEmpty()) {
            return true;
        }

        String invalidatedVc = vc.get().getVc();
        validVcRepository.delete(new ValidVc(vcId, invalidatedVc));
        invalidVcRepository.save(new InvalidVc(vcId, invalidatedVc));

        return true;
    }

    public boolean unrevokeVerifiableCredential(String unrevocationVc) {
        String vcId;

        try {
            vcId = extractId(unrevocationVc);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Optional<InvalidVc> vc = invalidVcRepository.findById(vcId);
        if (vc.isEmpty()) {
            return true;
        }

        String invalidatedVc = vc.get().getVc();

        invalidVcRepository.delete(new InvalidVc(vcId, invalidatedVc));
        validVcRepository.save(new ValidVc(vcId, invalidatedVc));

        return true;
    }

    public Set<String> listVerifiableCredentials() {
        return validVcRepository.findAll().stream().map(ValidVc::getId).collect(Collectors.toSet());

    }

    private String extractId(String vc) throws ParseException {
        JSONParser parser = new JSONParser();

        JSONObject json;
        json = (JSONObject) parser.parse(vc);

        String vcId = (String) json.get(ID_KEY);

        if (!isValidId(vcId)) {
            throw new IllegalStateException("not a valid VC, id field not found");
        }

        return vcId;
    }

    private boolean isValidId(String id) {
        return !(id == null || id.equals(""));
    }
}
