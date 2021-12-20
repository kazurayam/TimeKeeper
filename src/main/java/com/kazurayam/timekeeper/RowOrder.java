package com.kazurayam.timekeeper;

public enum RowOrder {

    ASCENDING(1),
    DESCENDING(-1);

    private int order;
    private RowOrder(int order) {
        this.order = order;
    }
    public int order() {
        return this.order;
    }
    public String description() {
        return this.toString().toLowerCase();
    }
}
