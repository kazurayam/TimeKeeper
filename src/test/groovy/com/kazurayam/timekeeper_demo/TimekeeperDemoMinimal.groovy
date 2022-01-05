package com.kazurayam.timekeeper_demo

import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Table
import com.kazurayam.timekeeper.Timekeeper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

class TimekeeperDemoMinimal {

    private static Path outDir_

    @BeforeAll
    static void setupClass() {
        outDir_ = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(TimekeeperDemoMinimal.class.getSimpleName())
        if (Files.exists(outDir_)) {
            outDir_.toFile().deleteDir();
        }
        Files.createDirectory(outDir_)
    }

    @Test
    void demo_planned_sleep() {
        Timekeeper tk = new Timekeeper()
        Measurement m1 = new Measurement.Builder("How long it waited",
                ["Case"]).build()
        tk.add(new Table.Builder(m1).build())
        doRecording(m1)
        tk.report(outDir_.resolve("planned_sleep.md"))
    }

    private void doRecording(Measurement m1) {
        for (int i in [13, 3, 7]) {
            LocalDateTime beforeSleep = LocalDateTime.now()
            // do a processing that could take long time.
            Thread.sleep(i * 1000L)
            LocalDateTime afterSleep = LocalDateTime.now()
            m1.recordDuration(["Case": "sleeping for " + i + " secs"],
                    beforeSleep, afterSleep)
            m1.getLast().getDurationMillis() < 20 * 1000
        }
    }

    @Test
    void demo_noDescription() {
        Timekeeper tk = new Timekeeper()
        Measurement m1 = new Measurement.Builder("How long it waited", ["Case"]).build()
        tk.add(new Table.Builder(m1)
                .noDescription()   // require no description
                .build())
        doRecording(m1)
        tk.report(outDir_.resolve("noDescription.md"))
    }

    @Test
    void demo_noLegend() {
        Timekeeper tk = new Timekeeper()
        Measurement m1 = new Measurement.Builder("How long it waited", ["Case"]).build()
        tk.add(new Table.Builder(m1)
                .noLegend()   // require no legend for the table
                .build())
        doRecording(m1)
        tk.report(outDir_.resolve("noLegend.md"))
    }

    @Test
    void demo_noGraph() {
        Timekeeper tk = new Timekeeper()
        Measurement m1 = new Measurement.Builder("How long it waited", ["Case"]).build()
        tk.add(new Table.Builder(m1)
                .noGraph()   // require no duration graph
                .build())
        doRecording(m1)
        tk.report(outDir_.resolve("noGraph.md"))
    }

    @Test
    void demo_the_simplest() {
        Timekeeper tk = new Timekeeper()
        Measurement m1 = new Measurement.Builder("How long it waited", ["Case"]).build()
        tk.add(new Table.Builder(m1)
                .noDescription()  // require no description
                .noLegend()       // require no legend
                .noGraph()        // require no duration graph
                .build())
        doRecording(m1)
        tk.report(outDir_.resolve("the_simplest.md"))
    }
}
