package com.kazurayam.timekeeper.reporter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataParser {

    public static Pattern DURATION_PATTERN =
            Pattern.compile("((\\d+)\\s*:\\s*)?(\\d+)\\s*:\\s*(\\d+)");

    private DataParser() {}

    public static Duration parseDuration(String duration) {
        Matcher m = DURATION_PATTERN.matcher(duration);
        if (m.matches()) {
            boolean hasHour = (m.group(2) != null);
            if (hasHour) {
                int hours = Integer.parseInt(m.group(2));
                int minutes = Integer.parseInt(m.group(3));
                int seconds = Integer.parseInt(m.group(4));
                long dur = 60L * 60 * hours + 60L * minutes + seconds;
                return Duration.ofSeconds(dur);
            } else {
                int minutes = Integer.parseInt(m.group(3));
                int seconds = Integer.parseInt(m.group(4));
                long dur = 60L * minutes + seconds;
                return Duration.ofSeconds(dur);
            }
        } else {
            throw new IllegalArgumentException("duration=" + duration +
                    " does not match " + DURATION_PATTERN.toString());
        }
    }

    public static long parseSize(String size) {
        try {
            return (long) NumberFormat.getIntegerInstance().parse(size);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
