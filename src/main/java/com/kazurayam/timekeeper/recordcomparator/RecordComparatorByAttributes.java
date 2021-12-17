package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Measurement;
import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordComparatorByAttributes implements RecordComparator {

    private final List<String> keys;
    private final int order;

    public RecordComparatorByAttributes(List<String> keys) {
        this(keys, Measurement.ROW_ORDER.ASCENDING);
    }

    public RecordComparatorByAttributes(List<String> keys, Measurement.ROW_ORDER rowOrder) {
        Objects.requireNonNull(keys);
        this.keys = keys;
        this.order = (rowOrder == Measurement.ROW_ORDER.ASCENDING) ? 1 : -1;
    }

    @Override
    public int compare(Record left, Record right) {
        Set<String> leftKeys = left.getAttributes().keySet();
        Set<String> rightKeys = right.getAttributes().keySet();
        // determine keys with which 2 Records are compared
        List<String> sortKeys = keys.stream()
                .filter(leftKeys::contains)
                .filter(rightKeys::contains)
                .collect(Collectors.toList());
        int compareResult = 0;
        for (String key : sortKeys) {
            String leftValue = left.getAttributes().get(key);
            String rightValue = right.getAttributes().get(key);
            compareResult = leftValue.compareTo(rightValue);
            if (compareResult != 0) {
                break;
            }
        }
        return this.order * compareResult;
    }
}
