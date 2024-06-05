package org.tum.thesis.experiment.data;

import java.util.Set;

public record ExperimentData(int size, double revocationRate, Set<VerifiableCredential> validCredentials, Set<VerifiableCredential> revokedCredentials) {
}
