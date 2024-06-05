package org.tum.thesis.experiment.cbf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tum.thesis.experiment.analysis.ExperimentResults;
import org.tum.thesis.experiment.data.ExperimentData;
import org.tum.thesis.experiment.data.VerifiableCredential;
import org.tum.thesis.experiment.filter.Filter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class CascadingFilter {
    // TODO: 1. re-introduce the FPR, compute it for layer-1 and the subsequent layers.
    // TODO: 2. add multithreading.
    // TODO: 3. use 'real' VCs - will have an impact on size - probably not needed
    // TODO: 4. implement diffing.
        // - most likely an XOR bitstring which has too many zeroes (because things generally stay the same from one day
        // to another so, the XOR bitstring is highly compressible. => reconfirm
    private List<Filter> filters;
    private static final String FILTER_DUMP = "filter-dump";
    private static final String TARGET_DIRECTORY_NAME = "target";

    public ExperimentResults generate(ExperimentData experimentData) {
        Instant start = Instant.now();

        filters = new ArrayList<>();
        Set<VerifiableCredential> shouldContain = experimentData.revokedCredentials();
        Set<VerifiableCredential> shouldNotContain = experimentData.validCredentials();

        List<Integer> falsePositiveCount = new ArrayList<>();
        List<Long> bitSizes = new ArrayList<>();
        while(true) {
            Filter filter = createFilter(shouldContain);
            filters.add(filter);
            Set<VerifiableCredential> shouldNotContainedThatAreContained =
                    findContainedCredentials(filter, shouldNotContain);
            if (shouldNotContainedThatAreContained.isEmpty()) {
                break;
            }
            falsePositiveCount.add(shouldNotContainedThatAreContained.size());
            shouldNotContain = shouldContain;
            shouldContain = shouldNotContainedThatAreContained;
        }
        Instant finish = Instant.now();
        return new ExperimentResults(
                filters.size(),
                bitSizes,
                falsePositiveCount,
                Duration.between(start, finish).toMillis()
        );
    }

    public boolean isRevoked(VerifiableCredential verifiableCredential) {
        int filterIndex = 0;
        while (filterIndex < filters.size()) {
            boolean contains = filters.get(filterIndex)
                    .mightContain(verifiableCredential.getStringRepresentation());
            if (!contains) {
                // if it is not found in an even filter (e.g., the first (0th) one), it is not revoked.
                // if it is not found in an odd filter (e.g., the second (1st) one), it is not in the valid set
                // therefore, is revoked.
                return (filterIndex % 2 == 1);
            }
            filterIndex++;
        }
        return (filters.size() % 2 == 1);
    }

    private Filter createFilter(Set<VerifiableCredential> including) {
        Filter filter = createFilter(including.size());

        including.stream().map(VerifiableCredential::getStringRepresentation).forEach(filter::put);

        return filter;
    }

    private Set<VerifiableCredential> findContainedCredentials(Filter filter, Set<VerifiableCredential> credentials) {
        return credentials
                .stream()
                .filter(verifiableCredential ->
                        filter.mightContain(verifiableCredential.getStringRepresentation()))
                .collect(Collectors.toSet());
    }

    protected abstract Filter createFilter(int size);
}
