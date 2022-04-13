package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Reporter;
import com.kazurayam.timekeeper.Table;
import com.kazurayam.timekeeper.TableList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public abstract class AbstractReporter implements Reporter {

    public void report(Table table, Path output) throws IOException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(output);
        report(table, output.toFile());
    }

    public void report(Table table, File output) throws IOException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(output);
        Path parent = output.toPath().getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        report(table, new FileOutputStream(output));
    }

    @Override
    public void report(Table table, OutputStream os) throws IOException {
        Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        report(table, writer);
    }

    public void report(Table table, Writer writer) throws IOException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(writer);
        Measurement m = (table.requireSorting()) ?
                table.sorted() : table.getMeasurement();
        Table sortedTable = new Table.Builder(table, m).build();
        this.compileContent(sortedTable, writer);
        writer.flush();
        writer.close();
    }

    public void report(TableList tableList, Path output) throws IOException {
        report(tableList, output.toFile());
    }

    public void report(TableList tableList, File output) throws IOException {
        OutputStream os = new FileOutputStream(output);
        report(tableList, os);
    }

    @Override
    public void report(TableList tableList, OutputStream os) throws IOException {
        Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        report(tableList, writer);
    }

    public void report(TableList tableList, Writer writer) throws IOException {
        Objects.requireNonNull(tableList);
        Objects.requireNonNull(writer);
        for (Table table : tableList) {
            this.compileContent(table, writer);
        }
        writer.close();
    }

    protected abstract void compileContent(Table table, Writer writer) throws IOException;

}
