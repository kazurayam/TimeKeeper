package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

import java.util.List;
import java.util.Objects;

public class ChainedRecordComparator implements RecordComparator {

    private final List<RecordComparator> recordComparatorList;

    public ChainedRecordComparator(List<RecordComparator> recordComparatorList) {
        Objects.requireNonNull(recordComparatorList);
        if (recordComparatorList.size() == 0) {
            throw new IllegalArgumentException("recordComparatorList must not be empty");
        }
        this.recordComparatorList = recordComparatorList;
    }

    @Override
    public int compare(Record left, Record right) {
        int result = 0;
        for (RecordComparator rc : recordComparatorList) {
            result = rc.compare(left, right);
            if (result != 0) {
                break;
            }
        }
        return result;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        for (RecordComparator rc : recordComparatorList) {
            if (sb.length() > 0) {
                sb.append(" > ");
            }
            sb.append(rc.getDescription());
        }
        return sb.toString();
    }
}
