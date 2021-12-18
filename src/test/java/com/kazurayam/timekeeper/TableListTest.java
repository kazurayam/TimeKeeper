package com.kazurayam.timekeeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableListTest {

    private TableList tableList;

    @BeforeEach
    public void setup() {
        tableList = new TableList();
    }

    @Test
    public void test_add_size() {
        Measurement m = new Measurement.Builder("foo", TestHelper.getColumnNames()).build();
        Table t =  new Table.Builder(m).build();
        tableList.add(t);
        assertEquals(1, tableList.size());
    }
}
