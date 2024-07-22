package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReportOptionsTest {

    @Test
    public void test_construction() {
        Timekeeper tk = new Timekeeper();
        Measurement m1 =
                new Measurement.Builder("How long it waited",
                        Arrays.asList("Case")).build();
        tk.add(new Table.Builder(m1).build());
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
