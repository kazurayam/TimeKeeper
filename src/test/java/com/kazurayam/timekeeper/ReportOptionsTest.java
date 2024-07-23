package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReportOptionsTest {

    @Test
    public void test_construction() {
        ReportOptions opts =
                new ReportOptions.Builder()
                        .noDescription()  // require no description
                        .noLegend()       // require no legend
                        .noGraph()        // require no duration graph
                        .build();
        assertNotNull(opts);
        assertFalse(opts.requireDescription());
        assertFalse(opts.requireLegend());
        assertFalse(opts.requireGraph());
    }
}
