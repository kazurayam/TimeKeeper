package com.kazurayam.timekeeper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Measurement implements Iterable {

    private String id;
    private List<Record> records;

    public Measurement(String id) {
        this.id = id;
        this.records = new ArrayList<Record>();
    }

    public String getId() {
        return this.id;
    }

    public void add(Record record) {
        this.records.add(record);
    }

    public int size() {
        return records.size();
    }

    public Record get(int index) {
        return this.records.get(index);
    }

    @Override
    public Iterator iterator() {
        return this.records.iterator();
    }

}
