package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.recordcomparator.ChainedRecordComparator;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByAttributes;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorByDuration;
import com.kazurayam.timekeeper.recordcomparator.RecordComparatorBySize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Table {

    private final Measurement measurement;
    private final List<RecordComparator> recordComparatorList;
    private final Boolean requireSorting;

    private Table(Builder builder) {
        this.measurement = builder.measurement;
        this.recordComparatorList = builder.recordComparatorList;
        this.requireSorting = builder.requireSorting;
    }

    public Measurement getMeasurement() {
        return this.measurement;
    }


    public List<RecordComparator> getRecordComparatorList() { return this.recordComparatorList;}

    public Boolean requireSorting() {
        return this.requireSorting;
    }

    public String getDescription() {
        if (this.recordComparatorList.size() > 0) {
            RecordComparator chain = new ChainedRecordComparator(this.recordComparatorList);
            return chain.getDescription();
        } else {
            return "as events flow";
        }
    }

    /**
     * clone the Measurement object with the records are sorted by the RecordComparator
     *
     * @return new Measurement instance with sorted rows
     */
    public Measurement sorted() {
        Measurement clonedMeasurement =
                new Measurement.Builder(measurement.getId(), measurement.getColumnNames())
                        .build();
        List<Record> records = measurement.cloneRecords();
        if (!this.recordComparatorList.isEmpty()) {
            RecordComparator chain = new ChainedRecordComparator(this.recordComparatorList);
            records.sort(chain);
        }
        clonedMeasurement.addAll(records);
        return clonedMeasurement;
    }

    /**
     *
     */
    public static class Builder {
        private final Measurement measurement;
        private List<RecordComparator> recordComparatorList;
        private Boolean requireSorting;
        //
        public Builder(Measurement measurement) {
            Objects.requireNonNull(measurement);
            this.measurement = measurement;
            this.recordComparatorList = new ArrayList<>();
            this.requireSorting = false;
        }

        /**
         * clone the source while replacing the measurement contained
         * @param source a Table object to clone
         * @param measurement the Measurement object to replace the one in the source
         */
        public Builder(Table source, Measurement measurement) {
            Objects.requireNonNull(source);
            this.measurement = measurement;   // replace the Measurement contained
            this.recordComparatorList = source.recordComparatorList;
            this.requireSorting = source.requireSorting;
        }
        //
        public Builder sortByAttributes() {
            return this.sortByAttributes(measurement.getColumnNames());
        }
        public Builder sortByAttributes(RowOrder rowOrder) {
            return this.sortByAttributes(measurement.getColumnNames(), rowOrder);
        }
        public Builder sortByAttributes(List<String> keys) {
            return this.sortByAttributes(keys, RowOrder.ASCENDING);
        }
        public Builder sortByAttributes(List<String> keys, RowOrder rowOrder) {
            this.recordComparatorList = new ArrayList<>();
            return thenByAttributes(keys, rowOrder);
        }
        //
        public Builder thenByAttributes() {
            return this.thenByAttributes(measurement.getColumnNames());
        }
        public Builder thenByAttributes(RowOrder rowOrder) {
            return this.thenByAttributes(measurement.getColumnNames(), rowOrder);
        }
        public Builder thenByAttributes(List<String> keys) {
            return this.thenByAttributes(keys, RowOrder.ASCENDING);
        }
        public Builder thenByAttributes(List<String> keys, RowOrder rowOrder) {
            Objects.requireNonNull(keys);
            if (keys.size() == 0) throw new IllegalArgumentException("keys must not be empty");
            RecordComparator rc = new RecordComparatorByAttributes(keys, rowOrder);
            this.recordComparatorList.add(rc);
            return this;
        }
        //
        public Builder sortByDuration() {
            return this.sortByDuration(RowOrder.ASCENDING);
        }
        public Builder sortByDuration(RowOrder rowOrder) {
            this.recordComparatorList = new ArrayList<>();
            return this.thenByDuration(rowOrder);
        }
        public Builder thenByDuration() {
            return this.thenByDuration(RowOrder.ASCENDING);
        }
        public Builder thenByDuration(RowOrder rowOrder) {
            RecordComparator rc = new RecordComparatorByDuration(rowOrder);
            this.recordComparatorList.add(rc);
            this.requireSorting = true;
            return this;
        }
        //
        public Builder sortBySize() {
            return this.sortBySize(RowOrder.ASCENDING);
        }
        public Builder sortBySize(RowOrder rowOrder) {
            this.recordComparatorList = new ArrayList<>();
            return this.thenBySize(rowOrder);
        }
        public Builder thenBySize() {
            return this.thenBySize(RowOrder.ASCENDING);
        }
        public Builder thenBySize(RowOrder rowOrder) {
            RecordComparator rc = new RecordComparatorBySize(rowOrder);
            this.recordComparatorList.add(rc);
            this.requireSorting = true;
            return this;
        }
        //
        public Table build() {
            return new Table(this);
        }
    }
}
