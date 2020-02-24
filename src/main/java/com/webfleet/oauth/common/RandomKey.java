package com.webfleet.oauth.common;

import java.util.UUID;

/**
 * Simple pseudo random generator. Used for obtaining non critical random values.
 */
public class RandomKey {
    private String random;

    public RandomKey() {
        initialize();
    }

    private void initialize() {
        random = UUID.randomUUID().toString();
    }

    public void reset() {
        initialize();
    }

    public String getKey() {
        return random;
    }
}
