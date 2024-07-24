A sample code that demonstrates how to use the timekeeper library is found

- https://github.com/kazurayam/timekeeper/blob/develop/src/test/java/com/kazurayam/timekeeper/Demonstration.java

[The following Groovy script](https://github.com/kazurayam/timekeeper/blob/develop/src/test/groovy/com/kazurayam/timekeeper_demo/TimekeeperDemoMinimal.groovy) will show you a minimal example to use the timekeeper library:

```
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

// This test takes 1 minutes to finish
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
        Measurement m1 =
                new Measurement.Builder("How long it waited", ["Case"])
                        .build()
        doRecording(m1)
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m1).build())
                .build()
        tk.report(outDir_.resolve("planned_sleep.md"))
    }

    private static void doRecording(Measurement m1) {
        for (int i in [2, 3, 1]) {
            m1.before(["Case": "sleeping for " + i + " secs"])
            Thread.sleep(i * 1000L)    // do something that takes long time.
            m1.after()
        }
    }
}
```

see https://kazurayam.github.io/timekeeper/ for more detail


