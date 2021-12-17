package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordComparatorByDurationTest {

    /**
     * {"attributes":{"URL":"https://www.kazurayam.com/web/"},"size":1985644,    "durationMillis":67000}
     * {"attributes":{"URL":"https://www.kazurayam.com/"},"size":736042,         "durationMillis":38000}
     * {"attributes":{"URL":"https://www.kazurayam.com/mobile/"},"size":1613751, "durationMillis":11000}
     */
    private List<Record> records;

    @BeforeEach
    public void setup() throws IOException {
        Path fixtureFile = Paths.get(".").resolve("src/test/fixtures/measurement_3lines.md");
        records = TestHelper.readMd(fixtureFile);
    }

    @Test
    public void test_smoke() {
        RecordComparatorByDuration comparator = new RecordComparatorByDuration();
        assertEquals(1, comparator.compare(records.get(0), records.get(1)),
                String.format("expected: > for [0]:%d, [1]:%d but not",
                        records.get(0).getDurationMillis(),
                        records.get(1).getDurationMillis()));

        assertEquals(0,  comparator.compare(records.get(0), records.get(0)),
                String.format("expected: == for [0]:%d, [0]:%d but not",
                        records.get(0).getDurationMillis(),
                        records.get(0).getDurationMillis()));

        assertEquals(-1,  comparator.compare(records.get(1), records.get(0)),
                String.format("expected: < for [1]:%d, [0]:%d but not",
                        records.get(1).getDurationMillis(),
                        records.get(0).getDurationMillis()));
    }

    @Disabled
    @Test
    public void test_print_fixture() throws IOException {
        Measurement m = TestHelper.makeMeasurementOf3lines();
        for (Record r : m) {
            System.out.println(r.toString());
        }

    }

}
