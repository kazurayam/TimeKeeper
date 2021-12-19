package com.kazurayam.timekeeper;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public interface Reporter {

    void setOutput(Path output) throws IOException;
    void setOutput(File file) throws IOException;
    void setOutput(Writer writer) throws IOException;

    void report(TableList tableList) throws IOException;
    void report(Table table) throws IOException;

}
