package com.kazurayam.timekeeper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimekeeperTest {

    private final Logger logger = LoggerFactory.getLogger(TestHelper.getClassName());

    private static Path classOutput;

    @BeforeAll
    static void beforeAll() throws IOException {
        classOutput = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(TimekeeperTest.class.getSimpleName());
        Files.createDirectories(classOutput);
    }

    @Test
    public void test_Builder() {
        Timekeeper tk = new Timekeeper.Builder().build();
        assertNotNull(tk);
    }

    @Test
    public void test_size() {
        Measurement m = TestHelper.makeMeasurement();
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m).build())
                .build();
        assertEquals(1, tk.size());
        assertNotNull(tk.get(0));
    }

    @Test
    public void test_report_MARKDOWN() throws IOException {
        Path caseOutputDir = classOutput.resolve("test_report_MARKDOWN");
        Files.createDirectories(caseOutputDir);
        Path markdown = caseOutputDir.resolve("report.md");

        Measurement m = TestHelper.makeMeasurement();
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m).build())
                .build();
        tk.report(markdown, ReportOptions.NODESCRIPTION_NOLEGEND);
        assertTrue(Files.exists(markdown));
    }

    @Test
    public void test_report_into_Writer() throws IOException {
        Measurement m = TestHelper.makeMeasurement();
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m).build())
                .build();
        StringWriter sw = new StringWriter();
        tk.report(sw);
        Method method = new Object(){}.getClass().getEnclosingMethod();
        logger.info(String.format("[%s] %s", method.getName(), sw.toString()));
    }

    @Test
    public void test_report_CSV() throws IOException {
        Path caseOutputDir = classOutput.resolve("test_report_CSV");
        Files.createDirectories(caseOutputDir);
        Path csv = caseOutputDir.resolve("report.csv");
        //
        Measurement m = TestHelper.makeMeasurement();
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m).build())
                .build();
        tk.reportCSV(csv, ReportOptions.NODESCRIPTION_NOLEGEND);
        assertTrue(Files.exists(csv));
    }
}
