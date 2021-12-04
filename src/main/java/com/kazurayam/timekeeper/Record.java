package com.kazurayam.timekeeper;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Record implements Comparable<Record> {

    private final Logger logger = LoggerFactory.getLogger(Helper.getClassName());

    public static DateTimeFormatter FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private SortedMap<String, String> attributes;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Record(Builder builder) {
        this.attributes = builder.attributes;
        this.startAt = builder.startAt;
        this.endAt = builder.endAt;
    }

    public void putAttribute(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (this.attributes.containsKey(key)) {
            this.attributes.put(key, value);
        } else {
            logger.warn("key=\"" + key + "\",value=\"" + value +
                    "\"; key is not contained in the keys:" +
                    this.attributes.keySet());
        }
    }

    public void setStartAt(LocalDateTime startAt) {
        Objects.requireNonNull(startAt);
        this.startAt = startAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        Objects.requireNonNull(endAt);
        this.endAt = endAt;
    }

    protected static Pattern DURATION_PATTERN = Pattern.compile("\\s*((\\d*)\\s*:\\s*)?(\\d+)\\s*");

    protected static int parseDurationString(String durationStr) {
        Matcher m = DURATION_PATTERN.matcher(durationStr);
        if (m.find()) {
            int minutes = 0;
            if (m.group(2) != null) {
                minutes = Integer.parseInt(m.group(2));
            }
            return (minutes * 60 + Integer.parseInt(m.group(3)));
        } else {
            throw new IllegalArgumentException("\"" + durationStr + "\" is not acceptable.");
        }
    }

    /**
     *
     * @param durationStr "2:34" -> 2 minutes 34 seconds; "17" -> 17 seconds;
     */
    public void setDuration(String durationStr) {
        int parsed = parseDurationString(durationStr);
        this.setDuration(parsed);
    }

    public void setDuration(int durationSeconds) {
        this.setDuration(durationSeconds * 1000L);
    }

    public void setDuration(long durationMillis) {
        Duration duration = Duration.ofMillis(durationMillis);
        this.setDuration(duration);
    }

    public void setDuration(Duration duration) {
        this.endAt = LocalDateTime.now();
        this.startAt = this.endAt.minus(duration);
    }

    public void setDuration(LocalDateTime startAt, Duration duration) {
        this.startAt = startAt;
        this.endAt = this.startAt.plus(duration);
    }

    public void setDuration(Duration duration, LocalDateTime endAt) {
        this.endAt = endAt;
        this.startAt = this.endAt.minus(duration);
    }

    public SortedMap<String, String> getAttributes() {
        return new TreeMap<String, String>(this.attributes);
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public Duration getDuration() {
        return Duration.between(startAt, endAt);
    }

    public long getDurationMillis() {
        return this.getDuration().toMillis();
    }

    public boolean hasEqualAttributes(Record other) {
        Objects.requireNonNull(other);
        return this.attributes.equals(other.attributes);
    }

    @Override
    public boolean equals(Object obj) {
        Objects.requireNonNull(obj);
        if (! (obj instanceof Record)) {
            return false;
        }
        Record other = (Record)obj;
        return this.attributes.equals(other.attributes) &&
                this.getDuration().equals(other.getDuration());
    }

    @Override
    public int hashCode() {
        return this.attributes.hashCode();
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        String thisAttrs = gson.toJson(this.attributes);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"attributes\":");
        sb.append(thisAttrs);
        sb.append(",");
        sb.append("\"startAt\":\"");
        sb.append(this.startAt.format(FORMAT));
        sb.append("\"");
        sb.append(",");
        sb.append("\"endAt\":\"");
        sb.append(this.endAt.format(FORMAT));
        sb.append("\"");
        sb.append(",");
        sb.append("\"durationMillis\":");
        sb.append(this.getDuration().toMillis());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int compareTo(Record other) {
        Objects.requireNonNull(other);
        Gson gson = new Gson();
        String thisAttrs = gson.toJson(this.attributes);
        String otherAttrs = gson.toJson(other.attributes);
        return thisAttrs.compareTo(otherAttrs);
    }

    public static class Builder {
        private SortedMap<String, String> attributes;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        public Builder() {
            attributes = new TreeMap<String, String>();
            startAt = LocalDateTime.now();
            endAt = startAt;
        }
        public Builder attr(String key, String value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            attributes.put(key, value);
            return this;
        }
        public Builder attributes(Map<String, String> attributes) {
            this.attributes = new TreeMap<String, String>(attributes);
            return this;
        }
        public Record build() {
            return new Record(this);
        }
    }
}
