package com.kazurayam.timekeeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RecordComparatorByDurationTest {

    private Measurement m;

    @BeforeEach
    public void setup() throws IOException {
        m = TestHelper.makeRichMeasurement();
    }

    @Test
    public void test_smoke() {
        System.out.println(m.toJson());
    }

}
