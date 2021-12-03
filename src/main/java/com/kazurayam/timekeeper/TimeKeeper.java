package com.kazurayam.timekeeper;

import java.util.ArrayList;
import java.util.List;

public class TimeKeeper {

    private List<Record> records;

    public TimeKeeper() {
        records = new ArrayList<Record>();
    }

    public void add(Record m) {
        records.add(m);
    }

    public Record get(int index) {
        return records.get(index);
    }

    public int search(Record m) {
        return 0;
    }

    public int size() {
        return records.size();
    }


}
