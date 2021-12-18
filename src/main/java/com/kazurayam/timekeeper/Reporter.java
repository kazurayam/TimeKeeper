package com.kazurayam.timekeeper;

import java.io.IOException;
import java.nio.file.Path;

public interface Reporter {

    void setOutput(Path output) throws IOException;

    void report(TableList tableList);

    void report(Table table) throws IOException;

}
