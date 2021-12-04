package com.kazurayam.timekeeper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;

public interface Reporter {

    public void report(Path output) throws IOException;

}
