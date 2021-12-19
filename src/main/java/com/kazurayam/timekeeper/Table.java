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
    private final String description;
    private final Boolean requireDescription;
    private final Boolean requireLegend;
    private final Boolean requireGraph;

    private Table(Builder builder) {
        this.measurement = builder.measurement;
        this.recordComparatorList = builder.recordComparatorList;
        this.requireSorting = builder.requireSorting;
        this.description = builder.description;
        this.requireDescription = builder.requireDescription;
        this.requireLegend = builder.requireLegend;
        this.requireGraph = builder.requireGraph;
    }

    public Measurement getMeasurement() {
        return this.measurement;
    }


    public List<RecordComparator> getRecordComparatorList() { return this.recordComparatorList;}

    public Boolean requireSorting() {
        return this.requireSorting;
    }

    public String getDescription() { return this.description; }

    public Boolean requireDescription() { return this.requireDescription; }

    public Boolean requireLegend() { return this.requireLegend; }

    public Boolean requireGraph() { return this.requireGraph; }

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
        if (this.recordComparatorList.size() > 0) {
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
        private String description;
        private Boolean requireDescription;
        private Boolean requireLegend;
        private Boolean requireGraph;
        //
        public Builder(Measurement measurement) {
            Objects.requireNonNull(measurement);
            this.measurement = measurement;
            this.recordComparatorList = new ArrayList<>();
            this.requireSorting = false;
            this.description = "as events flowed";
            this.requireDescription = true;
            this.requireLegend = true;
            this.requireGraph = true;
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
            this.description = source.description;
            this.requireDescription = source.requireDescription;
            this.requireLegend = source.requireLegend;
            this.requireGraph = source.requireGraph;
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
            this.requireSorting = true;
            this.description = String.format("sorted by attributes then duration (%s)",
                    rowOrder.description());
            return this;
        }
        //
        public Builder sortByDuration() {
            return this.sortByDuration(RowOrder.ASCENDING);
        }
        public Builder sortByDuration(RowOrder rowOrder) {
            return this.thenByDuration(rowOrder);
        }
        public Builder thenByDuration() {
            return this.sortByDuration(RowOrder.ASCENDING);
        }
        public Builder thenByDuration(RowOrder rowOrder) {
            RecordComparator rc = new RecordComparatorByDuration(rowOrder);
            this.recordComparatorList.add(rc);
            this.requireSorting = true;
            this.description = String.format("sorted by duration (%s)",
                    rowOrder.description());
            return this;
        }
        //
        public Builder sortBySize() {
            return this.sortBySize(RowOrder.ASCENDING);
        }
        public Builder sortBySize(RowOrder rowOrder) {
            return this.thenBySize(rowOrder);
        }
        public Builder thenBySize() {
            return this.sortBySize(RowOrder.ASCENDING);
        }
        public Builder thenBySize(RowOrder rowOrder) {
            RecordComparator rc = new RecordComparatorBySize(rowOrder);
            this.recordComparatorList.add(rc);
            this.requireSorting = true;
            this.description = "sorted by size";
            return this;
        }
        //
        Builder recordComparator(RecordComparator recordComparator) {
            Objects.requireNonNull(recordComparator);
            this.recordComparatorList.add(recordComparator);
            this.requireSorting = true;
            this.description = "sorted by " + recordComparator.getClass().getSimpleName();
            return this;
        }
        //
        public Builder noDescription() {
            this.requireDescription = false;
            return this;
        }
        //
        public Builder noLegend() {
            this.requireLegend = false;
            return this;
        }
        //
        public Builder noGraph() {
            this.requireGraph = false;
            return this;
        }
        //
        public Table build() {
            return new Table(this);
        }
    }
}
