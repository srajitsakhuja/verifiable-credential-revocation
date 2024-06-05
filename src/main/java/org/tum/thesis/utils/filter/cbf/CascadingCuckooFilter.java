package org.tum.thesis.utils.filter.cbf;

import org.tum.thesis.utils.filter.filter.CuckooFilter;
import org.tum.thesis.utils.filter.filter.Filter;

public class CascadingCuckooFilter extends CascadingFilter {
    @Override
    protected final Filter createFilter(int size) {
        return new CuckooFilter(size);
    }
}
