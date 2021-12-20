package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;
import com.kazurayam.timekeeper.RowOrder;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordComparatorByAttributes implements RecordComparator {

    private final List<String> keys;
    private RowOrder rowOrder;

    public RecordComparatorByAttributes(List<String> keys) {
        this(keys, RowOrder.ASCENDING);
    }

    public RecordComparatorByAttributes(List<String> keys, RowOrder rowOrder) {
        Objects.requireNonNull(keys);
        this.keys = keys;
        this.rowOrder = rowOrder;
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
        return rowOrder.order() * compareResult;
    }

    @Override
    public String getDescription() {
        return String.format("sorted by attributes (%s)", rowOrder.description());
    }
}
