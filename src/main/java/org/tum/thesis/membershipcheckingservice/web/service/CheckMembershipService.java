package org.tum.thesis.membershipcheckingservice.web.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.tum.thesis.utils.filter.cbf.CascadingFilter;
import org.tum.thesis.utils.filter.data.VerifiableCredential;
import org.tum.thesis.utils.ipfs.IpfsClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.tum.thesis.utils.ipfs.IpfsClient.NO_REVOKED_VC_FILTER_VALUE;

@Service
public class CheckMembershipService {
    private static final String IPFS_ADDRESS_REGEX = "(/ipfs/)(.*)";
    IpfsClient ipfsClient;

    @Autowired
    CheckMembershipService(IpfsClient ipfsClient) {
        this.ipfsClient = ipfsClient;
    }

    private static final String IPNS_BASE_URL = "https://name.web3.storage/name/%s";
    private static final String IPFS_ADDRESS_JSON_KEY = "value";

    private static final int TIMEOUT = 5000;

    public boolean check(String ipnsAddress, String vcId) throws IOException, ParseException {
        String ipfsHash = findRevocationListIpfsHash(ipnsAddress);
        if (ipfsHash.equals(NO_REVOKED_VC_FILTER_VALUE)) {
            System.out.println("This org has not revoked any VCs");
            return false;
        }
        byte[] filter = ipfsClient.read(ipfsHash);
        CascadingFilter cascadingFilter = (CascadingFilter) (SerializationUtils.deserialize(filter));
        if (cascadingFilter == null) {
            throw new IllegalArgumentException("Malformed cascading filter provided");
        }

        return cascadingFilter.isRevoked(new VerifiableCredential<>(vcId));
    }

    public static void main(String[] args) throws IOException, ParseException {
        CheckMembershipService checkMembershipService = new CheckMembershipService(new IpfsClient());
        String id = "urn:uuid:beb90af1-4f07-47ff-b887-e13954151e02";
        System.out.println(checkMembershipService.check("k51qzi5uqu5dhs4mze0mjps995qd7rtd8hfi3e1qd27689y0aik76lp2zctxej", id));
    }

    private String findRevocationListIpfsHash(String ipnsAddress) throws IOException, ParseException {
        URL url = new URL(String.format(IPNS_BASE_URL, ipnsAddress));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(TIMEOUT);
        con.setReadTimeout(TIMEOUT);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content.toString());
        String revocationListAddress = (String) json.get(IPFS_ADDRESS_JSON_KEY);

        return revocationListAddress;
//        System.out.println(revocationListAddress);
//        return extractHashFromIpfsAddress(revocationListAddress);
    }

    private String extractHashFromIpfsAddress(String address) {
        Pattern r = Pattern.compile(IPFS_ADDRESS_REGEX);
        Matcher m = r.matcher(address);

        if (!m.find() || m.groupCount() != 2) {
            throw new RuntimeException("Can not parse IPFS address");
        }

        return m.group(2);
    }
}
