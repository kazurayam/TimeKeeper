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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownReporter implements Reporter {

    private PrintWriter pw_;

    public MarkdownReporter() {
        this.pw_ = new PrintWriter(System.out);
    }

    @Override
    public void setOutput(Path output) throws IOException {
        Objects.requireNonNull(output);
        Files.createDirectories(output.getParent());
        File outputFile = output.toFile();
        this.setOutput(outputFile);

    }

    @Override
    public void setOutput(File outputFile) throws IOException {
        Objects.requireNonNull(outputFile);
        Writer writer = new OutputStreamWriter(
                new FileOutputStream(outputFile), StandardCharsets.UTF_8);
        this.setOutput(writer);
    }

    @Override
    public void setOutput(Writer writer) throws IOException {
        Objects.requireNonNull(writer);
        this.pw_ = new PrintWriter(new BufferedWriter(writer));
    }


    @Override
    public void report(TableList tableList) throws IOException {
        for (Table table : tableList) {
            this.processTable(table);
        }
        pw_.close();
    }

    @Override
    public void report(Table table) throws IOException {
        this.processTable(table);
        pw_.close();
    }

    protected void processTable(Table table) throws IOException {
        Objects.requireNonNull(table);
        Measurement m = (table.requireSorting()) ?
                table.sorted() : table.getMeasurement();
        Table sortedTable = new Table.Builder(table, m).build();
        this.compileReport(sortedTable);
    }

    /**
     * compile the content of report
     */
    private void compileReport(Table table) {
        Objects.requireNonNull(table);
        Measurement measurement = table.getMeasurement();
        pw_.println("## " + measurement.getId());
        pw_.println("");
        if (table.requireDescription()) {
            pw_.println(table.getDescription());
            pw_.println("");
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
        pw_.println(sb0);
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
        pw_.println(sb1);
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
                sb2.append(formatSize(record.getSize()));
                sb2.append("|");
            }
            if (measurement.hasRecordWithDuration()) {
                // print duration
                sb2.append(formatDuration(record.getDuration()));
                sb2.append("|");
                if (table.requireGraph()) {
                    // print duration graph
                    sb2.append("`");
                    sb2.append(getDurationGraph(record.getDuration()));
                    sb2.append("`");
                    sb2.append("|");
                }
            }
            //
            pw_.println(sb2);
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
            sb3.append(formatSize(measurement.getAverageSize()));
            sb3.append("|");
        }
        if (measurement.hasRecordWithDuration()) {
            sb3.append(formatDuration(measurement.getAverageDuration()));
            sb3.append("|");
            if (table.requireGraph()) {
                sb3.append(" |");
            }
        }
        pw_.println(sb3);
        pw_.println("");
        //
        if (table.requireLegend()) {
            if (measurement.hasRecordWithSize()) {
                pw_.println("The unit of size is bytes");
                pw_.println("");
            }
            if (measurement.hasRecordWithDuration()) {
                pw_.println("The format of duration is \"minutes:seconds\"");
                pw_.println("");
                pw_.println("one # represents 10 seconds in the duration graph");
                pw_.println("");
            }
        }
        pw_.flush();
    }

    /**
     * stringify a Duration to "minutes:seconds", or "hours:minutes:seconds"
     *
     * @param duration value in milliseconds to be formatted into "mm:ss"
     * @return 45 seconds will be "00:45"
     */
    public static String formatDuration(Duration duration) {
        int s = (int) duration.toMillis() / 1000;
        if (s >= 60 * 60) {
            return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
        } else {
            return String.format("%02d:%02d", (s % 3600) / 60, (s % 60));
        }
    }

    public static Pattern DURATION_PATTERN =
            Pattern.compile("((\\d+)\\s*:\\s*)?(\\d+)\\s*:\\s*(\\d+)");

    public static Duration parseDuration(String duration) {
        Matcher m = DURATION_PATTERN.matcher(duration);
        if (m.matches()) {
            boolean hasHour = (m.group(2) != null);
            if (hasHour) {
                int hours = Integer.parseInt(m.group(2));
                int minutes = Integer.parseInt(m.group(3));
                int seconds = Integer.parseInt(m.group(4));
                long dur = 60L * 60 * hours + 60L * minutes + seconds;
                return Duration.ofSeconds(dur);
            } else {
                int minutes = Integer.parseInt(m.group(3));
                int seconds = Integer.parseInt(m.group(4));
                long dur = 60L * minutes + seconds;
                return Duration.ofSeconds(dur);
            }
        } else {
            throw new IllegalArgumentException("duration=" + duration +
                    " does not match " + DURATION_PATTERN.toString());
        }
    }

    /**
     * stringify 123456L to "123,456"
     *
     * @param size e.g. 123456L
     * @return e.g "123,456"
     */
    public static String formatSize(long size) {
        return NumberFormat.getIntegerInstance().format(size);
    }

    public static long parseSize(String size) {
        try {
            return (long)NumberFormat.getIntegerInstance().parse(size);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected String getDurationGraph(Duration duration) {
        int tens = (int)(duration.toMillis() / 10000) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= tens; i++) {
            if ((i % 10) == 0) {
                int t = (i / 10) % 10;
                sb.append(Integer.valueOf(t).toString());
            } else {
                sb.append("#");
            }
        }
        return sb.toString();
    }
}
