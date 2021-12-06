package com.kazurayam.timekeeper.demo

import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Timekeeper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

class TimekeeperDemoHttpInteraction {

    private static Path outDir_

    @BeforeAll
    static void setupClass() {
        outDir_ = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(TimekeeperDemoHttpInteraction.class.getSimpleName())
        if (Files.exists(outDir_)) {
            outDir_.toFile().deleteDir();
        }
        Files.createDirectory(outDir_)
    }

    @Test
    void httpGetAndSaveRespose() {
        List<String> urlList = [
                "https://www.google.com/search?q=timekeeper",
                "https://duckduckgo.com/?q=timekeeper&t=h_&ia=web",
                "https://search.yahoo.co.jp/search?p=timekeeper"
        ]
        Timekeeper tk = new Timekeeper()
        Measurement interactions = tk.newMeasurement(
                "get URL, save HTML into file", ["URL"])
        for (entry in urlList) {
            URL url = new URL(entry)
            // mark the startAt timestamp
            LocalDateTime beforeGet = LocalDateTime.now()
            // do the heavy task
            String content = getHttpResponceContent(url)
            // mark the endAt timestamp
            LocalDateTime afterGet = LocalDateTime.now()
            File outFile = outDir_.resolve(url.getHost() + ".html").toFile()
            outFile.text = content
            // record the stats
            interactions.recordSizeAndDuration(
                    ["URL": entry],
                    outFile.length(),   // size of the HTTP response body
                    beforeGet,          // startAt
                    afterGet            // endAt
            )
        }
        // print the report
        tk.report(outDir_.resolve("report.md"))
    }

    String getHttpResponceContent(URL url) {
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
            sb.append(inputLine);
        }
        streamReader.close()
        con.disconnect()
        return sb.toString()
    }
}
