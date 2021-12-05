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
        sb0.append("duration|");
        sb0.append("duration graph|");
        pw_.println(sb0.toString());
        //
        StringBuilder sb1 = new StringBuilder();
        sb1.append("|");
        for (String cn : columnNames) {
            sb1.append(":----");
            sb1.append("|");
        }
        sb1.append("----:|");
        sb1.append(":----|");
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
            // print duration
            int s = (int)record.getDurationMillis() / 1000;
            String formattedDuration;
            if (s >= 60 * 60) {
                formattedDuration = String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
            } else {
                formattedDuration = String.format("%02d:%02d", (s % 3600) / 60, (s % 60));
            }
            sb2.append(formattedDuration);
            sb2.append("|");
            // print duration graph
            sb2.append("`" + getDurationGraph(record.getDuration()) + "`");
            sb2.append("|");
            //
            pw_.println(sb2.toString());
        }
        //
        pw_.println("");
        //pw_.println("The format of duration is \"minutes:seconds\"");
        pw_.println("----");
        pw_.flush();
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
