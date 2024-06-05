package org.tum.thesis.utils.ipfs;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Repository
public class IpfsClient {
    // A special reserved value that is stored on IPNS to represent the case where there are no revoked VCs among the
    // VCs issued by the given organization.
    public static final String NO_REVOKED_VC_FILTER_VALUE = "init";

    private static final IPFS IPFS_CLIENT = new IPFS("/ip4/127.0.0.1/tcp/5001");
    private static final String UPDATE_IPFS_SCRIPT_PATH = "ipfs-to-ipns/update-ipfs.js";

    public String write(String ipnsKey, byte[] content) throws IOException{
        NamedStreamable.ByteArrayWrapper byteArrayWrapper = new NamedStreamable.ByteArrayWrapper(content);
        MerkleNode node = IPFS_CLIENT.add(byteArrayWrapper).get(0);
        String value = node.hash.toBase58();

        System.out.println("Revocation List written to:" + value + "\n");

        ProcessBuilder processBuilder = new ProcessBuilder("node", getUpdateIpfsFilePath(), ipnsKey, value);

        Process process = processBuilder.start();
        String result = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
        return result;
    }

    public String clear(String ipnsKey) throws IOException{

        System.out.println("Clearing Revocation List");

        ProcessBuilder processBuilder = new ProcessBuilder("node", getUpdateIpfsFilePath(), ipnsKey, NO_REVOKED_VC_FILTER_VALUE);

        Process process = processBuilder.start();
        String result = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
        return result;
    }

    public byte[] read(String hash) throws IOException {
        return IPFS_CLIENT.cat(Multihash.fromBase58(hash));
    }

    private String getUpdateIpfsFilePath() throws IOException {
        File file = new ClassPathResource(UPDATE_IPFS_SCRIPT_PATH).getFile();
        return file.getAbsolutePath();
    }
}
