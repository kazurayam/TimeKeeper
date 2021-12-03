package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;
import java.util.TreeMap;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MeasurementTest {

    @Test
    public void test_constructor() {
        TreeMap<String, String> attrs = TestHelper.makeAttributes();
        Measurement instance = new Measurement(attrs);
        assertNotNull(instance);
    }

}
