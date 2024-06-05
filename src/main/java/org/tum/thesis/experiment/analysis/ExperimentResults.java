package org.tum.thesis.experiment.analysis;

import java.util.List;
import java.util.stream.Collectors;

public record ExperimentResults(int layerCount, List<Long> bitSizes, List<Integer> falsePositives, Long computationTime) {
    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s",
                layerCount,
                serializeList(bitSizes),
                serializeList(falsePositives),
                computationTime);
    }

    String serializeList(List<?> list) {
        return list.stream().map(String::valueOf)
                .collect(Collectors.joining("/", "[", "]"));
    }
}
