package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;
import com.kazurayam.timekeeper.RowOrder;

public class RecordComparatorBySize implements RecordComparator {

    private final int order;

    public RecordComparatorBySize() {
        this(RowOrder.ASCENDING);
    }

    public RecordComparatorBySize(RowOrder rowOrder) {
        this.order = (rowOrder == RowOrder.ASCENDING) ? 1 : -1;
    }

    @Override
    public int compare(Record left, Record right) {
        return this.order * left.getSize().compareTo(right.getSize());
    }

}
