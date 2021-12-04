package com.kazurayam.timekeeper;

import java.nio.file.Path;

public interface Reporter {

    public void report(Path output);

}
