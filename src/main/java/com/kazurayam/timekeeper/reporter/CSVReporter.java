package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.Reporter;
import com.kazurayam.timekeeper.Table;
import com.kazurayam.timekeeper.TableList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CSVReporter extends AbstractReporter {

    public CSVReporter() {}

    /**
     *
     * @param table
     * @param writer
     */
    protected void compileContent(Table table, Writer writer) throws IOException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(writer);
        PrintWriter pw = new PrintWriter(new BufferedWriter(writer));
        Measurement measurement = table.getMeasurement();

        StringBuilder sb0 = new StringBuilder();
        for (String cn :  measurement.getColumnNames()) {
            if (sb0.length() > 0) {
                sb0.append(",");
            }
            sb0.append(esc(cn));
        }
        if (measurement.hasRecordWithSize()) {
            sb0.append(",");
            sb0.append("size");
        }
        if (measurement.hasRecordWithDuration()) {
            sb0.append(",");
            sb0.append("duration");
            if (table.requireGraph()) {
                sb0.append(",");
                sb0.append("graph");
            }
        }
        pw.println(sb0);
        //
        for (Record record : measurement) {
            StringBuilder sb2 = new StringBuilder();
            for (String colName : measurement.getColumnNames()) {
                if (sb2.length() > 0) {
                    sb2.append(",");
                }
                sb2.append(esc(record.getAttributes().get(colName)));
            }
            if (measurement.hasRecordWithSize()) {
                // print size
                sb2.append(",");
                sb2.append(DataFormatter.formatSize(record.getSize()));
            }
            if (measurement.hasRecordWithDuration()) {
                // print duration
                sb2.append(",");
                sb2.append(DataFormatter.formatDuration(record.getDuration()));
                if (table.requireGraph()) {
                    // print duration graph
                    sb2.append(",");
                    sb2.append(DataFormatter.toGraph(record.getDuration()));
                }
            }
            pw.println(sb2);
        }
        // print a row of Average
        StringBuilder sb3 = new StringBuilder();
        for (int i = 0; i < measurement.getColumnNames().size(); i++) {
            if (sb3.length() > 0) {
                sb3.append(",");
            }
            if (i == 0) {
                sb3.append("Average");
            } else {
                sb3.append("-");
            }
        }
        if (measurement.hasRecordWithSize()) {
            sb3.append(",");
            sb3.append(DataFormatter.formatSize(measurement.getAverageSize()));
        }
        if (measurement.hasRecordWithDuration()) {
            sb3.append(",");
            sb3.append(DataFormatter.formatDuration(measurement.getAverageDuration()));
        }
        pw.println(sb3);
        pw.println();
        //
        pw.println("## " + measurement.getId());
        if (table.requireDescription()) {
            pw.println(table.getDescription());
        }
        pw.println();
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
        pw.println();
        pw.flush();
    }

    public static String esc(String s) {
        if (s.contains(",") || s.contains("\n") || s.contains("\r") || s.contains("\"")) {
            return "\"" + s.replaceAll("\"", "\"\"") + "\"";
        } else {
            return s;
        }
    }
}
