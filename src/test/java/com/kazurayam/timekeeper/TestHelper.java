package com.kazurayam.timekeeper;

import java.util.TreeMap;

public class TestHelper {

    static TreeMap makeAttributes() {
        TreeMap<String, String> attrs = new TreeMap();
        attrs.put("testCaseName", "Test Cases/printID - Iteration 1");
        attrs.put("testCaseId", "Test Cases/printID");
        attrs.put("ID", "\u0027#0000\u0027");
        return attrs;
    }
}
