package com.kazurayam.timekeeper.reporter;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.regex.Matcher;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataParserTest {

    @Test
    public void test_DURATION_PATTERN() {
        Matcher m1 = DataParser.DURATION_PATTERN.matcher("01:23");
        assertTrue(m1.matches());
        Matcher m2 = DataParser.DURATION_PATTERN.matcher("1:23:45");
        assertTrue(m2.matches());
        for (int i = 0; i <= m2.groupCount(); i++) {
            System.out.println(i + " " + m2.group(i));
        }
    }

    @Test
    public void test_parseDuration_mmss() {
        String mmss = "01:23";
        Duration dur = DataParser.parseDuration(mmss);
        assertEquals(83, dur.getSeconds());
    }

    @Test
    public void test_parseDuration_hhmmss() {
        String hhmmss = "1:23:45";
        Duration dur = DataParser.parseDuration(hhmmss);
        assertEquals(60 * 60 * 1 + 60 * 23 + 45, dur.getSeconds());
    }

    @Test
    public void test_parseSize() throws ParseException {
        assertEquals(123456L, DataParser.parseSize("123,456"));
    }




}
