package com.kazurayam.timekeeper;

import com.google.gson.Gson;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.time.format.DateTimeFormatter;

public class Measurement implements Comparable<Measurement> {

    public static DateTimeFormatter FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private SortedMap<String, String> attributes;
    LocalDateTime startAt;
    LocalDateTime endAt;

    public Measurement(SortedMap<String, String> attr) {
        attributes = attr;
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

    public long durationMillis() {
        return this.getDuration().toMillis();
    }

    public boolean hasEqualAttributes(Measurement other) {
        return this.attributes.equals(other.attributes);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Measurement)) {
            return false;
        }
        Measurement other = (Measurement)obj;
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
        sb.append("\"attribute\":");
        sb.append(thisAttrs);
        sb.append(",");
        sb.append("\"startAt\":\"");
        sb.append(this.startAt.format(FORMAT));
        sb.append("\"");
        sb.append(",");
        sb.append("\"endAt\":\"");
        sb.append(this.endAt.format(FORMAT));
        sb.append("\"");
        sb.append("\"durationMillis\":");
        sb.append(this.getDuration().toMillis());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int compareTo(Measurement other) {
        Gson gson = new Gson();
        String thisAttrs = gson.toJson(this.attributes);
        String otherAttrs = gson.toJson(other.attributes);
        return thisAttrs.compareTo(otherAttrs);
    }
}
