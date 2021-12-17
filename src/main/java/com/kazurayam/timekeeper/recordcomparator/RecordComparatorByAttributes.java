package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordComparatorByAttributes implements RecordComparator {

    private List<String> keys;

    public RecordComparatorByAttributes(List<String> keys) {
        Objects.requireNonNull(keys);
        this.keys = keys;
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
        for (int i = 0; i < sortKeys.size(); i++) {
            String key = sortKeys.get(i);
            String leftValue = left.getAttributes().get(key);
            String rightValue = right.getAttributes().get(key);
            compareResult = leftValue.compareTo(rightValue);
            if (compareResult != 0) {
                break;
            }
        }
        return compareResult;
    }
}
