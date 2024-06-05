package org.tum.thesis.experiment.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleVerifiableCredential implements VerifiableCredential {

    private UUID uuid;

    @Override
    public VerifiableCredential create() {
        return new SimpleVerifiableCredential(UUID.randomUUID());
    }

    @Override
    public String getStringRepresentation() {
        return uuid.toString();
    }

}
