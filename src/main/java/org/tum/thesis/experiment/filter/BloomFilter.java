package org.tum.thesis.experiment.filter;


public class BloomFilter implements Filter {
    org.apache.spark.util.sketch.BloomFilter bloomFilter;

    public BloomFilter(int size) {
        bloomFilter = org.apache.spark.util.sketch.BloomFilter.create(size);
    }

    @Override
    public void put(String value) {
        bloomFilter.putString(value);
    }

    @Override
    public boolean mightContain(String value) {
        return bloomFilter.mightContainString(value);
    }

    // @Override
    // public long getSize() {
    //     return bloomFilter.bitSize();
    // }
}
