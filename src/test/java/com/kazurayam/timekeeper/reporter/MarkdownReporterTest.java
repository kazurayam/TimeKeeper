package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.TestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public void test_report_single_Measurement() throws IOException {
        Path caseOutputDir = classOutput.resolve("test_report_single_Measurement");
        Path md = caseOutputDir.resolve("report.md");
        MarkdownReporter reporter = new MarkdownReporter();
        reporter.setOutput(md);
        Measurement measurement = TestHelper.makeMeasurement();
        reporter.report(measurement);
        assertTrue(Files.exists(md), "no output");
        assertTrue(Files.size(md) > 0, "empty output");
    }
}
