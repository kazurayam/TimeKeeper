package com.kazurayam.timekeeper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Measurement implements Iterable<Record> {

    private final Logger logger = LoggerFactory.getLogger(Helper.getClassName());

    private final String id;   // "M1"
    private final List<String> columnNames;   // ["case", "Suite", "Step Execution Log", "Log Viewer", "Mode"]
    private final List<Record> records;

    private Map<String, String> attrs = null;
    private LocalDateTime startAt = null;

    private Measurement(Builder builder) {
        this.id = builder.id;
        this.columnNames = builder.columnNames;
        this.records = builder.records;
    }

    public void before(Map<String, String> attrs) {
        Objects.requireNonNull(attrs);
        this.attrs = attrs;
        this.startAt = LocalDateTime.now();
    }

    public void after() {
        Objects.requireNonNull(this.attrs);
        this.recordDuration(this.attrs, this.startAt, LocalDateTime.now());
    }

    public void after(long size) {
        Objects.requireNonNull(this.attrs);
        this.recordSizeAndDuration(this.attrs, size, this.startAt, LocalDateTime.now());
    }

    public String getId() {
        return this.id;
    }

    public List<String> getColumnNames() {
        return new ArrayList<>(columnNames);
    }

    void add(Record record) {
        this.records.add(record);
    }

    void addAll(List<Record> records) {
        this.records.addAll(records);
    }

    void recordDuration(Map<String, String> attrs,
                               LocalDateTime startAt, LocalDateTime endAt) {
        Record record = this.newRecord(attrs);
        record.setStartAt(startAt);
        record.setEndAt(endAt);
    }

    void recordSizeAndDuration(Map<String, String> attrs,
                                   long size,
                                   LocalDateTime startAt, LocalDateTime endAt) {
        Record record = this.newRecord(attrs);
        record.setSize(size);
        record.setStartAt(startAt);
        record.setEndAt(endAt);
    }

    public int size() {
        return records.size();
    }


    /**
     * get the clone of the record stored at the index
     * @param index index of the inner List of Records
     * @return the clone of the Record stored at the index
     */
    public Record get(int index) {
        return Record.clone(records.get(index));
    }

    public Record getLast() {
        if (!records.isEmpty()) {
            return get(this.size() - 1);
        } else {
            return Record.NULL;
        }
    }

    /**
     *
     * @return if one or more records are there, return the duration between
     * endAt and startAt of the latest record.
     * If no records are there, returns Duration of ZERO
     */
    public Duration getLastRecordDuration() {
        if (!records.isEmpty()) {
            return this.getLast().getDuration();
        } else {
            return Duration.ZERO;
        }
    }

    /**
     *
     * @return if one or more records are there, return the duration between
     * endAt and startAt of the latest record in the unit of milli-seconds.
     * if no records are there, returns -1.
     */
    public long getLastRecordDurationMillis() {
        Duration dur = this.getLastRecordDuration();
        if (dur != Duration.ZERO) {
            return dur.toMillis();
        } else {
            return -1;
        }
    }

    @Override
    public Iterator<Record> iterator() {
        return this.records.iterator();
    }

    public List<Record> cloneRecords() {
        List<Record> list = new ArrayList<>();
        for (Record r : this.records) {
            list.add(Record.clone(r));
        }
        return list;
    }

    public Record newRecord() {
        Map<String, String> pairs = new HashMap<>();
        return this.newRecord(pairs);
    }

    public Record newRecord(Map<String, String> pairs) {
        Map<String, String> attributes = new HashMap<>();
        for (String columnName : getColumnNames()) {
            attributes.put(columnName, "");
        }
        for (String key : pairs.keySet()) {
            if (attributes.containsKey(key)) {
                attributes.put(key, pairs.get(key));
            } else {
                logger.warn("key=\"" + key + "\" is not contained in the Measurement's attribute");
            }
        }
        Record record = new Record.Builder().attributes(attributes).build();
        this.add(record);
        return record;
    }

    public boolean hasRecordWithSize() {
        List<Record> recordsWithSize = records.stream()
                .filter(rc -> rc.getSize() > 0)
                .collect(Collectors.toList());
        return !recordsWithSize.isEmpty();
    }

    public boolean hasRecordWithDuration() {
        List<Record> recordsWithDuration = records.stream()
                .filter(rc -> rc.getDurationMillis() > 0)
                .collect(Collectors.toList());
        return !recordsWithDuration.isEmpty();
    }

    public long getAverageSize() {
        long accumulatedSize = records.stream().mapToLong(Record::getSize).sum();
        return accumulatedSize / records.size();
    }

    public Duration getAverageDuration() {
        long accumulatedDurationMillis =
                records.stream().mapToLong(Record::getDurationMillis).sum();
        long average = accumulatedDurationMillis / records.size();
        return Duration.ofMillis(average);
    }

    @Override
    public String toString() {
        return this.toJson();
    }

    public String toPrettyJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object obj = gson.fromJson(this.toJson(), Object.class);
        return gson.toJson(obj);
    }

    public String toJson()  {
        JsonObject jo = new JsonObject();
        jo.addProperty("id", this.getId());
        JsonArray columnNameArray = new JsonArray();
        for (String columnName : columnNames) {
            columnNameArray.add(columnName);
        }
        jo.add("columnNames", columnNameArray);
        JsonArray recordArray = new JsonArray();
        for (Record record : records) {
            recordArray.add(record.toJsonObject());
        }
        jo.add("records", recordArray);
        Gson gson = new Gson();
        return gson.toJson(jo);
    }

    public JsonObject toJsonObject() {
        String json = this.toJson();
        return new Gson().fromJson(json, JsonObject.class);
    }

    /**
     * Effective Java, "Builder pattern"
     */
    public static class Builder {
        private final String id;   // "M1"
        private final List<String> columnNames;   // ["case", "Suite", "Step Execution Log", "Log Viewer", "Mode"]
        private final List<Record> records;
        public Builder(String id, List<String> columnNames) {
            Objects.requireNonNull(id);
            Objects.requireNonNull(columnNames);
            if (columnNames.isEmpty()) {
                throw new IllegalArgumentException("columnNames must not be empty");
            }
            this.id = id;
            this.columnNames = columnNames;
            this.records = new ArrayList<>();
        }
        /**
         * @return a Measurement instance filled with values
         */
        public Measurement build() {
            return new Measurement(this);
        }
    }
}
