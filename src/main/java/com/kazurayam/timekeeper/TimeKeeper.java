package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.reporter.MarkdownReporter;

import java.io.IOException;
import java.nio.file.Path;

public class TimeKeeper {

    public static enum FORMAT {
        MARKDOWN
    }

    private MeasurementList mList;

    public TimeKeeper() {
        mList = new MeasurementList();
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
