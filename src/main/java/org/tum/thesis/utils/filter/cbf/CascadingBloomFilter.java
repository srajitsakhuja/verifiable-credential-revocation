package org.tum.thesis.utils.filter.cbf;

import org.tum.thesis.utils.filter.filter.BloomFilter;
import org.tum.thesis.utils.filter.filter.Filter;

public class CascadingBloomFilter extends CascadingFilter {
    @Override
    protected final Filter createFilter(int size) {
        return new BloomFilter(size);
    }
}
