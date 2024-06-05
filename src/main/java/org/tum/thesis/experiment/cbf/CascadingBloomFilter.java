package org.tum.thesis.experiment.cbf;

import org.tum.thesis.experiment.filter.BloomFilter;
import org.tum.thesis.experiment.filter.Filter;

public class CascadingBloomFilter extends CascadingFilter {
    @Override
    protected final Filter createFilter(int size) {
        return new BloomFilter(size);
    }
}
