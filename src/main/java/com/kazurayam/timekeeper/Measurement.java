package com.kazurayam.timekeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Measurement implements Iterable {

    private String id;   // "M1"
    private List<String> columnNames;   // ["case", "Suite", "Step Execution Log", "Log Viewer", "Mode"]
    private List<Record> records;

    public Measurement(String id, List<String> columnNames) {
        this.id = id;
        this.columnNames = columnNames;
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

    public Record formRecord() {
        Map<String, String> attributes = new HashMap<String, String>();
        for (String columnName : attributes.keySet()) {
            attributes.put(columnName, "");
        }
        return new Record.Builder().attributes(attributes).build();
    }
}
