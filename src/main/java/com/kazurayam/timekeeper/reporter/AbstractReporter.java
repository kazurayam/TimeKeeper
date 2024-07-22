package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.ReportOptions;
import com.kazurayam.timekeeper.Reporter;
import com.kazurayam.timekeeper.Table;
import com.kazurayam.timekeeper.TableList;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public abstract class AbstractReporter implements Reporter {

    public void report(Table table, ReportOptions opts, File output) throws IOException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(opts);
        Objects.requireNonNull(output);
        report(table, opts, output.toPath());
    }

    public void report(Table table, ReportOptions opts, Path output) throws IOException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(opts);
        Objects.requireNonNull(output);
        Path parent = output.getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        report(table, opts, Files.newOutputStream(output));
    }

    @Override
    public void report(Table table, ReportOptions opts, OutputStream os) throws IOException {
        Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        report(table, opts, writer);
    }

    public void report(Table table, ReportOptions opts, Writer writer) throws IOException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(opts);
        Objects.requireNonNull(writer);
        Measurement m = (table.requireSorting()) ?
                table.sorted() : table.getMeasurement();
        Table sortedTable = new Table.Builder(table, m).build();
        this.compileContent(sortedTable, opts, writer);
        writer.flush();
        writer.close();
    }

    public void report(TableList tableList, ReportOptions opts, File output) throws IOException {
        report(tableList, opts, output.toPath());
    }

    public void report(TableList tableList, ReportOptions opts, Path output) throws IOException {
        OutputStream os = Files.newOutputStream(output);
        report(tableList, opts, os);
    }

    @Override
    public void report(TableList tableList, ReportOptions opts, OutputStream os) throws IOException {
        Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        report(tableList, opts, writer);
    }

    public void report(TableList tableList, ReportOptions opts, Writer writer) throws IOException {
        Objects.requireNonNull(tableList);
        Objects.requireNonNull(writer);
        for (Table table : tableList) {
            this.compileContent(table, opts, writer);
        }
        writer.close();
    }

    protected abstract void compileContent(Table table, ReportOptions opts, Writer writer) throws IOException;

}
