package com.kazurayam.timekeeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MeasurementListTest {

    private MeasurementList measurementList;

    @BeforeEach
    public void setup() {
        measurementList = new MeasurementList();
    }

    @Test
    public void test_add_size() {
        Measurement m = new Measurement.Builder("foo", TestHelper.getColumnNames()).build();
        measurementList.add(m);
        assertEquals(1, measurementList.size());
    }
}
