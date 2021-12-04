package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.Measurement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MeasurementList implements Iterable<Measurement> {

    private List<Measurement> list;

    public MeasurementList() {
        list = new ArrayList<Measurement>();
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
