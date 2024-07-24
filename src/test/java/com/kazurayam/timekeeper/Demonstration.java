package com.kazurayam.timekeeper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

/**
 * This code demonstrates how to use the timekeeper library
 */
public class Demonstration {

    private static Logger logger = LoggerFactory.getLogger(Demonstration.class);
    Path jsonFile = Paths.get("src/test/fixtures/store.json");

    @Test
    public void test_measuring_json_pretty_printing_duration_by_gson() throws IOException {
        Measurement m1 = new Measurement.Builder("Pretty printing by Gson",
                Arrays.asList("Case")).build();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(Files.newBufferedReader(jsonFile));
        //
        m1.before(Collections.singletonMap("Case", "store.json"));
        String pp1 = gson.toJson(je);
        m1.after();
        //
        m1.before(Collections.singletonMap("Case", "store.json again"));
        String pp2 = gson.toJson(je);
        m1.after();

        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(m1).build())
                .build();
        StringWriter sw = new StringWriter();
        tk.report(sw, ReportOptions.NODESCRIPTION_NOLEGEND);
        Method m = new Object(){}.getClass().getEnclosingMethod();
        logger.info(String.format("[%s] %s", m.getName(), sw.toString()));
    }
}
