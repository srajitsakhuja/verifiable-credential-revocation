package org.tum.thesis.membershipmanagementservice.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spruceid.DIDKit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class VcValidityChecker {

    private static final String DID_KIT_OPTIONS = "{}";
    static boolean isValid(String vc) {
        ObjectMapper objectMapper = new ObjectMapper();
        VerificationResult verificationResult;

        try {
            String verificationResultString = DIDKit.verifyCredential(vc, DID_KIT_OPTIONS);
            verificationResult = objectMapper.readValue(verificationResultString, VerificationResult.class);
        } catch (Exception e) {
            String errorMessage = String.format("Error in parsing verification result for %s: %s", vc, e.getMessage());
            throw new RuntimeException(errorMessage);
        }

        return verificationResult.errors.isEmpty() && verificationResult.warnings.isEmpty();
    }

    @Data
    @NoArgsConstructor
    private static class VerificationResult {
        List<String> checks;

        List<String> warnings;

        List<String> errors;
    }

//    public static void main(String[] args) {
//        String vc = "{ \"credentialSubject\": { \"gx:issuerCompanyID\": \"tz1ZWmek3d5wyR3Tn61mD4GLgLo3xdnGx5ne\", \"gx-terms-and-conditions:gaiaxTermsAndConditions\": \"70c1d713215f95191a11d38fe2341faed27d19e083917bc8732ca4fea4976700\", \"gx:legalRegistrationNumber\": { \"gx:vatID\": \"12345678\" }, \"gx:issuerCompanyName\": \"Srajit Inc.\", \"id\": \"did:pkh:tz:tz1MRoRc5DgRmXHiyfxtGm3EB8mqeLpBfrUA\", \"type\": \"gx:LegalParticipant\", \"gx:legalName\": \"Srajit Emp\" }, \"issuanceDate\": \"2023-10-07T16:41:56.044Z\", \"id\": \"urn:uuid:20e34a3d-fec5-46d7-949f-7ebf3947642d\", \"proof\": { \"proofValue\": \"edsigtajEzCi1DApxSue15AC5uwehv7kU6hnW5kCRAhys1RGbKkPr34R2sFrGXzJVihMDUKcz4qSrVFVNhDAsUxkyn9mtjyo648\", \"created\": \"2023-10-07T16:41:56.082Z\", \"publicKeyJwk\": { \"kty\": \"OKP\", \"crv\": \"Ed25519\", \"x\": \"JGZmlLAGKW_FrCtrddqP8vUghlOyCVi2qTL4hrO6CMY\", \"alg\": \"EdBlake2b\" }, \"proofPurpose\": \"assertionMethod\", \"type\": \"TezosSignature2021\", \"verificationMethod\": \"did:pkh:tz:tz1ZWmek3d5wyR3Tn61mD4GLgLo3xdnGx5ne#TezosMethod2021\", \"@context\": { \"TezosMethod2021\": \"https://w3id.org/security#TezosMethod2021\", \"TezosSignature2021\": { \"@id\": \"https://w3id.org/security#TezosSignature2021\", \"@context\": { \"expires\": { \"@type\": \"http://www.w3.org/2001/XMLSchema#dateTime\", \"@id\": \"https://w3id.org/security#expiration\" }, \"proofValue\": \"https://w3id.org/security#proofValue\", \"created\": { \"@type\": \"http://www.w3.org/2001/XMLSchema#dateTime\", \"@id\": \"http://purl.org/dc/terms/created\" }, \"publicKeyJwk\": { \"@type\": \"@json\", \"@id\": \"https://w3id.org/security#publicKeyJwk\" }, \"type\": \"@type\", \"nonce\": \"https://w3id.org/security#nonce\", \"domain\": \"https://w3id.org/security#domain\", \"@protected\": true, \"@version\": 1.1, \"challenge\": \"https://w3id.org/security#challenge\", \"proofPurpose\": { \"@type\": \"@vocab\", \"@id\": \"https://w3id.org/security#proofPurpose\", \"@context\": { \"assertionMethod\": { \"@type\": \"@id\", \"@id\": \"https://w3id.org/security#assertionMethod\", \"@container\": \"@set\" }, \"@protected\": true, \"@version\": 1.1, \"id\": \"@id\", \"type\": \"@type\", \"authentication\": { \"@type\": \"@id\", \"@id\": \"https://w3id.org/security#authenticationMethod\", \"@container\": \"@set\" } } }, \"id\": \"@id\", \"verificationMethod\": { \"@type\": \"@id\", \"@id\": \"https://w3id.org/security#verificationMethod\" } } } } }, \"type\": [ \"VerifiableCredential\", \"Employee Credential\" ], \"@context\": [ \"https://www.w3.org/2018/credentials/v1\" ], \"issuer\": \"did:pkh:tz:tz1ZWmek3d5wyR3Tn61mD4GLgLo3xdnGx5ne\" }";
//        System.out.println(isValid(vc));
//    }
}
