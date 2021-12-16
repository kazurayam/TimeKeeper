package com.kazurayam.timekeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordComparatorByAttributes implements RecordComparator {

    private List<String> keys;

    RecordComparatorByAttributes(List<String> keys) {
        this.keys = keys;
    }

    @Override
    public int compare(Record left, Record right) {
        Set<String> leftKeys = left.getAttributes().keySet();
        Set<String> rightKeys = right.getAttributes().keySet();
        List<String> sortKeys = keys.stream()
                .filter(leftKeys::contains)
                .filter(rightKeys::contains)
                .collect(Collectors.toList());
        return 0;
    }
}
