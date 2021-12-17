package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

public class RecordComparatorByDuration implements RecordComparator {

    private final int order;

    public RecordComparatorByDuration() {
        this(Measurement.ROW_ORDER.ASCENDING);
    }

    public RecordComparatorByDuration(Measurement.ROW_ORDER rowOrder) {
        this.order = (rowOrder == Measurement.ROW_ORDER.ASCENDING) ? 1 : -1;
    }

    @Override
    public int compare(Record left, Record right) {
        return this.order * left.getDuration().compareTo(right.getDuration());
    }
}
