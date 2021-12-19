package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;
import com.kazurayam.timekeeper.RowOrder;

public class RecordComparatorByDuration implements RecordComparator {

    private final int order;

    public RecordComparatorByDuration() {
        this(RowOrder.ASCENDING);
    }

    public RecordComparatorByDuration(RowOrder rowOrder) {
        this.order = (rowOrder == RowOrder.ASCENDING) ? 1 : -1;
    }

    @Override
    public int compare(Record left, Record right) {
        return this.order * left.getDuration().compareTo(right.getDuration());
    }
}
