package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

import java.util.List;

public class RecordComparatorByAttributesThenDuration implements RecordComparator {

    private final List<String> keys;
    private final int order;

    public RecordComparatorByAttributesThenDuration(List<String> keys) {
        this(keys, Measurement.ROW_ORDER.ASCENDING);
    }

    public RecordComparatorByAttributesThenDuration(List<String> keys, Measurement.ROW_ORDER rowOrder) {
        this.keys = keys;
        this.order = (rowOrder == Measurement.ROW_ORDER.ASCENDING) ? 1 : -1;
    }

    @Override
    public int compare(Record left, Record right) {
        int primaryResult = new RecordComparatorByAttributes(keys).compare(left, right);
        int compareResult;
        if (primaryResult == 0) {
            compareResult = new RecordComparatorByDuration().compare(left, right);
        } else {
            compareResult = primaryResult;
        }
        return this.order * compareResult;
    }

}
