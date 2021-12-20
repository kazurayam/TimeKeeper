package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;
import com.kazurayam.timekeeper.RowOrder;

public class RecordComparatorBySize implements RecordComparator {

    private final RowOrder rowOrder;

    public RecordComparatorBySize() {
        this(RowOrder.ASCENDING);
    }

    public RecordComparatorBySize(RowOrder rowOrder) {
        this.rowOrder = rowOrder;
    }

    @Override
    public int compare(Record left, Record right) {
        return rowOrder.order() * left.getSize().compareTo(right.getSize());
    }

    @Override
    public String getDescription() {
        return String.format("sorted by size (%s)", rowOrder.description());
    }

}
