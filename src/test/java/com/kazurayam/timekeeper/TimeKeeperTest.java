package com.kazurayam.timekeeper;

import org.junit.jupiter.api.Test;

public class TimeKeeperTest {

    @Test
    public void test_constructor() {
        TimeKeeper tk = new TimeKeeper();
        assert tk != null;
    }
}
