package com.kazurayam.timekeeper.reporter;

import com.kazurayam.timekeeper.Helper;
import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.MeasurementList;
import com.kazurayam.timekeeper.Record;
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
import java.text.NumberFormat;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(Helper.getClassName());

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
        //
        pw_.close();
    }

    public void report(Measurement measurement) throws IOException {
        pw_.println("## " + measurement.getId());
        pw_.println("");
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
            sb0.append("graph|");
        }
        pw_.println(sb0.toString());
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
            sb1.append(":----|");  // duration graph
        }
        pw_.println(sb1.toString());
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
                sb2.append(NumberFormat.getIntegerInstance().format(record.getSize()));
                sb2.append("|");
            }
            if (measurement.hasRecordWithDuration()) {
                // print duration
                sb2.append(formatDuration(record.getDuration()));
                sb2.append("|");
                // print duration graph
                sb2.append("`" + getDurationGraph(record.getDuration()) + "`");
                sb2.append("|");
            }
            //
            pw_.println(sb2.toString());
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
            sb3.append(NumberFormat.getIntegerInstance().format(measurement.getAverageSize()));
            sb3.append("|");
        }
        if (measurement.hasRecordWithDuration()) {
            sb3.append(formatDuration(measurement.getAverageDuration()));
            sb3.append("| |");
        }
        pw_.println(sb3.toString());
        //
        pw_.println("");
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
        pw_.println("----");
        pw_.flush();
    }

    /**
     * to "minutes:seconds", or "hours:minutes:secodns"
     *
     * @param duration value in milliseconds to be formated into "mm:ss"
     * @return 45 seconds will be "00:45"
     */
    protected String formatDuration(Duration duration) {
        int s = (int) duration.toMillis() / 1000;
        if (s >= 60 * 60) {
            return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
        } else {
            return String.format("%02d:%02d", (s % 3600) / 60, (s % 60));
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
