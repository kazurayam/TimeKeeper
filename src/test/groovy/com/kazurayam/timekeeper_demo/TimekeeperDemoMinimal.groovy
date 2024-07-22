package com.kazurayam.timekeeper_demo

import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Table
import com.kazurayam.timekeeper.Timekeeper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// This test takes 2 minutes to finish
@Disabled
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

    private static void doRecording(Measurement m1) {
        for (int i in [13, 3, 7]) {
            m1.before(["Case": "sleeping for " + i + " secs"])
            Thread.sleep(i * 1000L)    // do something that takes long time.
            m1.after()
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
