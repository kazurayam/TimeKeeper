package com.kazurayam.timekeeper_demo

import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.RowOrder
import com.kazurayam.timekeeper.Table
import com.kazurayam.timekeeper.Timekeeper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

// this test takes 3 minutes to finish
@Disabled
class TimekeeperDemoHttpInteraction {

    private static Path outDir_
    private static List<String> urlList = [
            "case 1|https://www.google.com/search?q=timekeeper",
            "case 1|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web",
            "case 1|https://search.yahoo.co.jp/search?p=timekeeper",
            "case 2|https://www.google.com/search?q=timekeeper",
            "case 2|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web",
            "case 3|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web",
    ]

    @BeforeAll
    static void setupClass() {
        outDir_ = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(TimekeeperDemoHttpInteraction.class.getSimpleName())
        if (Files.exists(outDir_)) {
            outDir_.toFile().deleteDir()
        }
        Files.createDirectory(outDir_)
    }

    @Test
    void test_HTTPGetAndSaveResponse() {
        Timekeeper tk = new Timekeeper()
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Table table = new Table.Builder(interactions).build()
        tk.add(table)
        tk.report(outDir_.resolve("report.md"))
    }

    static void processURLs(List<String> urlList, Path outDir, Measurement m) {
        for (line in urlList) {
            String[] items = line.split("\\|")
            URL url = new URL(items[1])
            // mark the startAt timestamp
            LocalDateTime beforeGet = LocalDateTime.now()
            // do the heavy task
            String content = getHttpResponceContent(url)
            // mark the endAt timestamp
            LocalDateTime afterGet = LocalDateTime.now()
            File outFile = outDir.resolve(url.getHost() + ".html").toFile()
            outFile.text = content
            // record the stats
            m.recordSizeAndDuration(
                    ["Case": items[0], "URL": items[1]],
                    outFile.length(),   // size of the HTTP response body
                    beforeGet,          // startAt
                    afterGet            // endAt
            )
        }
    }

    static String getHttpResponceContent(URL url) {
        HttpURLConnection con = (HttpURLConnection) url.openConnection()
        con.setRequestMethod("GET")
        con.setConnectTimeout(5000)
        con.setReadTimeout(5000)
        int status = con.getResponseCode()
        Reader streamReader
        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream())
        } else {
            streamReader = new InputStreamReader(con.getInputStream())
        }
        BufferedReader br = new BufferedReader(streamReader)
        String inputLine
        StringBuffer sb = new StringBuffer()
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine)
        }
        streamReader.close()
        con.disconnect()

        // sleep a while in between 1.0 - 5.0 seconds at random
        Thread.sleep((long)(4000 * Math.random() + 1000))

        return sb.toString()
    }





    //-----------------------------------------------------------------

    @Test
    void test_HTTPGetAndSaveResponse_sortByAttributes() {
        Timekeeper tk = new Timekeeper()
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Table table = new Table.Builder(interactions)
                .sortByAttributes().noLegend().build()
        tk.add(table)
        tk.report(outDir_.resolve("sortByAttributes.md"))
    }

    @Test
    void test_HTTPGetAndSaveResponse_sortByAttributes_URL() {
        Timekeeper tk = new Timekeeper()
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Table table = new Table.Builder(interactions)
                .sortByAttributes(["URL"]).noLegend().build()
        tk.add(table)
        tk.report(outDir_.resolve("sortByAttributes_URL.md"))
    }

    @Test
    void test_HTTPGetAndSaveResponse_sortByAttributes_descending() {
        Timekeeper tk = new Timekeeper()
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Table table = new Table.Builder(interactions)
                .sortByAttributes( RowOrder.DESCENDING ).noLegend().build()
        tk.add(table)
        tk.report(outDir_.resolve("sortByAttributes_descending.md"))
    }

    @Test
    void test_HTTPGetAndSaveResponse_sortByAttributesThenDuration() {
        Timekeeper tk = new Timekeeper()
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Table table = new Table.Builder(interactions)
                .sortByAttributes().thenByDuration().noLegend().build()
        tk.add(table)
        tk.report(outDir_.resolve("sortByAttributesThenDuration.md"))
    }

    @Test
    void test_HTTPGetAndSaveResponse_sortByDuration_descending() {
        Timekeeper tk = new Timekeeper()
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Table table = new Table.Builder(interactions)
                .sortByDuration( RowOrder.DESCENDING ).noLegend().build()
        tk.add(table)
        tk.report(outDir_.resolve("sortByDuration_descending.md"))
    }


    @Test
    void test_HTTPGetAndSaveResponse_sortBySize_ascending() {
        Timekeeper tk = new Timekeeper()
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Table table = new Table.Builder(interactions)
                .sortBySize().noLegend().build()
        tk.add(table)
        tk.report(outDir_.resolve("sortBySize_ascending.md"))
    }
}
