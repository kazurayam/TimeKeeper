package com.kazurayam.timekeeper.reporter;

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

public class MarkdownReporter implements Reporter {

    private Measurements measurements;

    public MarkdownReporter(Measurements measurements) {
        this.measurements = measurements;
    }

    public void report(Path output) throws IOException {
        Files.createDirectories(output);
        File outputFile = output.toFile();
        PrintWriter pw = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(outputFile),
                                StandardCharsets.UTF_8)));
        measurements.forEach( m -> {
            pw.println("Hello" + m.getId());
        });
    }
}
