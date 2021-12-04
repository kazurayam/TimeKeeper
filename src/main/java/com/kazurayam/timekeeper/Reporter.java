package com.kazurayam.timekeeper;

import java.io.IOException;
import java.nio.file.Path;

public interface Reporter {

    public void setOutput(Path output) throws IOException;

    public void report(MeasurementList measurements);

    public void report(Measurement measurement) throws IOException;

}
