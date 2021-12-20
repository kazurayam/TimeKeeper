package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.reporter.MarkdownReporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TestHelper {

    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

    public static Record makeRecord1() {
        Record.Builder builder = new Record.Builder();
        builder.attr("testCaseName", "Test Cases/printID - Iteration 1");
        builder.attr("testCaseId", "Test Cases/printID");
        builder.attr("ID", "\u0027#0000\u0027");
        return builder.build();
    }

    public static Record makeRecord2() {
        Record.Builder builder = new Record.Builder();
        builder.attr("testCaseName", "Test Cases/printID - Iteration 2");
        builder.attr("testCaseId", "Test Cases/printID");
        builder.attr("ID", "\u0027#0001\u0027");
        return builder.build();
    }

    public static List<String> getColumnNames() {
        return Arrays.asList("case", "Suite", "Step Execution Log", "Log Viewer", "Mode");
    }

    public static Measurement makeMeasurement() {
        Measurement m = new Measurement.Builder("M1",
                getColumnNames()).build();
        //
        Record y1 = m.newRecord();
        y1.putAttribute("case", "Y1");
        y1.putAttribute("Suite", "TS2");
        y1.putAttribute("Step Execution Log", "Enabled");
        y1.putAttribute("Log Viewer", "Attached");
        y1.putAttribute("Mode", "Tree");
        y1.setDuration("25:17");
        m.add(y1);
        //
        Record y2 = m.newRecord();
        y2.putAttribute("case", "Y2");
        y2.putAttribute("Suite", "TS2");
        y2.putAttribute("Step Execution Log", "Disabled");
        y2.putAttribute("Log Viewer", "Attached");
        y2.putAttribute("Mode", "Tree");
        y2.setDuration("6:11");
        m.add(y2);
        //
        Record y3 = m.newRecord();
        y3.putAttribute("case", "Y3");
        y3.putAttribute("Suite", "TS2");
        y3.putAttribute("Step Execution Log", "Disabled");
        y3.putAttribute("Log Viewer", "Closed");
        y3.putAttribute("Mode", "-");
        y3.setDuration("43");
        m.add(y3);
        //
        return m;
    }


    public static Measurement makeMeasurementOf3lines() throws IOException {
        Path fixtureFile = Paths.get(".").resolve("src/test/fixtures/measurement_3lines.md");
        Measurement m = new Measurement.Builder("foo", Arrays.asList("URL")).build();
        List<Record> records = readMd(fixtureFile);
        for (Record r : records) {
            m.add(r);
        }
        return m;
    }

    public static List<Record> readMd(Path fixtureFile) throws IOException {
        List<Record> list = new ArrayList<>();
        Stream<String> stream = Files.lines(fixtureFile);
        stream.forEach( line -> {
            List<String> items = Arrays.asList(line.split("\\|"));
                /*
                for (int i = 1; i < items.size(); i++) {
                    System.out.println(i + " " + items.get(i));
                }
                ->
                0
                1 https://example.com/
                2 736,042
                3 00:38
                4 `####`
                 */
            Record r = new Record.Builder().attr("URL", items.get(1)).build();
            r.setSize(MarkdownReporter.parseSize(items.get(2)));
            r.setDuration(MarkdownReporter.parseDuration(items.get(3)));
            list.add(r);
        });
        return list;
    }
}
