package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.reporter.MarkdownReporter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Timekeeper {

    public enum FORMAT {
        MARKDOWN
    }

    private final MeasurementList mList;

    public Timekeeper() {
        mList = new MeasurementList();
    }

    public Measurement newMeasurement(String id, List<String> columnNames) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(columnNames);
        assert columnNames.size() > 0;
        Measurement m = new Measurement.Builder(id, columnNames).build();
        this.mList.add(m);
        return m;
    }
    public void add(Measurement m) {
        mList.add(m);
    }

    public Measurement get(int index) {
        return mList.get(index);
    }

    public int size() {
        return mList.size();
    }

    public void report(Path outputFile) throws IOException {
        this.report(outputFile, FORMAT.MARKDOWN);
    }

    public void report(Path outputFile, FORMAT format) throws IOException {
        if (format == FORMAT.MARKDOWN) {
            Reporter reporter = new MarkdownReporter();
            reporter.setOutput(outputFile);
            reporter.report(mList);
        }
    }
}
