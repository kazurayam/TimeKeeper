package com.kazurayam.timekeeper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MeasurementList implements Iterable<Measurement> {

    private final List<Measurement> list;

    public MeasurementList() {
        list = new ArrayList<>();
    }

    public void add(Measurement m) {
        this.list.add(m);
    }

    public Measurement get(int index) {
        return this.list.get(index);
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public Iterator<Measurement> iterator() {
        return this.list.iterator();
    }
}
