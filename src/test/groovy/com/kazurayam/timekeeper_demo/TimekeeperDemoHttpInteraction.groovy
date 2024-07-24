package com.kazurayam.timekeeper_demo

import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.ReportOptions
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
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"])
                .build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(interactions).build())
                .build();
        tk.report(outDir_.resolve("report.md"))
    }

    static void processURLs(List<String> urlList, Path outDir, Measurement m) {
        for (line in urlList) {
            String[] items = line.split("\\|")
            URL url = new URL(items[1])
            // mark the startAt timestamp
            m.before(["Case": items[0], "URL": items[1]])
            // do a heavy task
            String content = getHttpResponseContent(url)
            LocalDateTime afterGet = LocalDateTime.now()
            File outFile = outDir.resolve(url.getHost() + ".html").toFile()
            outFile.text = content
            // record the file size and how long it took to finish the process
            m.after(outFile.length())
        }
    }

    static String getHttpResponseContent(URL url) {
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
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"])
                .build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(interactions)
                        .sortByAttributes().build())
                .build();
        tk.report(outDir_.resolve("sortByAttributes.md"),
                ReportOptions.NOLEGEND);
    }

    @Test
    void test_HTTPGetAndSaveResponse_sortByAttributes_URL() {
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"])
                .build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(interactions)
                        .sortByAttributes(["URL"]).build())
                .build()
        tk.report(outDir_.resolve("sortByAttributes_URL.md"),
                ReportOptions.NOLEGEND);
    }

    @Test
    void test_HTTPGetAndSaveResponse_sortByAttributes_descending() {
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"])
                .build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(interactions)
                        .sortByAttributes( RowOrder.DESCENDING ).build())
                .build();
        tk.report(outDir_.resolve("sortByAttributes_descending.md"),
        ReportOptions.NOLEGEND);
    }

    @Test
    void test_HTTPGetAndSaveResponse_sortByAttributesThenDuration() {
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(interactions)
                        .sortByAttributes().thenByDuration().build())
                .build();
        tk.report(outDir_.resolve("sortByAttributesThenDuration.md"),
                ReportOptions.NOLEGEND)
    }

    @Test
    void test_HTTPGetAndSaveResponse_sortByDuration_descending() {
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(interactions)
                        .sortByDuration( RowOrder.DESCENDING ).build())
                .build();
        tk.report(outDir_.resolve("sortByDuration_descending.md"),
                ReportOptions.NOLEGEND)
    }


    @Test
    void test_HTTPGetAndSaveResponse_sortBySize_ascending() {
        Measurement interactions = new Measurement.Builder(
                "get URL, save HTML into file", ["Case", "URL"]).build()
        // interact with URL, save the HTML into files
        processURLs(urlList, outDir_, interactions)
        // print the report
        Timekeeper tk = new Timekeeper.Builder()
                .table(new Table.Builder(interactions)
                        .sortBySize().build())
                .build();
        tk.report(outDir_.resolve("sortBySize_ascending.md"),
                ReportOptions.NOLEGEND);
    }
}
