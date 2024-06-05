package org.tum.thesis.experiment.filter;

public interface Filter {
    void put(String value);

    boolean mightContain(String value);

}
