# Revocation and Suspension of Verifiable Credentials
The revocation and suspension of Verifiable Credentials (VCs) are important use cases. This repository stores code that experiments with different revocation data structures, and proposes the infrastructure layer for VC revocation and suspension.

In this repository, we implement two services - the Membership Management Service (MMS) and the Membership Checking Service (MCS). The MMS is expected to be run and managed by the party issuing the VCs and the MCS can be operated by any actor wanting to verify a VC. VC verification in this context mainly pertains to checking if it is revoked/suspended or not. The MMS helps the issuer manage the lifecycle of a VC by supporting operations to add, revoke, unrevoke VCs. 

The revocation mechanism in this repository is an implementation of the proposals made in the master's thesis on [Design, Implementation, and Assessment of an
Improved Revocation Mechanism for Verifiable
Credentials](https://wwwmatthes.in.tum.de/file/1q8iwb60as103/Sebis-Public-Website/-/Master-s-Thesis-Srajit-Sakhuja/Srajit_Sakhuja_MT.pdf).

## Compile-time Dependencies
The code in this repository is implemented entirely in Java. We use the Spring framework for implementing the endpoints and interacting with the database and other common convenience libraries like Lombok. Besides these, the noteworthy dependencies are as follows:

1. H2: This application has very straightforward storage needs and we use the popular in-memory database H2 for data persistence. For persisting state across sessions, we disable the default state deletion that H2 does and persist state. This is controlled using the `spring.datasource.url` property in the `application.properties` file.
2. DIDKit: A popular [library](https://github.com/spruceid/didkit) in the VC ecosystem, we use DIDKit for checking the validity of our VCs. We found DIDKit's Rust implementation, cross-compiled it to a [jar file](https://github.com/srajitsakhuja/verifiable-credential-revocation/blob/main/didkit.jar) in order to use it as a compile-time dependency in this project.
3. IPFS: We use [IPFS's HTTP client](https://mvnrepository.com/artifact/com.github.ipfs/java-ipfs-http-client) for interacting with IPFS. This is useful to both write the revocation list in the MMS and to read the revocation list in the MCS.

## Runtime Dependencies
The program requires a running instance of the IPFS daemon with RPCs listening on port 5001. If we don't have the daemon running, the program fails gracefully throwing the following error:
```
Caused by: java.lang.RuntimeException: Couldn't connect to IPFS daemon at http://127.0.0.1:5001/api/v0/version
 Is IPFS running?
```
Going through the [IPFS documentation](https://ipfs.tech/developers/) is a quick way to setup the IPFS daemon locally and then execute it using the `ipfs daemon --enable-pubsub-experiment` command.

## Configuring the Membership Management Service as a New Issuer
Launching a new instance of the MMS will automatically create an empty instance of the H2 database. In addition to an H2 instance, each issuer must lease its own storage on IPFS. We provide two convenience scripts in the code that help you do this. 

1. `src/main/resources/ipfs-to-ipns/index.js` allows us to create a new IPNS name and key. This script can be executed using the `node index.js` command and prints the IPNS name and key to the console as its output. The key is used to write to your issuer's IPNS space and the MMS can be configured to do this using the `ipns.key` property in the `application.properties` file. As a best practice, we also store the IPNS name as a property in this file although it is not referenced in the code. This key and name are tied to each other and every update to the IPNS space can be checked by going to the `https://name.web3.storage/name/<ipns-name>` URL, e.g., [this page](https://name.web3.storage/name/k51qzi5uqu5dhs4mze0mjps995qd7rtd8hfi3e1qd27689y0aik76lp2zctxej) shows the state of the IPNS space we allocated during application development.
2. `src/main/resources/ipfs-to-ipns/update-ipfs.js` allows us to update the value stored at an IPNS address. During application development, we used this script to reset the IPNS address to its initial state when no VC has been revoked yet using the `node update-ipfs.js CAESQLO0p83GgXkCYBJhO+VVCmMS1O0c81JKYaobMn/MS2lUQCC/0iZ8L3O3o4pP3dM1GNT++MGN5E7vwPXmbzQ8pls= init` where `CAESQLO0p83GgXkCYBJhO+VVCmMS1O0c81JKYaobMn/MS2lUQCC/0iZ8L3O3o4pP3dM1GNT++MGN5E7vwPXmbzQ8pls=` is the `ipns.key` we used.

## Executing the program
1. If you are running the program for the first time, you would need to allocate an IPNS address. This can be done using the `src/main/resources/ipfs-to-ipns/index.js` script described in the previous section.
2. [optional] To start with a clean database, delete the `TestDataBase.mv.db` file and run the `node update-ipfs.js <ipns-key> init` command.
3. Depending on whether you are acting as an issuer or a verifier, you will either run the MMS or the MCS.
    1. If you are an issuer running the MMS, you must configure the `mainClass` the `pom.xml` file as  `org.tum.thesis.membershipmanagementservice.RestServiceApplication`.
    2. If you are an verifier running the MCS, you must configure the `mainClass` the `pom.xml` file as  `org.tum.thesis.membershipcheckingservice.RestServiceApplication`.
5. Run the application using the `mvn spring-boot:run` command.

## Sample test cases
The application expects JSON inputs of the following form where the VC is an encoded string stored against the "vc" key. We used the [GX-Credentials library](https://github.com/GAIA-X4PLC-AAD/gx-credentials) for generating these VCs. 
```
{
    "vc": "{\"credentialSubject\":{\"gx:issuerCompanyID\":\"tz1S9HwgrAYxSpK4FLn7pUXgcKJYwWvqHUMq\",\"gx:arbitraryKey\":\"arbitraryValue\",\"gx:revocationList\":\"k51qzi5uqu5dm580c4rapmtyxgbkm9eebih45wa30g75pr2fpnqxk26zj4tggt\",\"gx-terms-and-conditions:gaiaxTermsAndConditions\":\"70c1d713215f95191a11d38fe2341faed27d19e083917bc8732ca4fea4976700\",\"gx:legalRegistrationNumber\":{\"gx:vatID\":\"IPNS-enabled emp-2\"},\"gx:issuerCompanyName\":\"IPNS-enabled org\",\"id\":\"did:pkh:tz:tz1PFPoWGLNwJqHpxBjmAGTAqgbgsxTagAbQ\",\"type\":\"gx:LegalParticipant\",\"gx:legalName\":\"IPNS-enabled emp-2\"},\"issuanceDate\":\"2024-02-11T19:06:20.416Z\",\"id\":\"urn:uuid:beb90af1-4f07-47ff-b887-e13954151e01\",\"proof\":{\"proofValue\":\"edsigtyAkZvb4oXd5UXLcbjhqbpV2ontU6dF8HyiwUJYhNY3ULxxg52zNpEBjTDF9rCXL5MmXUFKhSgvSkhmyBXvzDPjTZho4Ek\",\"created\":\"2024-02-11T19:06:20.416Z\",\"publicKeyJwk\":{\"kty\":\"OKP\",\"crv\":\"Ed25519\",\"x\":\"PwrNgyfk-75XkEnPG6415Dm0zQf01BPg6-LT-iQmBps\",\"alg\":\"EdBlake2b\"},\"proofPurpose\":\"assertionMethod\",\"type\":\"TezosSignature2021\",\"verificationMethod\":\"did:pkh:tz:tz1S9HwgrAYxSpK4FLn7pUXgcKJYwWvqHUMq#TezosMethod2021\",\"@context\":{\"TezosMethod2021\":\"https://w3id.org/security#TezosMethod2021\",\"TezosSignature2021\":{\"@id\":\"https://w3id.org/security#TezosSignature2021\",\"@context\":{\"expires\":{\"@type\":\"http://www.w3.org/2001/XMLSchema#dateTime\",\"@id\":\"https://w3id.org/security#expiration\"},\"proofValue\":\"https://w3id.org/security#proofValue\",\"created\":{\"@type\":\"http://www.w3.org/2001/XMLSchema#dateTime\",\"@id\":\"http://purl.org/dc/terms/created\"},\"publicKeyJwk\":{\"@type\":\"@json\",\"@id\":\"https://w3id.org/security#publicKeyJwk\"},\"type\":\"@type\",\"nonce\":\"https://w3id.org/security#nonce\",\"domain\":\"https://w3id.org/security#domain\",\"@protected\":true,\"@version\":1.1,\"challenge\":\"https://w3id.org/security#challenge\",\"proofPurpose\":{\"@type\":\"@vocab\",\"@id\":\"https://w3id.org/security#proofPurpose\",\"@context\":{\"assertionMethod\":{\"@type\":\"@id\",\"@id\":\"https://w3id.org/security#assertionMethod\",\"@container\":\"@set\"},\"@protected\":true,\"@version\":1.1,\"id\":\"@id\",\"type\":\"@type\",\"authentication\":{\"@type\":\"@id\",\"@id\":\"https://w3id.org/security#authenticationMethod\",\"@container\":\"@set\"}}},\"id\":\"@id\",\"verificationMethod\":{\"@type\":\"@id\",\"@id\":\"https://w3id.org/security#verificationMethod\"}}}}},\"type\":[\"VerifiableCredential\",\"Employee Credential\"],\"@context\":[\"https://www.w3.org/2018/credentials/v1\"],\"issuer\":\"did:pkh:tz:tz1S9HwgrAYxSpK4FLn7pUXgcKJYwWvqHUMq\"}"
}
```
These VCs can be used to add, revoke, and unrevoke the corresponding Verifiable Credential using the application's `/addVerifiableCredential, /revokeVerifiableCredential, /unrevokeVerifiableCredential` endpoints. After revoking a certain credential, we can check the `https://name.web3.storage/name/<ipns-name>` URL to see if the IPFS address stored against this IPNS address has been updated or not. It should be noted that for the same set of revoked and unrevoked VCs, the IPFS address would always be the same because it is generated using the hash of the contents that it stores. Furthermore, we can test that the VC has actually been revoked or not using the MCS's `/checkMembership` endpoint.
