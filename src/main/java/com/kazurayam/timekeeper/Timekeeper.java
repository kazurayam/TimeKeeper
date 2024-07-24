package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.reporter.CSVReporter;
import com.kazurayam.timekeeper.reporter.MarkdownReporter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public class Timekeeper {

    private final TableList tableList;

    private Timekeeper(Builder builder) {
        this.tableList = builder.tableList;
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

    public void report(Writer writer) throws IOException {
        MarkdownReporter reporter = new MarkdownReporter();
        reporter.report(tableList, ReportOptions.DEFAULT, writer);
    }

    public void report(Writer writer, ReportOptions opts) throws IOException {
        MarkdownReporter reporter = new MarkdownReporter();
        reporter.report(tableList, opts, writer);
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

    public void reportCSV(Writer writer) throws IOException {
        CSVReporter reporter = new CSVReporter();
        reporter.report(tableList, ReportOptions.DEFAULT, writer);
    }

    public void reportCSV(Writer writer, ReportOptions opts)
            throws IOException {
        CSVReporter reporter = new CSVReporter();
        reporter.report(tableList, opts, writer);
    }


    public static class Builder {
        private TableList tableList;

        public Builder() {
            this.tableList = new TableList();
        }

        public Builder table(Table table) {
            tableList.add(table);
            return this;
        }

        public Timekeeper build() {
            return new Timekeeper(this);
        }
    }
}
