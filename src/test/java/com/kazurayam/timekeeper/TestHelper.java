package com.kazurayam.timekeeper;

import java.util.TreeMap;

public class TestHelper {

    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

    static Record makeRecord1() {
        Record.Builder builder = new Record.Builder();
        builder.attr("testCaseName", "Test Cases/printID - Iteration 1");
        builder.attr("testCaseId", "Test Cases/printID");
        builder.attr("ID", "\u0027#0000\u0027");
        return builder.build();
    }

    static Record makeRecord2() {
        Record.Builder builder = new Record.Builder();
        builder.attr("testCaseName", "Test Cases/printID - Iteration 2");
        builder.attr("testCaseId", "Test Cases/printID");
        builder.attr("ID", "\u0027#0001\u0027");
        return builder.build();
    }

}
