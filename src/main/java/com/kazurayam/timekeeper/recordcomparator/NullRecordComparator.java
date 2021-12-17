package com.kazurayam.timekeeper.recordcomparator;

import com.kazurayam.timekeeper.Record;
import com.kazurayam.timekeeper.RecordComparator;

/**
 * a sort of "NULL Object" that implements RecordComparator
 */
public class NullRecordComparator implements RecordComparator {

    public NullRecordComparator() {}

    @Override
    public int compare(Record left, Record right) {
        return 0;
    }
}
