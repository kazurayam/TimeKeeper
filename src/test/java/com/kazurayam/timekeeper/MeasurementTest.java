package com.kazurayam.timekeeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class MeasurementTest {

    private final Logger logger = LoggerFactory.getLogger(TestHelper.getClassName());

    private Measurement m;

    @BeforeEach
    public void setup() {
        m = new Measurement("foo", TestHelper.getColumnNames());
        m.add(TestHelper.makeRecord1());
        m.add(TestHelper.makeRecord2());
    }

    @Test
    public void test_getId() {
        assertEquals("foo", m.getId());
    }

    @Test
    public void test_size() {
        assertEquals(2, m.size());
    }

    @Test
    public void test_iterator() {
        m.forEach(record -> {
            logger.debug(record.toString());
        });
    }

    @Test
    public void test_get() {
        Record r = m.get(0);
        assertEquals("Test Cases/printID", r.getAttributes().get("testCaseId"));
    }

    @Test
    public void test_newRecord() {
        Record record = m.newRecord();
        Set<String> keySet = record.getAttributes().keySet();
        assertTrue(keySet.size() > 0);
        assertTrue(keySet.contains("case"));
    }

}
