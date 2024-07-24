package com.kazurayam.timekeeper_demo

import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.ReportOptions
import com.kazurayam.timekeeper.Table
import com.kazurayam.timekeeper.Timekeeper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TimekeeperDemoReportOptions {

    private static Path outDir_

    @BeforeAll
    static void setupClass() {
        outDir_ = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(TimekeeperDemoReportOptions.class.getSimpleName())
        if (Files.exists(outDir_)) {
            outDir_.toFile().deleteDir();
        }
        Files.createDirectory(outDir_)
    }

    @Test
    void demo_noDescription() {
        Measurement m1 =
                new Measurement.Builder("How long it waited", ["Case"])
                        .build()
        doRecording(m1)
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m1).build())
                .build()
        tk.report(outDir_.resolve("noDescription.md"),
                ReportOptions.NODESCRIPTION)
    }

    @Test
    void demo_noLegend() {
        Measurement m1 =
                new Measurement.Builder("How long it waited", ["Case"])
                        .build()
        doRecording(m1)
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m1).build())
                .build()
        tk.report(outDir_.resolve("noLegend.md"),
                ReportOptions.NOLEGEND);
    }

    @Test
    void demo_noGraph() {
        Measurement m1 =
                new Measurement.Builder("How long it waited", ["Case"])
                        .build()
        doRecording(m1)
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m1).build())
                .build()
        tk.report(outDir_.resolve("noGraph.md"),
                ReportOptions.NOGRAPH);
    }

    @Test
    void demo_the_simplest() {
        Measurement m1 =
                new Measurement.Builder("How long it waited", ["Case"])
                        .build()
        doRecording(m1)
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m1).build())
                .build()
        tk.report(outDir_.resolve("the_simplest.md"),
                ReportOptions.NODESCRIPTION_NOLEGEND_NOGRAPH)
    }

    private static void doRecording(Measurement m1) {
        for (int i in [2, 3, 1]) {
            m1.before(["Case": "sleeping for " + i + " secs"])
            Thread.sleep(i * 1000L)    // do something that takes long time.
            m1.after()
        }
    }
}
