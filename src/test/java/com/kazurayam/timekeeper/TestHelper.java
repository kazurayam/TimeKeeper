package com.kazurayam.timekeeper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
        Measurement m = new Measurement("M1",
                getColumnNames());
        //
        Record y1 = m.formRecord();
        y1.putAttribute("case", "Y1");
        y1.putAttribute("Suite", "TS2");
        y1.putAttribute("Step Execution Log", "Enabled");
        y1.putAttribute("Log Viewer", "Attached");
        y1.putAttribute("Mode", "Tree");
        y1.setDuration("25:17");
        m.add(y1);
        //
        Record y2 = m.formRecord();
        y2.putAttribute("case", "Y2");
        y2.putAttribute("Suite", "TS2");
        y2.putAttribute("Step Execution Log", "Disabled");
        y2.putAttribute("Log Viewer", "Attached");
        y2.putAttribute("Mode", "Tree");
        y2.setDuration("6:11");
        m.add(y2);
        //
        Record y3 = m.formRecord();
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

}
