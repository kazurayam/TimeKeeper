package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.MeasurementList;
import com.kazurayam.timekeeper.Reporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MarkdownReporter implements Reporter {

    private PrintWriter pw_;

    public MarkdownReporter() {
        this.pw_ = new PrintWriter(System.out);
    }

    public void setOutput(Path output) throws IOException {
        Objects.requireNonNull(output);
        Files.createDirectories(output.getParent());
        File outputFile = output.toFile();
        this.pw_ = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(outputFile),
                                StandardCharsets.UTF_8)));
    }

    public void report(MeasurementList mList) {
        mList.forEach(m -> {
            try {
                this.report(m);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void report(Measurement measurement) throws IOException {
        pw_.println(measurement.getId());

        //
        pw_.flush();
        pw_.close();
    }
}
