package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.reporter.CSVReporter;
import com.kazurayam.timekeeper.reporter.MarkdownReporter;

import java.io.IOException;
import java.nio.file.Path;

public class Timekeeper {

    public enum FORMAT {
        MARKDOWN,
        CSV
    }

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
        this.report(outputFile, FORMAT.MARKDOWN);
    }

    public void report(Path outputFile, FORMAT format) throws IOException {
        if (format == FORMAT.MARKDOWN) {
            MarkdownReporter reporter = new MarkdownReporter();
            reporter.report(tableList, outputFile);
        } else if (format == FORMAT.CSV) {
            CSVReporter reporter = new CSVReporter();
            reporter.report(tableList, outputFile);
        } else {
            throw new IllegalArgumentException(
                    String.format("%s is not supported", format.toString()));
        }
    }
}
