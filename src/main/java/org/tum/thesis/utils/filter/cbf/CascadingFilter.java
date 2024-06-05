package org.tum.thesis.utils.filter.cbf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tum.thesis.utils.filter.data.VerifiableCredential;
import org.tum.thesis.utils.filter.filter.Filter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class CascadingFilter implements Serializable {
    private List<Filter> filters;

    public void generate(Set<VerifiableCredential<?>> validVcs, Set<VerifiableCredential<?>> invalidVcs) {
        Instant start = Instant.now();

        filters = new ArrayList<>();
        Set<VerifiableCredential<?>> shouldContain = new HashSet<>(invalidVcs);
        Set<VerifiableCredential<?>> shouldNotContain = new HashSet<>(validVcs);

        List<Integer> falsePositiveCount = new ArrayList<>();
        List<Long> bitSizes = new ArrayList<>();
        while (true) {
            Filter filter = createFilter(shouldContain);
            filters.add(filter);
            Set<VerifiableCredential<?>> shouldNotContainedThatAreContained =
                    findContainedCredentials(filter, shouldNotContain);
            if (shouldNotContainedThatAreContained.isEmpty()) {
                break;
            }
            falsePositiveCount.add(shouldNotContainedThatAreContained.size());
            shouldNotContain = shouldContain;
            shouldContain = shouldNotContainedThatAreContained;
            System.out.println(shouldContain.size());
            System.out.println(shouldNotContain.size());
        }
        Instant finish = Instant.now();
    }

    public boolean isRevoked(VerifiableCredential<?> verifiableCredential) {
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

    private Filter createFilter(Set<VerifiableCredential<?>> including) {
        Filter filter = createFilter(including.size());

        including.stream().map(VerifiableCredential::getStringRepresentation).forEach(filter::put);

        return filter;
    }

    private Set<VerifiableCredential<?>> findContainedCredentials(Filter filter, Set<VerifiableCredential<?>> credentials) {
        return credentials
                .stream()
                .filter(verifiableCredential ->
                        filter.mightContain(verifiableCredential.getStringRepresentation()))
                .collect(Collectors.toSet());
    }

    protected abstract Filter createFilter(int size);
}
