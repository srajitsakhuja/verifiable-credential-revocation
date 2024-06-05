package org.tum.thesis.utils.filter.filter;

import java.io.Serializable;

public interface Filter extends Serializable {
    void put(String value);

    boolean mightContain(String value);

}
