package com.terry.backend.core;

public enum YN {

    Y,
    N;

    public static boolean to(String value) {
        return Y.name().equalsIgnoreCase(value);
    }

}
