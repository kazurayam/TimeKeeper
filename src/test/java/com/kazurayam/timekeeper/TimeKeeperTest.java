package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

import java.util.TreeMap;

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
        Record m = TestHelper.makeRecord1();
        tk.add(m);
        Record got = tk.get(0);
        assertNotNull(got);
        assertEquals(1, tk.size());
    }


    /*

     */
    @Test
    public void test_case_1() {

    }
}
