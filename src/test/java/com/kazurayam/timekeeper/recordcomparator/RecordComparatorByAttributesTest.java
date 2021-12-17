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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecordComparatorByAttributesTest {


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
        RecordComparatorByAttributes comparator = new RecordComparatorByAttributes(Arrays.asList("URL"));
        assertTrue( 0 < comparator.compare(records.get(0), records.get(1)),
                String.format("expected: 0 < \"%s\".compare(\"%s\") but not",
                        records.get(0).getAttributes().get("URL"),
                        records.get(1).getAttributes().get("URL")));

        assertTrue(0 == comparator.compare(records.get(0), records.get(0)),
                String.format("expected: 0 == \"%s\".compare(\"%s\") but not",
                        records.get(0).getAttributes().get("URL"),
                        records.get(0).getAttributes().get("URL")));

        assertTrue( 0 > comparator.compare(records.get(1), records.get(0)),
                String.format("expected: 0 > \"%s\".compare(\"%s\") but not",
                        records.get(1).getAttributes().get("URL"),
                        records.get(0).getAttributes().get("URL")));
    }

    /**
     * RecordComparatorByAttributes#compare(r1, r2) will return 0 in case the given key is invalid.
     * Effectively the Measurement rows will not be sorted.
     */
    @Test
    public void test_invalid_key_specified() {
        RecordComparatorByAttributes comparator = new RecordComparatorByAttributes(Arrays.asList("foo"));
        assertTrue( 0 == comparator.compare(records.get(0), records.get(1)),
                String.format("expected: 0 == \"%s\".compare(\"%s\") but not",
                        records.get(0).getAttributes().get("URL"),
                        records.get(1).getAttributes().get("URL")));
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
