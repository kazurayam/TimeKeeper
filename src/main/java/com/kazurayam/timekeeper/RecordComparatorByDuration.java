package com.kazurayam.timekeeper;

public class RecordComparatorByDuration implements RecordComparator {
    @Override
    public int compare(Record o1, Record o2) {
        return o1.getDuration().compareTo(o2.getDuration());
    }
}
