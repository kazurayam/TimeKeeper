package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.reporter.CSVReporter;
import com.kazurayam.timekeeper.reporter.MarkdownReporter;

import java.io.IOException;
import java.nio.file.Path;

public class Timekeeper {

    private final TableList tableList;

    public Timekeeper() {
        tableList = new TableList();
    }

    public void add(Table table) {
        tableList.add(table);
    }

    public Table get(int index) {
        return tableList.get(index);
    }

    public int size() {
        return tableList.size();
    }

    public void report(Path outputFile) throws IOException {
        this.report(outputFile,
                new ReportOptions.Builder().build());
    }

    public void report(Path outputFile, ReportOptions opts)
            throws IOException {
        MarkdownReporter reporter = new MarkdownReporter();
        reporter.report(tableList, opts, outputFile);
    }

    public void reportCSV(Path outputFile) throws IOException {
        CSVReporter reporter = new CSVReporter();
        reporter.report(tableList, ReportOptions.DEFAULT, outputFile);
    }

    public void reportCSV(Path outputFile, ReportOptions opts)
            throws IOException {
        CSVReporter reporter = new CSVReporter();
        reporter.report(tableList, opts, outputFile);
    }
}
