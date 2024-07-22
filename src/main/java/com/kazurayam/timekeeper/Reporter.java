package com.kazurayam.timekeeper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;

public interface Reporter {

    void report(Table table, ReportOptions opts, OutputStream outputStream) throws IOException;

    void report(TableList tableList, ReportOptions opts, OutputStream outputStream) throws IOException;

}
