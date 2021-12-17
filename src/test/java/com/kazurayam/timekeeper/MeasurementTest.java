package com.kazurayam.timekeeper;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MeasurementTest {

    private final Logger logger = LoggerFactory.getLogger(TestHelper.getClassName());

    private Measurement m;

    @BeforeEach
    public void setup() {
        m = new Measurement.Builder("foo", TestHelper.getColumnNames()).build();
        m.add(TestHelper.makeRecord1());
        m.add(TestHelper.makeRecord2());
    }

    @Test
    public void test_getId() {
        assertEquals("foo", m.getId());
    }

    @Test
    public void test_size() {
        assertEquals(2, m.size());
    }

    @Test
    public void test_iterator() {
        m.forEach(record -> {
            logger.debug(record.toString());
        });
    }

    @Test
    public void test_get() {
        Record r = m.get(0);
        assertEquals("Test Cases/printID", r.getAttributes().get("testCaseId"));
    }

    @Test
    public void test_newRecord() {
        Record record = m.newRecord();
        Set<String> keySet = record.getAttributes().keySet();
        assertTrue(keySet.size() > 0);
        assertTrue(keySet.contains("case"));
    }


    @Test
    public void test_getLatestRecordDurationMillis() throws InterruptedException {
        Measurement m = new Measurement.Builder("some", Arrays.asList("URL")).build();
        Record r = new Record.Builder().attr("URL", "http://example.com").build();
        r.setStartAt(LocalDateTime.now());
        Thread.sleep(300);
        r.setEndAt(LocalDateTime.now());
        m.add(r);
        long millis = m.getLastRecordDurationMillis();
        assertTrue(millis < 1000);
        // System.out.println(millis);
    }

    @Test
    public void test_toString() {
        Gson gson = new Gson();
        String json = m.toString();
        //System.out.println(json);
        Object obj = gson.fromJson(json, Object.class);
        assertNotNull(obj);
    }

    @Test
    public void test_toJson() {
        String json = m.toJson();
        System.out.println(json);
        assertNotNull(json);
    }

    @Test
    public void test_fixture_rich() throws IOException {
        Measurement m = TestHelper.makeRichMeasurement();
        //System.out.println(m.toJson());
        assertNotNull(m);
    }

    @Test
    public void test_fixture_3lines() throws IOException {
        Measurement m = TestHelper.makeMeasurementOf3lines();
        //System.out.println(m.toJson());
        assertNotNull(m);
    }

    @Test
    public void test_Builder_sortByAttributes_noArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByAttributes().build());
    }

    @Test
    public void test_Builder_sortByAttributes_withArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByAttributes(Arrays.asList("URL")).build());
    }

    @Test
    public void test_Builder_sortByAttributesThenDuration_noArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByAttributesThenDuration().build());
    }

    @Test
    public void test_Builder_sortByAttributesThenDuration_withArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByAttributesThenDuration(Arrays.asList("URL")).build());
    }

    @Test
    public void test_Builder_sortByAttributesThenSize_noArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByAttributesThenSize().build());
    }

    @Test
    public void test_Builder_sortByAttributesThenSize_withArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByAttributesThenSize(Arrays.asList("URL")).build());
    }

    @Test
    public void test_Builder_sortByDuration() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByDuration().build());
    }

    @Test
    public void test_Builder_sortByDurationThenAttributes_noArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByDurationThenAttributes().build());
    }

    @Test
    public void test_Builder_sortByDurationThenAttributes_withArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortByDurationThenAttributes(Arrays.asList("URL")).build());
    }

    @Test
    public void test_Builder_sortBySize() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortBySize().build());
    }

    @Test
    public void test_Builder_sortBySizeThenAttributes_noArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortBySizeThenAttributes().build());
    }

    @Test
    public void test_Builder_sortBySizeThenAttributes_withArg() {
        assertNotNull(new Measurement.Builder("foo", TestHelper.getColumnNames())
                .sortBySizeThenAttributes(Arrays.asList("URL")).build());
    }

    @Test
    public void test_sorted_byAttributes() {
        Measurement m = new Measurement.Builder("foo", Arrays.asList("URL"))
                .sortByAttributes().build();
        Measurement stuffed = stuffRecordsToSort(m);
        Measurement sorted = stuffed.sorted();
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
        Measurement m = new Measurement.Builder("foo", Arrays.asList("URL"))
                .sortByAttributes()
                .reverseOrder(true).build();
        Measurement stuffed = stuffRecordsToSort(m);
        Measurement sorted = stuffed.sorted();
        System.out.println(sorted.toJson());
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
        Measurement m = new Measurement.Builder("foo", Arrays.asList("URL"))
                .sortByDuration().build();
        Measurement stuffed = stuffRecordsToSort(m);
        Measurement sorted = stuffed.sorted();
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
        Measurement m = new Measurement.Builder("foo", Arrays.asList("URL"))
                .sortByDuration()
                .reverseOrder(true).build();
        Measurement stuffed = stuffRecordsToSort(m);
        Measurement sorted = stuffed.sorted();
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
        Measurement m = new Measurement.Builder("foo", Arrays.asList("URL"))
                .sortBySize().build();
        Measurement stuffed = stuffRecordsToSort(m);
        Measurement sorted = stuffed.sorted();
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
        Measurement m = new Measurement.Builder("foo", Arrays.asList("URL"))
                .sortByAttributesThenDuration().build();
        Measurement stuffed = stuffRecordsToSort(m);
        Measurement sorted = stuffed.sorted();
        System.out.println(sorted.toJson());
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
}