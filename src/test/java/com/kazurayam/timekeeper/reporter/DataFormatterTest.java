package com.kazurayam.timekeeper.reporter;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFormatterTest {

    @Test
    public void test_formatDuration_mmss() {
        LocalDateTime startInclusive = LocalDateTime.of(
                2021, 12, 16, 21, 10, 00);
        LocalDateTime endExclusive   = LocalDateTime.of(
                2021, 12, 16, 21, 11, 23);
        Duration dur = Duration.between(startInclusive, endExclusive);
        assertEquals("01:23", DataFormatter.formatDuration(dur));
    }

    @Test
    public void test_formatDuration_hhmmss() {
        LocalDateTime startInclusive = LocalDateTime.of(
                2021, 12, 16, 21, 10, 00);
        LocalDateTime endExclusive   = LocalDateTime.of(
                2021, 12, 16, 22, 33, 45);
        Duration dur = Duration.between(startInclusive, endExclusive);
        assertEquals("1:23:45", DataFormatter.formatDuration(dur));
    }

    @Test
    public void test_formatNumber() {
        assertEquals("123,456", DataFormatter.formatSize(123456L));
    }

}
