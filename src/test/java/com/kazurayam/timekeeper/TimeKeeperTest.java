package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TimeKeeperTest {

    @Test
    public void test_constructor() {
        TimeKeeper tk = new TimeKeeper();
        assertNotNull(tk);
    }

    @Test
    public void test_void_add_get_size() {
        TimeKeeper tk = new TimeKeeper();
        TreeMap attrs = TestHelper.makeAttributes();
        Record m = new Record(attrs);
        tk.add(m);
        Record got = tk.get(0);
        assertNotNull(got);
        assertEquals(1, tk.size());
    }
}
