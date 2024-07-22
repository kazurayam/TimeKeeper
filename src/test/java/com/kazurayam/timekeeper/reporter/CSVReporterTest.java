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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVReporterTest {

    private final Logger logger = LoggerFactory.getLogger(CSVReporterTest.class.getName());

    private static Path outputDir;

    @BeforeAll
    static void beforeAll() throws IOException {
        outputDir = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(CSVReporterTest.class.getSimpleName());
        Files.createDirectories(outputDir);
    }

    @Test
    public void test_esc() {
        assertEquals(" a b ", CSVReporter.esc(" a b "));
        assertEquals("\"a,b\"", CSVReporter.esc("a,b"));
        assertEquals("\"a\nb\"", CSVReporter.esc("a\nb"));
        assertEquals("\"a\"\"b\"", CSVReporter.esc("a\"b"));
    }

    @Test
    public void test_report_a_Table_to_Writer() throws IOException {
        StringWriter sw = new StringWriter();
        CSVReporter reporter = new CSVReporter();
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        reporter.report(table, ReportOptions.DEFAULT, sw);
        String content = sw.toString();
        logger.info(content);
        assertTrue(content.length() > 0);
        assertTrue(content.contains("## M1"));
    }

    @Test
    public void test_report_a_Table_to_Path() throws IOException {
        Path out = outputDir.resolve("test_report_a_Table_to_Path.csv");
        CSVReporter reporter = new CSVReporter();
        Measurement measurement = TestHelper.makeMeasurement();
        Table table = new Table.Builder(measurement).build();
        reporter.report(table, ReportOptions.DEFAULT, out);
        assertTrue(Files.exists(out));
        assertTrue(out.toFile().length() > 0);
    }

}
