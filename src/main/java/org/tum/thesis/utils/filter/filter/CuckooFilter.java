package org.tum.thesis.utils.filter.filter;

import com.google.common.hash.Funnels;

public class CuckooFilter implements Filter {
    com.duprasville.guava.probably.CuckooFilter<String> cuckooFilter;
    public CuckooFilter(int size) {
        cuckooFilter = com.duprasville.guava.probably.CuckooFilter.create(Funnels.stringFunnel(), size);
    }

    @Override
    public void put(String value) {
        cuckooFilter.add(value);
    }

    @Override
    public boolean mightContain(String value) {
        return cuckooFilter.contains(value);
    }

    // @Override
    // public long getSize() {
    //     return cuckooFilter.size();
    // }
}
