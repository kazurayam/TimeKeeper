package com.kazurayam.timekeeper;

public enum RowOrder {

    ASCENDING,
    DESCENDING;

    public String description() {
        return this.toString().toLowerCase();
    }
}
