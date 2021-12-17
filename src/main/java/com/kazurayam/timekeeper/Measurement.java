package com.kazurayam.timekeeper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kazurayam.timekeeper.recordcomparator.NullRecordComparator;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByAttributes;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByAttributesThenDuration;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByAttributesThenSize;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByDuration;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByDurationThenAttributes;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorBySize;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorBySizeThenAttributes;
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

    public enum ROW_ORDER {
        ASCENDING,
        DESCENDING
    }

    private final Logger logger = LoggerFactory.getLogger(Helper.getClassName());

    private final String id;   // "M1"
    private final List<String> columnNames;   // ["case", "Suite", "Step Execution Log", "Log Viewer", "Mode"]
    private final List<Record> records;
    private final RecordComparator recordComparator;
    private final Boolean requireSorting;

    private Measurement(Builder builder) {
        this.id = builder.id;
        this.columnNames = builder.columnNames;
        this.records = builder.records;
        this.recordComparator = builder.recordComparator;
        this.requireSorting = builder.requireSorting;
    }

    public String getId() {
        return this.id;
    }

    public List<String> getColumnNames() {
        return new ArrayList<>(columnNames);
    }

    public void add(Record record) {
        this.records.add(record);
    }

    public void addAll(List<Record> records) {
        this.records.addAll(records);
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
     * If no records are there, returns Duration of ZERO
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

    public Boolean requireSorting() {
        return this.requireSorting;
    }

    /**
     * clone this while sorting the records contained
     *
     * @return new Measurement instance with sorted rows
     */
    public Measurement sorted() {
        Measurement clonedMeasurement =
                new Measurement.Builder(this.id, this.columnNames)
                        .recordComparator(this.recordComparator)
                        .build();
        List<Record> records = this.cloneRecords();
        if (this.requireSorting) {
            records.sort(this.recordComparator);
        }
        clonedMeasurement.addAll(records);
        return clonedMeasurement;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object obj = gson.fromJson(this.toString(), Object.class);
        return gson.toJson(obj);
    }

    /**
     * TODO
     * should construct JSON using Gson, rather than StringBuilder
     * because current implementation does not support escaping characters
     * that are sensitive for JSON syntax.
     *
     * https://www.baeldung.com/java-json-escaping
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":\"");
        sb.append(this.id);
        sb.append("\",\"columnNames\":[");
        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"");
            sb.append(columnNames.get(i));
            sb.append("\"");
        }
        sb.append("],\"records\":[");
        for (int i = 0; i < records.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(records.get(i));
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Effective Java, "Builder pattern"
     */
    public static class Builder {
        private final String id;   // "M1"
        private final List<String> columnNames;   // ["case", "Suite", "Step Execution Log", "Log Viewer", "Mode"]
        private final List<Record> records;
        private RecordComparator recordComparator;
        private Boolean requireSorting;
        public Builder(String id, List<String> columnNames) {
            Objects.requireNonNull(id);
            Objects.requireNonNull(columnNames);
            if (columnNames.size() == 0) {
                throw new IllegalArgumentException("columnNames must not be empty");
            }
            this.id = id;
            this.columnNames = columnNames;
            this.records = new ArrayList<>();
            this.recordComparator = new NullRecordComparator();
            this.requireSorting = false;
        }
        //
        public Builder sortByAttributes() {
            return this.sortByAttributes(this.columnNames);
        }
        public Builder sortByAttributes(Measurement.ROW_ORDER rowOrder) {
            return this.sortByAttributes(this.columnNames, rowOrder);
        }
        public Builder sortByAttributes(List<String> keys) {
            return this.sortByAttributes(keys, ROW_ORDER.ASCENDING);
        }
        public Builder sortByAttributes(List<String> keys, Measurement.ROW_ORDER rowOrder) {
            Objects.requireNonNull(keys);
            if (keys.size() == 0) throw new IllegalArgumentException("keys must not be empty");
            this.recordComparator = new RecordComparatorByAttributes(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }

        public Builder sortByAttributesThenDuration() {
            return this.sortByAttributesThenDuration(this.columnNames);
        }
        public Builder sortByAttributesThenDuration(Measurement.ROW_ORDER rowOrder) {
            return this.sortByAttributesThenDuration(this.columnNames, rowOrder);
        }
        public Builder sortByAttributesThenDuration(List<String> keys) {
            return this.sortByAttributesThenDuration(keys, ROW_ORDER.ASCENDING);
        }
        public Builder sortByAttributesThenDuration(List<String> keys, Measurement.ROW_ORDER rowOrder) {
            Objects.requireNonNull(keys);
            if (keys.size() == 0) throw new IllegalArgumentException("keys must not be empty");
            this.recordComparator = new RecordComparatorByAttributesThenDuration(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }
        public Builder sortByAttributesThenSize() {
            return this.sortByAttributesThenSize(this.columnNames);
        }
        public Builder sortByAttributesThenSize(ROW_ORDER rowOrder) {
            return this.sortByAttributesThenSize(this.columnNames, rowOrder);
        }
        public Builder sortByAttributesThenSize(List<String> keys) {
            return this.sortByAttributesThenSize(keys, ROW_ORDER.ASCENDING);
        }
        public Builder sortByAttributesThenSize(List<String> keys, Measurement.ROW_ORDER rowOrder) {
                Objects.requireNonNull(keys);
            if (keys.size() == 0) throw new IllegalArgumentException("keys must not be empty");
            this.recordComparator = new RecordComparatorByAttributesThenSize(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }

        public Builder sortByDuration() {
            return this.sortByDuration(ROW_ORDER.ASCENDING);
        }
        public Builder sortByDuration(ROW_ORDER rowOrder) {
            this.recordComparator = new RecordComparatorByDuration(rowOrder);
            this.requireSorting = true;
            return this;
        }

        public Builder sortByDurationThenAttributes() {
            return this.sortByDurationThenAttributes(this.columnNames);
        }
        public Builder sortByDurationThenAttributes(ROW_ORDER rowOrder) {
            return this.sortByDurationThenAttributes(this.columnNames, rowOrder);
        }
        public Builder sortByDurationThenAttributes(List<String> keys) {
            return this.sortByDurationThenAttributes(keys, ROW_ORDER.ASCENDING);
        }
        public Builder sortByDurationThenAttributes(List<String> keys, ROW_ORDER rowOrder) {
            this.recordComparator = new RecordComparatorByDurationThenAttributes(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }

        public Builder sortBySize() {
            return this.sortBySize(ROW_ORDER.ASCENDING);
        }
        public Builder sortBySize(ROW_ORDER rowOrder) {
            this.recordComparator = new RecordComparatorBySize(rowOrder);
            this.requireSorting = true;
            return this;
        }

        public Builder sortBySizeThenAttributes() {
            return this.sortBySizeThenAttributes(this.columnNames);
        }
        public Builder sortBySizeThenAttributes(ROW_ORDER rowOrder) {
            return this.sortBySizeThenAttributes(this.columnNames, rowOrder);
        }
        public Builder sortBySizeThenAttributes(List<String> keys) {
            return this.sortBySizeThenAttributes(keys, ROW_ORDER.ASCENDING);
        }
        public Builder sortBySizeThenAttributes(List<String> keys, ROW_ORDER rowOrder) {
            this.recordComparator = new RecordComparatorBySizeThenAttributes(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }

        //
        public Builder recordComparator(RecordComparator recordComparator) {
            this.recordComparator = recordComparator;
            this.requireSorting = true;
            return this;
        }

        /**
         * @return a Measurement instance filled with values
         */
        public Measurement build() {
            return new Measurement(this);
        }
    }
}
