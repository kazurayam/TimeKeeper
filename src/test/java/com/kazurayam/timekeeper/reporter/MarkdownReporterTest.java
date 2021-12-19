package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Table;
import com.kazurayam.timekeeper.TestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkdownReporterTest {

    private final Logger logger = LoggerFactory.getLogger(TestHelper.getClassName());

    private static Path classOutput;

    @BeforeAll
    static void beforeAll() throws IOException {
        classOutput = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(MarkdownReporterTest.class.getSimpleName());
        Files.createDirectories(classOutput);
    }

    @Test
    public void test_report_a_Table_to_Path() throws IOException {
        Path caseOutputDir = classOutput.resolve("test_report_a_Table_to_Path");
        Path md = caseOutputDir.resolve("report.md");
        MarkdownReporter reporter = new MarkdownReporter();
        reporter.setOutput(md);
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        //logger.debug("measurement.size() is " + measurement.size());
        reporter.report(table);
        assertTrue(Files.exists(md), "no output");
        assertTrue(Files.size(md) > 0, "empty output");
    }


    @Test
    public void test_formatDuration_mmss() {
        LocalDateTime startInclusive = LocalDateTime.of(
                2021, 12, 16, 21, 10, 00);
        LocalDateTime endExclusive   = LocalDateTime.of(
                2021, 12, 16, 21, 11, 23);
        Duration dur = Duration.between(startInclusive, endExclusive);
        assertEquals("01:23", MarkdownReporter.formatDuration(dur));
    }

    @Test
    public void test_formatDuration_hhmmss() {
        LocalDateTime startInclusive = LocalDateTime.of(
                2021, 12, 16, 21, 10, 00);
        LocalDateTime endExclusive   = LocalDateTime.of(
                2021, 12, 16, 22, 33, 45);
        Duration dur = Duration.between(startInclusive, endExclusive);
        assertEquals("1:23:45", MarkdownReporter.formatDuration(dur));
    }

    @Test
    public void test_formatNumber() {
        assertEquals("123,456", MarkdownReporter.formatSize(123456L));
    }

    @Test
    public void test_DURATION_PATTERN() {
        Matcher m1 = MarkdownReporter.DURATION_PATTERN.matcher("01:23");
        assertTrue(m1.matches());
        Matcher m2 = MarkdownReporter.DURATION_PATTERN.matcher("1:23:45");
        assertTrue(m2.matches());
        for (int i = 0; i <= m2.groupCount(); i++) {
            System.out.println(i + " " + m2.group(i));
        }
    }

    @Test
    public void test_parseDuration_mmss() {
        String mmss = "01:23";
        Duration dur = MarkdownReporter.parseDuration(mmss);
        assertEquals(83, dur.getSeconds());
    }

    @Test
    public void test_parseDuration_hhmmss() {
        String hhmmss = "1:23:45";
        Duration dur = MarkdownReporter.parseDuration(hhmmss);
        assertEquals(60 * 60 * 1 + 60 * 23 + 45, dur.getSeconds());
    }

    @Test
    public void test_parseSize() throws ParseException {
        assertEquals(123456L, MarkdownReporter.parseSize("123,456"));
    }

    @Test
    public void test_report_a_Table_to_Writer() throws IOException {
        StringWriter sw = new StringWriter();
        MarkdownReporter reporter = new MarkdownReporter();
        reporter.setOutput(sw);
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        reporter.report(table);
        String content = sw.toString();
        //logger.debug(content);
        assertTrue(content.length() > 0);
        assertTrue(content.contains("## M1"));
    }

    @Test
    public void test_noDescription() throws IOException {
        StringWriter sw = new StringWriter();
        MarkdownReporter reporter = new MarkdownReporter();
        reporter.setOutput(sw);
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).noDescription().build();
        reporter.report(table);
        String content = sw.toString();
        //logger.debug(content);
        assertFalse(content.contains("as events flowed"));
    }

}
