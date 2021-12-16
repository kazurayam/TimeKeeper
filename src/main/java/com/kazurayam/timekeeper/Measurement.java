package com.kazurayam.timekeeper;

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

    private String id;   // "M1"
    private List<String> columnNames;   // ["case", "Suite", "Step Execution Log", "Log Viewer", "Mode"]
    private List<Record> records;

    public Measurement(String id, List<String> columnNames) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(columnNames);
        assert columnNames.size() > 0;
        this.id = id;
        this.columnNames = columnNames;
        this.records = new ArrayList<Record>();
    }

    public String getId() {
        return this.id;
    }

    public List<String> getColumnNames() {
        return new ArrayList<String>(columnNames);
    }

    public void add(Record record) {
        this.records.add(record);
    }

    public void recordDuration(Map<String, String> attrs,
                               LocalDateTime startAt, LocalDateTime endAt) {
        Record record = this.newRecord(attrs);
        record.setStartAt(startAt);
        record.setEndAt(endAt);
    }

    public void recordSize(Map<String, String> attrs, long size) {
        Record record = this.newRecord(attrs);
        record.setSize(size);
    }

    public void recordSizeAndDuration(Map<String, String> attrs,
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
        if (records.size() > 0) {
            return get(this.size() - 1);
        } else {
            return Record.NULL;
        }
    }

    /**
     *
     * @return if one or more records are there, return the duration between
     * endAt and startAt of the latest record.
     * if no records are there, returns Duration.ZERO
     */
    public Duration getLastRecordDuration() {
        if (records.size() > 0) {
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

    public Record newRecord() {
        Map<String, String> pairs = new HashMap<String, String>();
        return this.newRecord(pairs);
    }

    public Record newRecord(Map<String, String> pairs) {
        Map<String, String> attributes = new HashMap<String, String>();
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
        return recordsWithSize.size() > 0;
    }

    public boolean hasRecordWithDuration() {
        List<Record> recordsWithDuration = records.stream()
                .filter(rc -> rc.getDurationMillis() > 0)
                .collect(Collectors.toList());
        return recordsWithDuration.size() > 0;
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
}
