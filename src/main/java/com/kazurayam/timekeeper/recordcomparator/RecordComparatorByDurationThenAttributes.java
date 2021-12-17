package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

import java.util.List;
import java.util.Objects;

public class RecordComparatorByDurationThenAttributes implements RecordComparator {

    private List<String> keys;

    public RecordComparatorByDurationThenAttributes(List<String> keys) {
        Objects.requireNonNull(keys);
        this.keys = keys;
    }

    @Override
    public int compare(Record left, Record right) {
        int primaryResult = new RecordComparatorByDuration().compare(left, right);
        if (primaryResult == 0) {
            return new RecordComparatorByAttributes(keys).compare(left, right);
        } else {
            return primaryResult;
        }
    }
}
