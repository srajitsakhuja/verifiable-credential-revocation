package org.tum.thesis.experiment.data;

public interface VerifiableCredential {
    VerifiableCredential create();

    String getStringRepresentation();
}
