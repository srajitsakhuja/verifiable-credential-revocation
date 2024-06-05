package org.tum.thesis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.spark.util.sketch.BloomFilter;
import org.tum.thesis.experiment.analysis.ExperimentResults;
import org.tum.thesis.experiment.data.ExperimentData;
import org.tum.thesis.experiment.data.VerifiableCredential;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CascadingBloomFilter {
    // TODO: 1. re-introduce the FPR, compute it for layer-1 and the subsequent layers.
    // TODO: 2. add multithreading.
    // TODO: 3. use 'real' VCs - will have an impact on size - probably not needed
    // TODO: 4. implement diffing.
        // - most likely an XOR bitstring which has too many zeroes (because things generally stay the same from one day
        // to another so, the XOR bitstring is highly compressible. => reconfirm

    private List<BloomFilter> bloomFilters;
    private static final String BLOOM_FILTER_DUMP = "bloom-filter-dump";
    private static final String TARGET_DIRECTORY_NAME = "target";

    public ExperimentResults generate(ExperimentData experimentData, String suffix, boolean saveToDisk) {
        Instant start = Instant.now();

        bloomFilters = new ArrayList<>();
        Set<VerifiableCredential> shouldContain = experimentData.revokedCredentials();
        Set<VerifiableCredential> shouldNotContain = experimentData.validCredentials();

        List<Integer> falsePositiveCount = new ArrayList<>();
        List<Long> bitSizes = new ArrayList<>();
        while(true) {
            BloomFilter bloomFilter = createBloomFilter(shouldContain);
            bloomFilters.add(bloomFilter);
            bitSizes.add(bloomFilter.bitSize());
            Set<VerifiableCredential> shouldNotContainedThatAreContained =
                    findContainedCredentials(bloomFilter, shouldNotContain);
            if (shouldNotContainedThatAreContained.isEmpty()) {
                break;
            }
            falsePositiveCount.add(shouldNotContainedThatAreContained.size());
            shouldNotContain = shouldContain;
            shouldContain = shouldNotContainedThatAreContained;
        }
        Instant finish = Instant.now();
        if (saveToDisk) {
            saveToDisk(BLOOM_FILTER_DUMP + suffix);
        }
        return new ExperimentResults(
                bloomFilters.size(),
                bitSizes,
                falsePositiveCount,
                Duration.between(start, finish).toMillis()
        );
    }

    public boolean isRevoked(VerifiableCredential verifiableCredential) {
        int bloomFilterIndex = 0;
        while (bloomFilterIndex < bloomFilters.size()) {
            boolean contains = bloomFilters.get(bloomFilterIndex)
                    .mightContainString(verifiableCredential.getStringRepresentation());
            if (!contains) {
                // if it is not found in an even bloom filter (e.g., the first (0th) one), it is not revoked.
                // if it is not found in an odd bloom filter (e.g., the second (1st) one), it is not in the valid set
                // therefore, is revoked.
                return (bloomFilterIndex % 2 == 1);
            }
            bloomFilterIndex++;
        }
        return (bloomFilters.size() % 2 == 1);
    }

    private BloomFilter createBloomFilter(Set<VerifiableCredential> including) {
        BloomFilter bloomFilter = BloomFilter.create(including.size());

        including.stream().map(VerifiableCredential::getStringRepresentation).forEach(bloomFilter::putString);

        return bloomFilter;
    }

    private Set<VerifiableCredential> findContainedCredentials(BloomFilter bloomFilter, Set<VerifiableCredential> credentials) {
        return credentials
                .stream()
                .filter(verifiableCredential ->
                        bloomFilter.mightContainString(verifiableCredential.getStringRepresentation()))
                .collect(Collectors.toSet());
    }

    private void saveToDisk(String fileName) {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(TARGET_DIRECTORY_NAME, fileName))) {
            for (BloomFilter bloomFilter: bloomFilters) {
                bloomFilter.writeTo(outputStream);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
