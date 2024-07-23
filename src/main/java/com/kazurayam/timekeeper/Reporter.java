package com.kazurayam.timekeeper;

import java.io.IOException;
import java.io.Writer;

public interface Reporter {

    void report(Table table, ReportOptions opts, Writer writer) throws IOException;

    void report(TableList tableList, ReportOptions opts, Writer writer) throws IOException;

}
