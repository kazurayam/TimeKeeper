package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.recordcomparator.NullRecordComparator;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByAttributes;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByAttributesThenDuration;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByAttributesThenSize;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByDuration;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByDurationThenAttributes;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorBySize;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorBySizeThenAttributes;

import java.util.List;
import java.util.Objects;

public class Table {

    private Measurement measurement;
    private RecordComparator recordComparator;
    private Boolean requireSorting;

    private Table(Builder builder) {
        this.measurement = builder.measurement;
        this.recordComparator = builder.recordComparator;
        this.requireSorting = builder.requireSorting;
    }

    public Measurement getMeasurement() {
        return this.measurement;
    }

    public RecordComparator getRecordComparator() {
        return this.recordComparator;
    }

    public Boolean requireSorting() {
        return this.requireSorting;
    }

    /**
     * clone the Measurement object with the records are sorted by the RecordComparator
     *
     * @return new Measurement instance with sorted rows
     */
    public Measurement sortedMeasurement() {
        Measurement clonedMeasurement =
                new Measurement.Builder(measurement.getId(), measurement.getColumnNames())
                        .build();
        List<Record> records = measurement.cloneRecords();
        if ( ! (recordComparator instanceof NullRecordComparator) ) {
            records.sort(recordComparator);
        }
        clonedMeasurement.addAll(records);
        return clonedMeasurement;
    }

    public static class Builder {
        private final Measurement measurement;
        private RecordComparator recordComparator;
        private Boolean requireSorting;
        public Builder(Measurement measurement) {
            Objects.requireNonNull(measurement);
            this.measurement = measurement;
            this.recordComparator = new NullRecordComparator();
            this.requireSorting = false;
        }
        //
        public Builder sortByAttributes() {
            return this.sortByAttributes(measurement.getColumnNames());
        }
        public Builder sortByAttributes(Measurement.ROW_ORDER rowOrder) {
            return this.sortByAttributes(measurement.getColumnNames(), rowOrder);
        }
        public Builder sortByAttributes(List<String> keys) {
            return this.sortByAttributes(keys, Measurement.ROW_ORDER.ASCENDING);
        }
        public Builder sortByAttributes(List<String> keys, Measurement.ROW_ORDER rowOrder) {
            Objects.requireNonNull(keys);
            if (keys.size() == 0) throw new IllegalArgumentException("keys must not be empty");
            this.recordComparator = new RecordComparatorByAttributes(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }
        //
        public Builder sortByAttributesThenDuration() {
            return this.sortByAttributesThenDuration(measurement.getColumnNames());
        }
        public Builder sortByAttributesThenDuration(Measurement.ROW_ORDER rowOrder) {
            return this.sortByAttributesThenDuration(measurement.getColumnNames(), rowOrder);
        }
        public Builder sortByAttributesThenDuration(List<String> keys) {
            return this.sortByAttributesThenDuration(keys, Measurement.ROW_ORDER.ASCENDING);
        }
        public Builder sortByAttributesThenDuration(List<String> keys, Measurement.ROW_ORDER rowOrder) {
            Objects.requireNonNull(keys);
            if (keys.size() == 0) throw new IllegalArgumentException("keys must not be empty");
            this.recordComparator = new RecordComparatorByAttributesThenDuration(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }
        //
        public Builder sortByAttributesThenSize() {
            return this.sortByAttributesThenSize(measurement.getColumnNames());
        }
        public Builder sortByAttributesThenSize(Measurement.ROW_ORDER rowOrder) {
            return this.sortByAttributesThenSize(measurement.getColumnNames(), rowOrder);
        }
        public Builder sortByAttributesThenSize(List<String> keys) {
            return this.sortByAttributesThenSize(keys, Measurement.ROW_ORDER.ASCENDING);
        }
        public Builder sortByAttributesThenSize(List<String> keys, Measurement.ROW_ORDER rowOrder) {
            Objects.requireNonNull(keys);
            if (keys.size() == 0) throw new IllegalArgumentException("keys must not be empty");
            this.recordComparator = new RecordComparatorByAttributesThenSize(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }
        //
        public Builder sortByDuration() {
            return this.sortByDuration(Measurement.ROW_ORDER.ASCENDING);
        }
        public Builder sortByDuration(Measurement.ROW_ORDER rowOrder) {
            this.recordComparator = new RecordComparatorByDuration(rowOrder);
            this.requireSorting = true;
            return this;
        }
        //
        public Builder sortByDurationThenAttributes() {
            return this.sortByDurationThenAttributes(measurement.getColumnNames());
        }
        public Builder sortByDurationThenAttributes(Measurement.ROW_ORDER rowOrder) {
            return this.sortByDurationThenAttributes(measurement.getColumnNames(), rowOrder);
        }
        public Builder sortByDurationThenAttributes(List<String> keys) {
            return this.sortByDurationThenAttributes(keys, Measurement.ROW_ORDER.ASCENDING);
        }
        public Builder sortByDurationThenAttributes(List<String> keys, Measurement.ROW_ORDER rowOrder) {
            this.recordComparator = new RecordComparatorByDurationThenAttributes(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }
        //
        public Builder sortBySize() {
            return this.sortBySize(Measurement.ROW_ORDER.ASCENDING);
        }
        public Builder sortBySize(Measurement.ROW_ORDER rowOrder) {
            this.recordComparator = new RecordComparatorBySize(rowOrder);
            this.requireSorting = true;
            return this;
        }
        //
        public Builder sortBySizeThenAttributes() {
            return this.sortBySizeThenAttributes(measurement.getColumnNames());
        }
        public Builder sortBySizeThenAttributes(Measurement.ROW_ORDER rowOrder) {
            return this.sortBySizeThenAttributes(measurement.getColumnNames(), rowOrder);
        }
        public Builder sortBySizeThenAttributes(List<String> keys) {
            return this.sortBySizeThenAttributes(keys, Measurement.ROW_ORDER.ASCENDING);
        }
        public Builder sortBySizeThenAttributes(List<String> keys, Measurement.ROW_ORDER rowOrder) {
            this.recordComparator = new RecordComparatorBySizeThenAttributes(keys, rowOrder);
            this.requireSorting = true;
            return this;
        }
        //
        Builder recordComparator(RecordComparator recordComparator) {
            Objects.requireNonNull(recordComparator);
            this.recordComparator = recordComparator;
            this.requireSorting = true;
            return this;
        }
        //
        public Table build() {
            return new Table(this);
        }
    }
}
