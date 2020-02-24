package com.webfleet.oauth.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomKeyTest {

    private RandomKey randomKey;

    @BeforeEach
    public void setUp() {
        this.randomKey = new RandomKey();
    }

    @Test
    public void reset() {
        final String orig = this.randomKey.getKey();
        this.randomKey.reset();
        final String result = this.randomKey.getKey();
        Assertions.assertNotEquals(orig, result);
    }

    @Test
    void getKey() {
        Assertions.assertNotNull(this.randomKey.getKey());
    }
}