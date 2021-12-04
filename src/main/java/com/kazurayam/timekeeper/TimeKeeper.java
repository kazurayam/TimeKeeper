package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.reporter.MarkdownReporter;
import com.kazurayam.timekeeper.reporter.Measurements;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TimeKeeper {

    public static enum FORMAT {
        MARKDOWN
    }

    private Measurements measurements;

    public TimeKeeper() {
        measurements = new Measurements();
    }

    public void add(Measurement m) {
        measurements.add(m);
    }

    public Measurement get(int index) {
        return measurements.get(index);
    }

    public int size() {
        return measurements.size();
    }

    public void write(Path output, FORMAT format) throws IOException {
        switch (format) {
            case MARKDOWN:
                Reporter reporter = new MarkdownReporter(measurements);
                reporter.report(output);
                break;
            default :
                ;
        }
    }
}
