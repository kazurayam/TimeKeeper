package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RecordTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testLogger() {
        logger.trace("Hello Trace");
        logger.debug("Hello Debug");
        logger.info("Hello Info");
        logger.warn("Hello Warn");
        logger.error("Hello Error");
    }

    @Test
    public void test_constructor() {
        TreeMap<String, String> attrs = TestHelper.makeAttributes();
        Record m = new Record(attrs);
        assertNotNull(m);
    }

    @Test
    public void test_getDurationMillis() throws Exception {
        TreeMap<String, String> attrs = TestHelper.makeAttributes();
        Record m = new Record(attrs);
        m.setStartAt(LocalDateTime.now());
        Thread.sleep(1000);
        m.setEndAt(LocalDateTime.now());
        long durationMillis = m.getDurationMillis();
        assertTrue(durationMillis >= 1000);
        assertTrue(durationMillis < 1100);
        //
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        logger.debug(methodName + " startAt " + m.getStartAt().format(Record.FORMAT));
        logger.debug(methodName + " endAt   " + m.getEndAt().format(Record.FORMAT));
        logger.debug(methodName + " durationMillis " + durationMillis);
    }

    @Test
    public void test_toString() throws Exception {
        TreeMap<String, String> attrs = TestHelper.makeAttributes();
        Record m = new Record(attrs);
        m.setStartAt(LocalDateTime.now());
        Thread.sleep(1000);
        m.setEndAt(LocalDateTime.now());
        String s = m.toString();
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        logger.debug(methodName + " " + s);
        Gson gson = new Gson();
        try {
            JsonObject jo = gson.fromJson(s, JsonObject.class);
        } catch (Exception e) {
            fail(e.getClass().getSimpleName() + " was thrown");
        }
    }

    @Test
    public void test_compareTo() throws Exception {
        List<Record> list = new ArrayList<Record>();
        // input is "Iteration 2" -> "Iteration 1"
        list.add(new Record(TestHelper.makeAttributes2()));
        list.add(new Record(TestHelper.makeAttributes()));
        List<Record> sorted = list.stream().sorted().collect(Collectors.toList());
        // sorted is "Iteration 1" -> "Iteration 2"
        assertTrue(sorted.get(0).getAttributes().get("testCaseName").contains("Iteration 1"));
        assertTrue(sorted.get(1).getAttributes().get("testCaseName").contains("Iteration 2"));
    }

    @Test
    public void test_hasEqualAttributes() {
        Record m1 = new Record(TestHelper.makeAttributes());
        Record m2 = new Record(TestHelper.makeAttributes());
        assertTrue(m1.hasEqualAttributes(m2));
    }
}
