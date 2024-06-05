package org.tum.thesis.experiment.cbf;

import org.tum.thesis.experiment.filter.CuckooFilter;
import org.tum.thesis.experiment.filter.Filter;

public class CascadingCuckooFilter extends CascadingFilter {
    @Override
    protected final Filter createFilter(int size) {
        return new CuckooFilter(size);
    }
}
