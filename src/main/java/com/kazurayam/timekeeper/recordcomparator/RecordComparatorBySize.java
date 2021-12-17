package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

public class RecordComparatorBySize implements RecordComparator {

    public RecordComparatorBySize() {}

    @Override
    public int compare(Record o1, Record o2) {
        return o1.getSize().compareTo(o2.getSize());
    }

}
