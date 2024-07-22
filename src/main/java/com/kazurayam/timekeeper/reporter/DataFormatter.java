package com.kazurayam.timekeeper.reporter;

import java.text.NumberFormat;
import java.time.Duration;

public class DataFormatter {

    private DataFormatter() {}

    /**
     * Stringify a Duration to "minutes:seconds", or "hours:minutes:seconds".
     * A duration in millisecond will be rounded up to 1000 milliseconds.
     * Therefore, a duration of 456 milliseconds will be formatted
     * into a string "00:01"
     *
     * @param duration value in milliseconds to be formatted into "mm:ss"
     * @return 65 seconds will be formatted into "01:05".
     * 45 seconds will be "00:45".
     * 1456 milliseconds will be "00:01".
     * 456 milliseconds will be "00:01".
     */
    public static String formatDuration(Duration duration) {
        int s;
        if (duration.toMillis() < 1000) {
            s = 1;
        } else {
            s = (int) duration.toMillis() / 1000;
        }
        if (s >= 60 * 60) {
            return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
        } else {
            return String.format("%02d:%02d", (s % 3600) / 60, (s % 60));
        }
    }

    /**
     * stringify 123456L to "123,456"
     *
     * @param size e.g. 123456L
     * @return e.g "123,456"
     */
    public static String formatSize(long size) {
        return NumberFormat.getIntegerInstance().format(size);
    }

    public static String toGraph(Duration duration) {
        int tens = (int)(duration.toMillis() / 10000) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= tens; i++) {
            if ((i % 10) == 0) {
                int t = (i / 10) % 10;
                sb.append(Integer.valueOf(t).toString());
            } else {
                sb.append("#");
            }
        }
        return sb.toString();
    }
}
