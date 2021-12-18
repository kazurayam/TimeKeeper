package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;
import com.kazurayam.timekeeper.RowOrder;

import java.util.List;
import java.util.Objects;

public class RecordComparatorByDurationThenAttributes implements RecordComparator {

    private final List<String> keys;
    private final int order;

    public RecordComparatorByDurationThenAttributes(List<String> keys) {
        this(keys, RowOrder.ASCENDING);
    }

    public RecordComparatorByDurationThenAttributes(List<String> keys, RowOrder rowOrder) {
        Objects.requireNonNull(keys);
        this.keys = keys;
        this.order = (rowOrder == RowOrder.ASCENDING) ? 1 : -1;
    }

    @Override
    public int compare(Record left, Record right) {
        int primaryResult = new RecordComparatorByDuration().compare(left, right);
        int compareResult;
        if (primaryResult == 0) {
            compareResult = new RecordComparatorByAttributes(keys).compare(left, right);
        } else {
            compareResult = primaryResult;
        }
        return this.order * compareResult;
    }
}
