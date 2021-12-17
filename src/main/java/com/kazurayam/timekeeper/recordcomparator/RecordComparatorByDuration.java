package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

public class RecordComparatorByDuration implements RecordComparator {

    public RecordComparatorByDuration() {}

    @Override
    public int compare(Record o1, Record o2) {
        return o1.getDuration().compareTo(o2.getDuration());
    }
}
