package com.kazurayam.timekeeper;

import java.util.ArrayList;
import java.util.List;

public class TimeKeeper {

    private List<Measurement> measurements;

    public TimeKeeper() {
        measurements = new ArrayList<Measurement>();
    }

    public void add(Measurement m) {
        measurements.add(m);
    }

    public Measurement get(int index) {
        return measurements.get(index);
    }

    public int size() {
        return measurements.size();
    }

}
