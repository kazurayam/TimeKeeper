package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecordComparatorByDurationThenAttributesTest {

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

    @Test
    public void test_smoke() {
        RecordComparatorByDurationThenAttributes comparator =
                new RecordComparatorByDurationThenAttributes(Arrays.asList("URL"));
        Record left = records.get(0);
        Record right = records.get(4);
        assertTrue( comparator.compare(left, right) > 0,
                String.format("expected: %d = %d && \"%s\".compare(\"%s\") > 0 but not",
                        left.getSize(),
                        right.getSize(),
                        left.getAttributes().get("URL"),
                        right.getAttributes().get("URL")
                ));
    }
}
