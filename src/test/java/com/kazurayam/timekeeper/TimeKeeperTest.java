package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeKeeperTest {

    private final Logger logger = LoggerFactory.getLogger(TestHelper.getClassName());

    @Test
    public void test_constructor() {
        TimeKeeper tk = new TimeKeeper();
        assertNotNull(tk);
    }

    @Test
    public void test_void_add_get_size() {
        TimeKeeper tk = new TimeKeeper();
        Measurement m = makeFixture();
        tk.add(m);
        Measurement result = tk.get(0);
        assertNotNull(result);
        assertEquals(1, tk.size());
    }

    @Test
    public void test_search() {
        Measurement m = makeFixture();
        assertNotNull(m);

    }

    private Measurement makeFixture() {
        Measurement m = new Measurement("M1");
        //
        Map<String, String> attrsY1 = new HashMap<String, String>();
        attrsY1.put("case", "Y1");
        attrsY1.put("Suite", "TS2");
        attrsY1.put("Step Execution Log", "Enabled");
        attrsY1.put("Log Viewer", "Attached");
        attrsY1.put("Mode", "Tree");
        LocalDateTime startAtY1 = LocalDateTime.now();
        long durationMillisY1 = 167000;   // 25 minutes 17 seconds
        m.add(makeRecord(attrsY1, startAtY1, durationMillisY1));
        //
        Map<String, String> attrsY2 = new HashMap<String, String>();
        attrsY1.put("case", "Y2");
        attrsY1.put("Suite", "TS2");
        attrsY1.put("Step Execution Log", "Disabled");
        attrsY1.put("Log Viewer", "Attached");
        attrsY1.put("Mode", "Tree");
        LocalDateTime startAtY2 = LocalDateTime.now();
        long durationMillisY2 = 371000;   // 6 minutes 11 seconds
        m.add(makeRecord(attrsY2, startAtY2, durationMillisY2));
        //
        Map<String, String> attrsY3 = new HashMap<String, String>();
        attrsY1.put("case", "Y3");
        attrsY1.put("Suite", "TS2");
        attrsY1.put("Step Execution Log", "Disabled");
        attrsY1.put("Log Viewer", "Closed");
        attrsY1.put("Mode", "-");
        LocalDateTime startAtY3 = LocalDateTime.now();
        long durationMillisY3 = 43000;   // 0 minutes 43 seconds
        m.add(makeRecord(attrsY3, startAtY3, durationMillisY3));
        //
        return m;
    }

    private Record makeRecord(Map<String, String> attrs, LocalDateTime startAt, long durationMillis) {
        Record r= new Record.Builder().attributes(attrs).build();
        LocalDateTime endAt = startAt.plus(Duration.ofMillis(durationMillis));
        r.setStartAt(startAt);
        r.setEndAt(endAt);
        return r;
    }
}
