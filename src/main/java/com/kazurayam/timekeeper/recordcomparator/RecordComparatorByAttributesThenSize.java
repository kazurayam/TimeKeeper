package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

import java.util.List;

public class RecordComparatorByAttributesThenSize implements RecordComparator {

    private List<String> keys;

    public RecordComparatorByAttributesThenSize(List<String> keys) {
        this.keys = keys;
    }

    @Override
    public int compare(Record left, Record right) {
        int primaryResult = new RecordComparatorByAttributes(keys).compare(left, right);
        if (primaryResult == 0) {
            return new RecordComparatorBySize().compare(left, right);
        } else {
            return primaryResult;
        }
    }

}
