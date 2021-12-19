package com.kazurayam.timekeeper;

import com.kazurayam.timekeeper.recordcomparator.NullRecordComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableTest {

    Measurement m;

    @BeforeEach
    public void setup() {
        m = TestHelper.makeMeasurement();
    }

    @Test
    public void test_smoke() {
        RecordComparator rc = new NullRecordComparator();
        Table t = new Table.Builder(m).recordComparator(rc).build();
        assertNotNull(t);
    }

    @Test
    public void test_Builder_sortByAttributes_noArg() {
        assertNotNull(new Table.Builder(m)
                .sortByAttributes().build());
    }

    @Test
    public void test_Builder_sortByAttributes_withArg() {
        assertNotNull(new Table.Builder(m)
                .sortByAttributes(Collections.singletonList("URL")).build());
    }

    @Test
    public void test_Builder_sortByAttributesThenDuration_noArg() {
        assertNotNull(new Table.Builder(m)
                .sortByAttributesThenDuration().build());
    }

    @Test
    public void test_Builder_sortByAttributesThenDuration_withArg() {
        assertNotNull(new Table.Builder(m)
                .sortByAttributesThenDuration(Collections.singletonList("URL")).build());
    }

    @Test
    public void test_Builder_sortByAttributesThenSize_noArg() {
        assertNotNull(new Table.Builder(m)
                .sortByAttributesThenSize().build());
    }

    @Test
    public void test_Builder_sortByAttributesThenSize_withArg() {
        assertNotNull(new Table.Builder(m)
                .sortByAttributesThenSize(Collections.singletonList("URL")).build());
    }

    @Test
    public void test_Builder_sortByDuration() {
        assertNotNull(new Table.Builder(m)
                .sortByDuration().build());
    }

    @Test
    public void test_Builder_sortByDurationThenAttributes_noArg() {
        assertNotNull(new Table.Builder(m)
                .sortByDurationThenAttributes().build());
    }

    @Test
    public void test_Builder_sortByDurationThenAttributes_withArg() {
        assertNotNull(new Table.Builder(m)
                .sortByDurationThenAttributes(Collections.singletonList("URL")).build());
    }

    @Test
    public void test_Builder_sortBySize() {
        assertNotNull(new Table.Builder(m)
                .sortBySize().build());
    }

    @Test
    public void test_Builder_sortBySizeThenAttributes_noArg() {
        assertNotNull(new Table.Builder(m)
                .sortBySizeThenAttributes().build());
    }

    @Test
    public void test_Builder_sortBySizeThenAttributes_withArg() {
        assertNotNull(new Table.Builder(m)
                .sortBySizeThenAttributes(Collections.singletonList("URL")).build());
    }

    @Test
    public void test_sorted_byAttributes() {
        Measurement m = stuffRecordsToSort(
                new Measurement.Builder("foo", Collections.singletonList("URL")).build());
        Table t = new Table.Builder(m)
                .sortByAttributes().build();
        Measurement sorted = t.sortedMeasurement();
        //System.out.println(sorted.toJson());
        assertNotNull(sorted);
        String url0 = sorted.get(0).getAttributes().get("URL");
        String url1 = sorted.get(1).getAttributes().get("URL");
        String url2 = sorted.get(2).getAttributes().get("URL");
        String url3 = sorted.get(3).getAttributes().get("URL");
        String url4 = sorted.get(4).getAttributes().get("URL");
        assertTrue(url0.compareTo(url1) <= 0);
        assertTrue(url1.compareTo(url2) <= 0);
        assertTrue(url2.compareTo(url3) <= 0);
        assertTrue(url3.compareTo(url4) <= 0);
    }

    @Test
    public void test_sorted_byAttributes_reverseOrder() {
        Measurement m = stuffRecordsToSort(
                new Measurement.Builder("foo", Collections.singletonList("URL")).build());
        Table t = new Table.Builder(m)
                .sortByAttributes(RowOrder.DESCENDING).build();
        Measurement sorted = t.sortedMeasurement();
        //System.out.println(sorted.toJson());
        assertNotNull(sorted);
        String url0 = sorted.get(0).getAttributes().get("URL");
        String url1 = sorted.get(1).getAttributes().get("URL");
        String url2 = sorted.get(2).getAttributes().get("URL");
        String url3 = sorted.get(3).getAttributes().get("URL");
        String url4 = sorted.get(4).getAttributes().get("URL");
        assertTrue(url0.compareTo(url1) >= 0);
        assertTrue(url1.compareTo(url2) >= 0);
        assertTrue(url2.compareTo(url3) >= 0);
        assertTrue(url3.compareTo(url4) >= 0);
    }

    @Test
    public void test_sorted_byDuration() {
        Measurement m = stuffRecordsToSort(
                new Measurement.Builder("foo", Collections.singletonList("URL")).build());
        Table t = new Table.Builder(m)
                .sortByDuration().build();
        Measurement sorted = t.sortedMeasurement();
        //System.out.println(sorted.toJson());
        assertNotNull(sorted);
        Duration dur0 = sorted.get(0).getDuration();
        Duration dur1 = sorted.get(1).getDuration();
        Duration dur2 = sorted.get(2).getDuration();
        Duration dur3 = sorted.get(3).getDuration();
        Duration dur4 = sorted.get(4).getDuration();
        assertTrue(dur0.compareTo(dur1) <= 0);
        assertTrue(dur1.compareTo(dur2) <= 0);
        assertTrue(dur2.compareTo(dur3) <= 0);
        assertTrue(dur3.compareTo(dur4) <= 0);
    }

    @Test
    public void test_sorted_byDuration_reverseOrder() {
        Measurement m = stuffRecordsToSort(
                new Measurement.Builder("foo", Collections.singletonList("URL")).build());
        Table t = new Table.Builder(m)
                .sortByDuration(RowOrder.DESCENDING)
                .build();
        Measurement sorted = t.sortedMeasurement();
        //System.out.println(sorted.toJson());
        assertNotNull(sorted);
        Duration dur0 = sorted.get(0).getDuration();
        Duration dur1 = sorted.get(1).getDuration();
        Duration dur2 = sorted.get(2).getDuration();
        Duration dur3 = sorted.get(3).getDuration();
        Duration dur4 = sorted.get(4).getDuration();
        assertTrue(dur0.compareTo(dur1) >= 0);
        assertTrue(dur1.compareTo(dur2) >= 0);
        assertTrue(dur2.compareTo(dur3) >= 0);
        assertTrue(dur3.compareTo(dur4) >= 0);
    }

    @Test
    public void test_sorted_bySize() {
        Measurement m = stuffRecordsToSort(
                new Measurement.Builder("foo", Collections.singletonList("URL")).build());
        Table t = new Table.Builder(m)
                .sortBySize().build();
        Measurement sorted = t.sortedMeasurement();
        //System.out.println(sorted.toJson());
        assertNotNull(sorted);
        Long size0 = sorted.get(0).getSize();
        Long size1 = sorted.get(1).getSize();
        Long size2 = sorted.get(2).getSize();
        Long size3 = sorted.get(3).getSize();
        Long size4 = sorted.get(4).getSize();
        assertTrue(size0.compareTo(size1) <= 0);
        assertTrue(size1.compareTo(size2) <= 0);
        assertTrue(size2.compareTo(size3) <= 0);
        assertTrue(size3.compareTo(size4) <= 0);
    }

    @Test
    public void test_sorted_byAttributesThenDuration() {
        Measurement m = stuffRecordsToSort(
                new Measurement.Builder("foo", Collections.singletonList("URL")).build());
        Table t = new Table.Builder(m)
                .sortByAttributesThenDuration().build();
        Measurement sorted = t.sortedMeasurement();
        //System.out.println(sorted.toJson());
        assertNotNull(sorted);
        String url0 = sorted.get(0).getAttributes().get("URL");
        String url1 = sorted.get(1).getAttributes().get("URL");
        String url2 = sorted.get(2).getAttributes().get("URL");
        String url3 = sorted.get(3).getAttributes().get("URL");
        String url4 = sorted.get(4).getAttributes().get("URL");
        assertTrue(url0.compareTo(url1) <= 0);
        assertTrue(url1.compareTo(url2) <= 0);
        assertTrue(url2.compareTo(url3) <= 0);
        assertTrue(url3.compareTo(url4) <= 0);
        //
        Duration dur3 = sorted.get(3).getDuration();
        Duration dur4 = sorted.get(4).getDuration();
        assertTrue(dur3.compareTo(dur4) <= 0);
    }

    private Measurement stuffRecordsToSort(Measurement m) {
        try {
            Path fixtureFile = Paths.get(".").resolve("src/test/fixtures/measurement_5lines.md");

            List<Record> records = TestHelper.readMd(fixtureFile);
            for (Record r : records) {
                m.add(r);
            }
            return m;
        } catch (IOException e) {
            throw new IllegalStateException("unable to proceed");
        }
    }


    @Test
    public void test_noDescription() {
        Measurement m = stuffRecordsToSort(
                new Measurement.Builder("foo", Collections.singletonList("URL")).build());
        Table t = new Table.Builder(m)
                .noDescription().build();
        assertNotNull(t);
    }
}
