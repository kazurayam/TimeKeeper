package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.ReportOptions;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkdownReporterTest {

    private final Logger logger = LoggerFactory.getLogger(MarkdownReporterTest.class.getName());

    private static Path outputDir;

    @BeforeAll
    static void beforeAll() throws IOException {
        outputDir = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(MarkdownReporterTest.class.getSimpleName());
        Files.createDirectories(outputDir);
    }

    @Test
    public void test_report_a_Table_to_Path() throws IOException {
        Path caseOutputDir = outputDir.resolve("test_report_a_Table_to_Path");
        Path md = caseOutputDir.resolve("report.md");
        MarkdownReporter reporter = new MarkdownReporter();
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        //logger.debug("measurement.size() is " + measurement.size());
        reporter.report(table, ReportOptions.DEFAULT, md);
        assertTrue(Files.exists(md), "no output");
        assertTrue(Files.size(md) > 0, "empty output");
    }

    @Test
    public void test_report_a_Table_to_Writer() throws IOException {
        StringWriter sw = new StringWriter();
        MarkdownReporter reporter = new MarkdownReporter();
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        reporter.report(table, ReportOptions.DEFAULT, sw);
        String content = sw.toString();
        //logger.debug(content);
        assertTrue(content.length() > 0);
        assertTrue(content.contains("## M1"));
    }

    @Test
    public void test_noDescription() throws IOException {
        StringWriter sw = new StringWriter();
        MarkdownReporter reporter = new MarkdownReporter();
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        ReportOptions opts = new ReportOptions.Builder().noDescription().build();
        reporter.report(table, opts, sw);
        String content = sw.toString();
        //logger.debug(content);
        assertFalse(content.contains("as events flowed"));
    }

    @Test
    public void test_noLegend() throws IOException {
        StringWriter sw = new StringWriter();
        MarkdownReporter reporter = new MarkdownReporter();
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        ReportOptions opts = new ReportOptions.Builder().noLegend().build();
        reporter.report(table, opts, sw);
        String content = sw.toString();
        //logger.debug(content);
        assertFalse(content.contains("minutes:seconds"));
    }

    @Test
    public void test_noGraph() throws IOException {
        StringWriter sw = new StringWriter();
        MarkdownReporter reporter = new MarkdownReporter();
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        ReportOptions opts = new ReportOptions.Builder().noGraph().build();
        reporter.report(table, opts, sw);
        String content = sw.toString();
        //logger.debug(content);
        assertFalse(content.contains("|graph|"));
        assertFalse(content.contains("`###"));
    }
}
