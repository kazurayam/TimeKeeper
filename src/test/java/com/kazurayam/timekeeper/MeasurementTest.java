package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.TreeMap;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class MeasurementTest {

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
        Measurement m = new Measurement(attrs);
        assertNotNull(m);
    }

    @Test
    public void test_getDurationMillis() throws Exception {
        TreeMap<String, String> attrs = TestHelper.makeAttributes();
        Measurement m = new Measurement(attrs);
        m.setStartAt(LocalDateTime.now());
        Thread.sleep(1000);
        m.setEndAt(LocalDateTime.now());
        long durationMillis = m.getDurationMillis();
        assertTrue(durationMillis >= 1000);
        assertTrue(durationMillis < 1100);
        //
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        logger.debug(methodName + " startAt " + m.getStartAt().format(Measurement.FORMAT));
        logger.debug(methodName + " endAt   " + m.getEndAt().format(Measurement.FORMAT));
        logger.debug(methodName + " durationMillis " + durationMillis);
    }

    @Test
    public void test_toString() throws Exception {
        TreeMap<String, String> attrs = TestHelper.makeAttributes();
        Measurement m = new Measurement(attrs);
        m.setStartAt(LocalDateTime.now());
        Thread.sleep(1000);
        m.setEndAt(LocalDateTime.now());
        String s = m.toString();
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        logger.debug(methodName + " " + s);
        Gson gson = new Gson();
        try {
            JsonObject jo = gson.fromJson(s, JsonObject.class);
        } catch (Exception e) {
            fail(e.getClass().getSimpleName() + " was thrown");
        }
    }
}
