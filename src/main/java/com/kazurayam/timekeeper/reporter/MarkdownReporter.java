package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.Reporter;
import com.kazurayam.timekeeper.Table;
import com.kazurayam.timekeeper.TableList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class MarkdownReporter implements Reporter {

    public MarkdownReporter() {}

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



    /**
     * compile the content of report
     */
    void compileContent(Table table, Writer writer) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(writer);
        PrintWriter pw = new PrintWriter(new BufferedWriter(writer));
        Measurement measurement = table.getMeasurement();
        pw.println("## " + measurement.getId());
        pw.println("");
        if (table.requireDescription()) {
            pw.println(table.getDescription());
            pw.println("");
        }
        StringBuilder sb0 = new StringBuilder();
        sb0.append("|");
        List<String> columnNames = measurement.getColumnNames();
        for (String cn : columnNames) {
            sb0.append(cn);
            sb0.append("|");
        }
        if (measurement.hasRecordWithSize()) {
            sb0.append("size|");
        }
        if (measurement.hasRecordWithDuration()) {
            sb0.append("duration|");
            if (table.requireGraph()) {
                sb0.append("graph|");
            }
        }
        pw.println(sb0);
        //
        StringBuilder sb1 = new StringBuilder();
        sb1.append("|");
        for (String cn : columnNames) {
            sb1.append(":----");
            sb1.append("|");
        }
        if (measurement.hasRecordWithSize()) {
            sb1.append("----:|");  // size
        }
        if (measurement.hasRecordWithDuration()) {
            sb1.append("----:|");  // duration
            if (table.requireGraph()) {
                sb1.append(":----|");  // duration graph
            }
        }
        pw.println(sb1);
        //
        //logger.debug("measurement.size()=" + measurement.size());
        for (Record record : measurement) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("|");
            // print attributes
            for (String colName : measurement.getColumnNames()) {
                sb2.append(record.getAttributes().get(colName));
                sb2.append("|");
            }
            if (measurement.hasRecordWithSize()) {
                // print size
                sb2.append(DataFormatter.formatSize(record.getSize()));
                sb2.append("|");
            }
            if (measurement.hasRecordWithDuration()) {
                // print duration
                sb2.append(DataFormatter.formatDuration(record.getDuration()));
                sb2.append("|");
                if (table.requireGraph()) {
                    // print duration graph
                    sb2.append("`");
                    sb2.append(DataFormatter.toGraph(record.getDuration()));
                    sb2.append("`");
                    sb2.append("|");
                }
            }
            //
            pw.println(sb2);
        }
        // print a row of Average
        StringBuilder sb3 = new StringBuilder();
        sb3.append("|");
        for (int i = 0; i < measurement.getColumnNames().size(); i++) {
            if (i == 0) {
                sb3.append("Average");
            } else {
                sb3.append("-");
            }
            sb3.append("|");
        }
        if (measurement.hasRecordWithSize()) {
            sb3.append(DataFormatter.formatSize(measurement.getAverageSize()));
            sb3.append("|");
        }
        if (measurement.hasRecordWithDuration()) {
            sb3.append(DataFormatter.formatDuration(measurement.getAverageDuration()));
            sb3.append("|");
            if (table.requireGraph()) {
                sb3.append(" |");
            }
        }
        pw.println(sb3);
        pw.println("");
        //
        if (table.requireLegend()) {
            if (measurement.hasRecordWithSize()) {
                pw.println("The unit of size is bytes");
                pw.println("");
            }
            if (measurement.hasRecordWithDuration()) {
                pw.println("The format of duration is \"minutes:seconds\"");
                pw.println("");
                pw.println("one # represents 10 seconds in the duration graph");
                pw.println("");
            }
        }
        pw.flush();
    }
}
