package com.kazurayam.timekeeper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
    public void test_constructor() {
        Timekeeper tk = new Timekeeper();
        assertNotNull(tk);
    }

    @Test
    public void test_void_add_get_size() {
        Timekeeper tk = new Timekeeper();
        Measurement m = TestHelper.makeMeasurement();
        tk.add(m);
        Measurement result = tk.get(0);
        assertNotNull(result);
        assertEquals(1, tk.size());
    }

    @Test
    public void test_report() throws IOException {
        Path caseOutputDir = classOutput.resolve("test_report");
        Path markdown = caseOutputDir.resolve("report.md");
        Timekeeper tk = new Timekeeper();
        Measurement m = TestHelper.makeMeasurement();
        tk.add(m);
        tk.report(markdown, Timekeeper.FORMAT.MARKDOWN);
        assertTrue(Files.exists(markdown));
    }

}
