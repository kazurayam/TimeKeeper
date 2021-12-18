package com.kazurayam.timekeeper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TableList implements Iterable<Table> {

    private final List<Table> list;

    public TableList() {
        list = new ArrayList<>();
    }

    public void add(Table t) {
        this.list.add(t);
    }

    public Table get(int index) {
        return this.list.get(index);
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public Iterator<Table> iterator() {
        return this.list.iterator();
    }
}
