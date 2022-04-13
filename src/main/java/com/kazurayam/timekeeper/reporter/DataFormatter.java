package com.kazurayam.timekeeper.reporter;

import java.text.NumberFormat;
import java.time.Duration;

public class DataFormatter {

    private DataFormatter() {}

    /**
     * stringify a Duration to "minutes:seconds", or "hours:minutes:seconds"
     *
     * @param duration value in milliseconds to be formatted into "mm:ss"
     * @return 45 seconds will be "00:45"
     */
    public static String formatDuration(Duration duration) {
        int s = (int) duration.toMillis() / 1000;
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
