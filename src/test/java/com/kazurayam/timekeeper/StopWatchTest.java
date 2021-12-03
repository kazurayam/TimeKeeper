package com.kazurayam.timekeeper;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

public class StopWatchTest {

    @Test
    void test_smoke() throws Exception {
        StopWatch stopWatch = new StopWatch();
        // 計測開始
        stopWatch.start();
        Thread.sleep(500);
        // 計測停止
        stopWatch.stop();
        // 経過時間出力
        System.out.println(stopWatch.getTime()); // ⇒500
    }

    @Test
    void test_stop_to_resume() throws Exception {
        StopWatch stopWatch = new StopWatch();
        // 計測開始
        stopWatch.start();
        Thread.sleep(500);
        // 計測中断
        stopWatch.suspend();
        Thread.sleep(500);
        // 計測再開
        stopWatch.resume();
        Thread.sleep(500);
        // 計測停止
        stopWatch.stop();
        // 経過時間出力
        System.out.println(stopWatch.getTime()); // 1000 + alpha
    }
}
