package org.tum.thesis.utils.filter.data;

public class VerifiableCredential<ID> {

    ID id;

    public VerifiableCredential(ID id) {
        this.id = id;
    }
    public String getStringRepresentation() {
        return String.valueOf(id);
    }
}

