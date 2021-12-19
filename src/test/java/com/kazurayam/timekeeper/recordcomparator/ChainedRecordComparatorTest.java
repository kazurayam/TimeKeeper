package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;
import com.kazurayam.timekeeper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChainedRecordComparatorTest {

    /**
     * {"attributes":{"URL":"https://www.kazurayam.com/web/"},"size":1985644,    "durationMillis":67000}
     * {"attributes":{"URL":"https://www.kazurayam.com/"},"size":736042,         "durationMillis":38000}
     * {"attributes":{"URL":"https://www.kazurayam.com/mobile/"},"size":1613751, "durationMillis":11000}
     * |https://www.kazurayam.com/web/|878,653|00:33|`#`|
     * |https://www.kazurayam.com/foo/|1,985,644|01:07|`#`|
     */
    private List<Record> records;

    @BeforeEach
    public void setup() throws IOException {
        Path fixtureFile = Paths.get(".").resolve("src/test/fixtures/measurement_5lines.md");
        records = TestHelper.readMd(fixtureFile);
    }

    /**
     * compare the following 2 records
     *
     * record0 : |https://www.kazurayam.com/web/|1,985,644|01:07|`#`|
     * record3 : |https://www.kazurayam.com/web/|878,653|00:33|`#`|
     *
     * using a chain of
     *
     * - RecordComparatorByAttributes
     * - RecordComparatorByDuration
     */
    @Test
    public void test_ByAttributesThenByDuration() {
        List<RecordComparator> chain = new ArrayList<>();
        chain.add(new RecordComparatorByAttributes(Arrays.asList("URL")));
        chain.add(new RecordComparatorByDuration());
        RecordComparator comparator = new ChainedRecordComparator(chain);
        Record record0 = records.get(0);
        Record record3 = records.get(3);
        assertTrue( comparator.compare(record0, record3) > 0,
                String.format("expected: \"%s\".compare(\"%s\") > 0 || %d > %d; but not",
                        record0.getAttributes().get("URL"),
                        record3.getAttributes().get("URL"),
                        record0.getDurationMillis(),
                        record3.getDurationMillis()
                ));
        assertTrue( comparator.compare(record0, record0) == 0,
                String.format("expected: \"%s\".compare(\"%s\") == 0 && %d == %d; but not",
                        record0.getAttributes().get("URL"),
                        record0.getAttributes().get("URL"),
                        record0.getDurationMillis(),
                        record0.getDurationMillis()
                ));
        assertTrue( comparator.compare(record3, record0) < 0,
                String.format("expected: \"%s\".compare(\"%s\") < 0 || %d < %d; but not",
                        record3.getAttributes().get("URL"),
                        record0.getAttributes().get("URL"),
                        record3.getDurationMillis(),
                        record0.getDurationMillis()
                ));
    }
}
