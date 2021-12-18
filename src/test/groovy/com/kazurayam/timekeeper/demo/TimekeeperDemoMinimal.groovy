package com.kazurayam.timekeeper.demo

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
                ["description m1"]).build()
        tk.add(new Table.Builder(m1).build())
        for (int i in [2, 3, 5, 7]) {
            LocalDateTime beforeSleep = LocalDateTime.now()
            // do a processing that could take long time.
            Thread.sleep(i * 1000L)
            LocalDateTime afterSleep = LocalDateTime.now()
            m1.recordDuration(["description m1": "sleeping for " + i + " secs"],
                    beforeSleep, afterSleep)
        }
        tk.report(outDir_.resolve("planned_sleep.md"))
    }
}
