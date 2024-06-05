package org.tum.thesis.experiment.data;

import java.util.*;

public class ExperimentDataGenerator {
    public static ExperimentData generateData(int size, double revocationRate) {
        List<VerifiableCredential> verifiableCredentials = new ArrayList<>();
        int counter = 0;
        while (counter < size) {
            verifiableCredentials.add(new SimpleVerifiableCredential().create());
            counter++;
        }
        counter = 0;
        Set<VerifiableCredential> revokedCredentials = new HashSet<>();
        Set<Integer> revokedCredentialsIndices = new HashSet<>();
        Random random = new Random();
        // TODO: random pick without replacement - done
        while (counter < revocationRate * size) {
            int index = random.nextInt(size);
            if (revokedCredentialsIndices.contains(index)) {
                continue;
            }
            revokedCredentialsIndices.add(index);
            revokedCredentials.add(verifiableCredentials.get(index));
            counter++;
        }
        Set<VerifiableCredential> validCredentials = new HashSet<>(verifiableCredentials);
        validCredentials.removeAll(revokedCredentials);

        return new ExperimentData(size, revocationRate, validCredentials, revokedCredentials);
    }
}
