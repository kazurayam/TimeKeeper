package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RecordTest {

    private final transient Logger logger = LoggerFactory.getLogger(TestHelper.getClassName());

    @Test
    public void testLogger() {
        logger.trace("Hello Trace");
        logger.debug("Hello Debug");
        logger.info("Hello Info");
        logger.warn("Hello Warn");
        logger.error("Hello Error");
    }

    @Test
    public void test_Builder_attributes() {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("case", "Y1");
        m.put("Suite", "TS2");
        m.put("Step Execution Log", "Enabled");
        m.put("Log Viewer", "Attached");
        m.put("Mode", "Tree");
        Record.Builder b = new Record.Builder();
        b.attributes(m);
        Record r = b.build();
        assertNotNull(r);
        assertEquals("Y1", r.getAttributes().get("case"));
    }

    @Test
    public void test_getDurationMillis() throws Exception {
        Record m = TestHelper.makeRecord1();
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
    public void test_toJson() throws Exception {
        Record record = TestHelper.makeRecord1();
        String json = record.toJson();
        //System.out.println(json);
        assertNotNull(json);
    }

    @Test
    public void test_toPrettyJson() throws Exception {
        Record record = TestHelper.makeRecord1();
        String json = record.toPrettyJson();
        //System.out.println(json);
        assertNotNull(json);
    }

    @Test
    public void test_toString() throws Exception {
        Record record = TestHelper.makeRecord1();
        record.setStartAt(LocalDateTime.now());
        Thread.sleep(1000);
        record.setEndAt(LocalDateTime.now());
        String s = record.toString();
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        //logger.debug(methodName + " " + s);
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
        list.add(TestHelper.makeRecord2());
        list.add(TestHelper.makeRecord1());
        List<Record> sorted = list.stream().sorted().collect(Collectors.toList());
        // sorted is "Iteration 1" -> "Iteration 2"
        assertTrue(sorted.get(0).getAttributes().get("testCaseName").contains("Iteration 1"));
        assertTrue(sorted.get(1).getAttributes().get("testCaseName").contains("Iteration 2"));
    }

    @Test
    public void test_hasEqualAttributes() {
        Record mX = TestHelper.makeRecord1();
        Record mY = TestHelper.makeRecord1();
        assertTrue(mX.hasEqualAttributes(mY));
    }

    @Test
    public void test_DURATION_PATTERN() {
        Matcher m0 = Record.DURATION_PATTERN.matcher(" 0 ");
        assertTrue(m0.matches(), "\" 0 \" does not match");
        Matcher m1 = Record.DURATION_PATTERN.matcher("1");
        assertTrue(m1.matches(), "1 does not match");
        Matcher m23 = Record.DURATION_PATTERN.matcher("2:3");
        assertTrue(m23.matches(), "2:3 does not match");
        Matcher m456 = Record.DURATION_PATTERN.matcher("4 : 56");
        assertTrue(m456.matches(), "\"4 : 56\" does not match");
    }

    @Test
    public void test_parseDurationString() {
        int result = Record.parseDurationString("10 : 11");
        assertEquals(611, result);
    }


    @Test
    public void test_setDuration_string() {
        Record r = TestHelper.makeRecord1();
        r.setDuration("1 : 1");   // 1 minutes 1 seconds = 61 seconds
        assertEquals(61000L, r.getDurationMillis());
        //
        r.setDuration("10 : 1");   // 10 minutes 1 seconds = 601 seconds
        assertEquals(601000L, r.getDurationMillis());
        //
        r.setDuration("10 : 11");   // 10 minutes 1 seconds = 601 seconds
        assertEquals(611000L, r.getDurationMillis());
    }
}
