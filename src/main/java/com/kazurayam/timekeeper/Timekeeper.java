package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.reporter.MarkdownReporter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Timekeeper {

    public static enum FORMAT {
        MARKDOWN
    }

    private MeasurementList mList;

    public Timekeeper() {
        mList = new MeasurementList();
    }

    public Measurement newMeasurement(String id, List<String> columnNames) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(columnNames);
        assert columnNames.size() > 0;
        Measurement m = new Measurement(id, columnNames);
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

    public void report(Path output, FORMAT format) throws IOException {
        switch (format) {
            case MARKDOWN:
                Reporter reporter = new MarkdownReporter();
                reporter.setOutput(output);
                reporter.report(mList);
                break;
            default :
                ;
        }
    }
}
