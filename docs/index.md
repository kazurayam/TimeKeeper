Timekeeper helps Java/Groovy test codes to record performance measurement statistics, and to compile reports in Markdown format.

# Motivation

Often I develop Web UI tests in Groovy using Selenium. I wanted to measure the performance of the tests. Often I want to measure:

1.  how long (seconds) tests take to navigate to a URL in browser

2.  how long tests take to take and save screenshot of a web page

3.  how large (bytes) is the generated image file

I want to examine many URLs; 100 or more. In practice, most of URLs respond within 3 seconds but a few of them sometimes respond slow (over 20 seconds). Why? What happened? I need to list the slow URLs and look into them.

The 1st problem is that it is bothersome recording the duration with a stopwatch. So I introduced the Apache Commonns [StopWatch](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/time/StopWatch.html) library to measure the duration and print the figure in console messages. Still, it is difficult to find useful information out of bulky console messages. So I want to summerise the staticstics. The 2nd problem is that it is too tiresome to write a statistics report in Markdown table format manually. I want to automate this task.

I want my WebUI tests to perform not only performance measurement but also compiling a concise report in Markdown format as well. Here comes the Timekeeper!

# API

Javadoc is [here](./api/index.html)

# Examples

A `Timekeeper` object lets you create **one or more** `Measurement` objects. A `Measurement` object stands for a table which contains a header and one or more `Record` set. A `Record` contains columns and a duration in `mm:ss` format (minutes:seconds). My test will put an instance of `LocalDateTime.now()` just before the test calls a long running method call (such as Selenium navite, and taking screenshot). This timestatmp is recorded as `startAt`. Also my test will put another instance of `LocalDateTime.now()` just after the long-running method call. This timestamp is recorded `endAt`. Each record object can calculate the duration = endAt minus startAt. And finally Timekeeper’s `report(Path)` method can generate a text report in Markdown syntax.

## Example 1 --- minimalistic

The following example is a minimalistic example of utilizing the Timekeeper library, in Groovy using JUnit 5.

    package com.kazurayam.timekeeper.demo

    import com.kazurayam.timekeeper.Measurement
    import com.kazurayam.timekeeper.Timekeeper
    import org.junit.jupiter.api.BeforeAll
    import org.junit.jupiter.api.Test

    import java.nio.file.Files
    import java.nio.file.Path
    import java.nio.file.Paths
    import java.time.LocalDateTime

    class TimekeeperDemoMinimal {

        private static Path outDir_

        @BeforeAll
        static void setupClass() {
            outDir_ = Paths.get(".")
                    .resolve("build/tmp/testOutput")
                    .resolve(TimekeeperDemoMinimal.class.getSimpleName())
            if (Files.exists(outDir_)) {
                outDir_.toFile().deleteDir();
            }
            Files.createDirectory(outDir_)
        }

        @Test
        void demo_planned_sleep() {
            Timekeeper tk = new Timekeeper()
            Measurement m1 = new Measurement.Builder("How long it waited",
                    ["description m1"]).build()
            tk.add(m1)
            for (int i in [2, 3, 5, 7]) {
                LocalDateTime beforeSleep = LocalDateTime.now()
                // do a processing that could take long time.
                Thread.sleep(i * 1000L)
                LocalDateTime afterSleep = LocalDateTime.now()
                m1.recordDuration(["description m1": "sleeping for " + i + " secs"],
                        beforeSleep, afterSleep)
            }
            tk.report(outDir_.resolve("planned_sleep.md"))
        }
    }

This code outputs the following markdown text.

    ## How long it waited

    |description m1|duration|graph|
    |:----|----:|:----|
    |sleeping for 2 secs|00:02|`#`|
    |sleeping for 3 secs|00:03|`#`|
    |sleeping for 5 secs|00:05|`#`|
    |sleeping for 7 secs|00:07|`#`|
    |Average|00:04|

    The format of duration is "minutes:seconds"

    one # represents 10 seconds in the duration graph

## Example 2 --- HTTP GET & save HTML

The following code processes a list URLs. It makes HTTP GET request, save the request body into file. It checks the size of the file in bytes, and measures the duration of HTTP GET request.

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
                outDir_.toFile().deleteDir();
            }
            Files.createDirectory(outDir_)
        }

        @Test
        void test_HTTPGetAndSaveResponse() {
            Timekeeper tk = new Timekeeper()
            Measurement interactions = new Measurement.Builder(
                    "get URL, save HTML into file", ["Case", "URL"]).build();
            tk.add(interactions)
            // interact with URL, save the HTML into files
            processURLs(urlList, outDir_, interactions)
            // print the report
            tk.report(outDir_.resolve("report.md"))
        }

        void processURLs(List<String> urlList, Path outDir, Measurement m) {
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

            // sleep a while in between 1.0 - 5.0 seconds at random
            Thread.sleep((long)(4000 * Math.random() + 1000));

            return sb.toString()
        }

This code will output the following markdown text.

    ## get URL, save HTML into file

    |Case|URL|size|duration|graph|
    |:----|:----|----:|----:|:----|
    |case 1|https://www.google.com/search?q=timekeeper|7,165|00:05|`#`|
    |case 1|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:03|`#`|
    |case 1|https://search.yahoo.co.jp/search?p=timekeeper|21,217|00:05|`#`|
    |case 2|https://www.google.com/search?q=timekeeper|7,165|00:03|`#`|
    |case 2|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:04|`#`|
    |case 3|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:04|`#`|
    |Average|-|6,002|00:04| |

## Example 3 --- Selenium test

Input CSV file is here:

    https://www.google.com/search?q=timekeeper,timekeeper_google.png
    https://duckduckgo.com/?q=timekeeper&t=h_&ia=web,timekeeper_duckduckgo.png
    https://search.yahoo.co.jp/search?p=timekeeper,timekeeper_yahoo.png

The test emits the following Markdown text:

    ## How long it took to navigate to URLs

    |URL|duration|graph|
    |:----|----:|:----|
    |https://www.google.com/search?q=timekeeper|00:02|`#`|
    |https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|00:02|`#`|
    |https://search.yahoo.co.jp/search?p=timekeeper|00:03|`#`|
    |Average|00:02|

    The format of duration is "minutes:seconds"

    one # represents 10 seconds in the duration graph

    ## How long it took to take shootshots

    |URL|size|duration|graph|
    |:----|----:|----:|:----|
    |https://www.google.com/search?q=timekeeper|510,456|00:05|`#`|
    |https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|272,954|00:03|`#`|
    |https://search.yahoo.co.jp/search?p=timekeeper|381,088|00:03|`#`|
    |Average|388,166|00:04|

This Markdown text will be rendered on browser like this:

![report](images/report.png)

The code is here:

    package com.kazurayam.timekeeper.demo

    import com.kazurayam.ashotwrapper.AShotWrapper
    import com.kazurayam.ashotwrapper.DevicePixelRatioResolver
    import com.kazurayam.timekeeper.Measurement
    import com.kazurayam.timekeeper.Timekeeper
    import io.github.bonigarcia.wdm.WebDriverManager
    import org.junit.jupiter.api.AfterEach
    import org.junit.jupiter.api.BeforeAll
    import org.junit.jupiter.api.BeforeEach
    import org.junit.jupiter.api.Test
    import org.openqa.selenium.Dimension
    import org.openqa.selenium.WebDriver
    import org.openqa.selenium.chrome.ChromeDriver
    import org.openqa.selenium.chrome.ChromeOptions

    import java.util.concurrent.TimeUnit;

    import java.awt.image.BufferedImage;
    import java.nio.file.Files
    import java.nio.file.Path
    import java.nio.file.Paths
    import javax.imageio.ImageIO
    import java.time.LocalDateTime

    import static org.junit.jupiter.api.Assertions.*;

    class TimekeeperDemoWithSelenium {

        private static Path outDir_
        private WebDriver driver_
        private AShotWrapper.Options aswOptions_ = null

        @BeforeAll
        static void setupClass() {
            WebDriverManager.chromedriver().setup();
            outDir_ = Paths.get(".")
                    .resolve("build/tmp/testOutput")
                    .resolve(TimekeeperDemoWithSelenium.class.getSimpleName())
            if (Files.exists(outDir_)) {
                outDir_.toFile().deleteDir();
            }
            Files.createDirectory(outDir_)
        }

        @BeforeEach
        void setupTest() {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--headless");   // use Headless Chrome browser
            driver_ = new ChromeDriver(options);
            driver_.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
            driver_.manage().window().setSize(new Dimension(1200, 800));
            //
            float dpr = DevicePixelRatioResolver.resolveDPR(driver_);
            aswOptions_ = new AShotWrapper.Options.Builder().devicePixelRatio(dpr).build();
        }

        @AfterEach
        void tearDown() {
            if (driver_ != null) {
                driver_.quit();
            }
        }

        @Test
        void demo_with_selenium() {
            Timekeeper tk = new Timekeeper()
            Measurement navigation = new Measurement.Builder(
                    "How long it took to navigate to URLs", ["URL"])
                    .build()
            tk.add(navigation)
            Measurement screenshot = new Measurement.Builder(
                    "How long it took to take shootshots", ["URL"])
                    .build()
            tk.add(screenshot)
            // process all URLs in the CSV file
            Path csv = Paths.get(".").resolve("src/test/fixtures/URLs.csv");
            for (Tuple t in parseCSVfile(csv)) {
                String url = t.get(0)
                String filename = t.get(1)
                driver_.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
                // navigate to the URL, record the duration
                LocalDateTime beforeNavigate = LocalDateTime.now()
                driver_.navigate().to(url)
                LocalDateTime afterNavigate = LocalDateTime.now()
                navigation.recordDuration(["URL": url], beforeNavigate, afterNavigate)
                // take a screenshot of the page, record the duration
                LocalDateTime beforeScreenshot = LocalDateTime.now()
                Path imageFile = this.takeFullPageScreenshot(driver_, outDir_, filename)
                LocalDateTime afterScreenshot = LocalDateTime.now()
                screenshot.recordSizeAndDuration(["URL": url],
                        imageFile.toFile().size(),
                        beforeScreenshot, afterScreenshot)
            }
            // now print the report
            tk.report(outDir_.resolve("report.md"))
        }

        private Path takeFullPageScreenshot(WebDriver driver, Path outDir, String fileName) {
            // using my AShotWrapper lib at https://kazurayam.github.io/ashotwrapper/
            BufferedImage image = AShotWrapper.takeEntirePageImage(driver, aswOptions_);
            assertNotNull(image);
            Path screenshotFile = outDir.resolve(fileName);
            ImageIO.write(image, "PNG", screenshotFile.toFile());
            assertTrue(Files.exists(screenshotFile));
            return screenshotFile;
        }

        /**
         * read a CSV file of:
         *
         * url1,filename1
         * url2,filename2
         * url3,filename3
         * ...
         *
         * @param csv
         * @return
         */
        private List<Tuple2> parseCSVfile(Path csv) {
            List<Tuple2> result = new ArrayList<Tuple2>()
            List<String> lines = csv.toFile() as List<String>
            for (String line in lines) {
                String[] items = line.split(",")
                if (items.size() >= 2) {
                    result.add(new Tuple2(items[0].trim(), items[1].trim()))
                }
            }
            return result
        }

    }

# Sorting rows in table

As soon as you get a table generated by Timekeeper, you would feel like to sort it for better readability. Timekeeper is capable of it. Timekeeper supports sorting rows by multiple selected keys. It is possible to sort rows in either of ascending and descending order.

A few examples down here.

Here I use a term "Attributes" to categorise the column names such as "Case", "URL". "Attributes" does not include the recorded figures: size and duration.

You can find a sample code at

-   [TimekeeperDemoHttpInteraction.groovy](https://github.com/kazurayam/timekeeper/blob/master/src/test/groovy/com/kazurayam/timekeeper/demo/TimekeeperDemoHttpInteraction.groovy)

## Table sorted by Attributes

The sample code has this method:

        @Test
        void test_HTTPGetAndSaveResponse_sortByAttributes() {
            Timekeeper tk = new Timekeeper()
            Measurement interactions = new Measurement.Builder(
                    "get URL, save HTML into file", ["Case", "URL"])
                    .sortByAttributes().
                    build();
            tk.add(interactions)
            // interact with URL, save the HTML into files
            processURLs(urlList, outDir_, interactions)
            // print the report
            tk.report(outDir_.resolve("sortByAttributes.md"))
        }

Please note the following fragment:

                .sortByAttributes()

This fragment specifies Timekeeper to sort the rows by all Attributes of the table. If you have 2 or more attributes in the table, the left column has higher sorting priority than its right. In this example, the sorting key is : "Case" &gt; "URL".

Rows are sorted in ascending order unless the order is explicitly specified.

The output looks like this:

    ## get URL, save HTML into file

    |Case|URL|size|duration|graph|
    |:----|:----|----:|----:|:----|
    |case 1|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:03|`#`|
    |case 1|https://search.yahoo.co.jp/search?p=timekeeper|21,217|00:02|`#`|
    |case 1|https://www.google.com/search?q=timekeeper|7,165|00:02|`#`|
    |case 2|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:05|`#`|
    |case 2|https://www.google.com/search?q=timekeeper|7,165|00:05|`#`|
    |case 3|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:05|`#`|
    |Average|-|6,002|00:04| |

## Table sorted by Attributes, descending order

You can sort in descending order.

        @Test
        void test_HTTPGetAndSaveResponse_sortByAttributes_descending() {
            Timekeeper tk = new Timekeeper()
            Measurement interactions = new Measurement.Builder(
                    "get URL, save HTML into file", ["Case", "URL"])
                    .sortByAttributes(Measurement.ROW_ORDER.DESCENDING).
                    build();
            tk.add(interactions)
            // interact with URL, save the HTML into files
            processURLs(urlList, outDir_, interactions)
            // print the report
            tk.report(outDir_.resolve("sortByAttributes_descending.md"))
        }

Please note this fragment where you specify the descending order.

        .sortByAttributes(Measurement.ROW_ORDER.DESCENDING).

The output looks like this:

    ## get URL, save HTML into file

    |Case|URL|size|duration|graph|
    |:----|:----|----:|----:|:----|
    |case 3|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:02|`#`|
    |case 2|https://www.google.com/search?q=timekeeper|7,165|00:03|`#`|
    |case 2|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:02|`#`|
    |case 1|https://www.google.com/search?q=timekeeper|7,153|00:04|`#`|
    |case 1|https://search.yahoo.co.jp/search?p=timekeeper|21,217|00:02|`#`|
    |case 1|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:04|`#`|
    |Average|-|6,000|00:03| |

## Table sorted by selected items among Attributes

You can choose columns as sort key out of the Attributes.

        @Test
        void test_HTTPGetAndSaveResponse_sortByAttributes_URL() {
            Timekeeper tk = new Timekeeper()
            Measurement interactions = new Measurement.Builder(
                    "get URL, save HTML into file", ["Case", "URL"])
                    .sortByAttributes(["URL"]).
                    build();
            tk.add(interactions)
            // interact with URL, save the HTML into files
            processURLs(urlList, outDir_, interactions)
            // print the report
            tk.report(outDir_.resolve("sortByAttributes_URL.md"))
        }

Please note the following fragment:

    Measurement interactions = new Measurement.Builder(
        "get URL, save HTML into file",
        ["Case", "URL"])
                    .sortByAttributes(["URL"]).
                    build();

The table has 2 attributes "Case" and "URL". And you selected "URL" as the sort key.

The output will look like this:

    ## get URL, save HTML into file

    |Case|URL|size|duration|graph|
    |:----|:----|----:|----:|:----|
    |case 1|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:03|`#`|
    |case 2|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:01|`#`|
    |case 3|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:01|`#`|
    |case 1|https://search.yahoo.co.jp/search?p=timekeeper|21,136|00:05|`#`|
    |case 1|https://www.google.com/search?q=timekeeper|7,165|00:05|`#`|
    |case 2|https://www.google.com/search?q=timekeeper|7,165|00:02|`#`|
    |Average|-|5,989|00:03| |

## Table sorted by Duration

You can sort rows by duration.

        @Test
        void test_HTTPGetAndSaveResponse_sortByDuration_descending() {
            Timekeeper tk = new Timekeeper()
            Measurement interactions = new Measurement.Builder(
                    "get URL, save HTML into file", ["Case", "URL"])
                    .sortByDuration(Measurement.ROW_ORDER.DESCENDING).
                    build();
            tk.add(interactions)
            // interact with URL, save the HTML into files
            processURLs(urlList, outDir_, interactions)
            // print the report
            tk.report(outDir_.resolve("sortByDuration_descending.md"))
        }

The output is like this:

    ## get URL, save HTML into file

    |Case|URL|size|duration|graph|
    |:----|:----|----:|----:|:----|
    |case 2|https://www.google.com/search?q=timekeeper|7,165|00:05|`#`|
    |case 1|https://www.google.com/search?q=timekeeper|7,165|00:04|`#`|
    |case 1|https://search.yahoo.co.jp/search?p=timekeeper|21,217|00:04|`#`|
    |case 2|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:04|`#`|
    |case 1|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:04|`#`|
    |case 3|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:03|`#`|
    |Average|-|6,002|00:04| |

## Table sorted by Attributes, then by duration

You can sort rows by Attributes first, then secondly by duration. Perhaps this sorting condition is most useful.

        @Test
        void test_HTTPGetAndSaveResponse_sortByAttributesThenDuration() {
            Timekeeper tk = new Timekeeper()
            Measurement interactions = new Measurement.Builder(
                    "get URL, save HTML into file", ["Case", "URL"])
                    .sortByAttributesThenDuration(["URL"]).
                    build();
            tk.add(interactions)
            // interact with URL, save the HTML into files
            processURLs(urlList, outDir_, interactions)
            // print the report
            tk.report(outDir_.resolve("sortByAttributesThenDuration.md"))
        }

The output looks like this:

    ## get URL, save HTML into file

    |Case|URL|size|duration|graph|
    |:----|:----|----:|----:|:----|
    |case 2|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:01|`#`|
    |case 3|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:01|`#`|
    |case 1|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:02|`#`|
    |case 1|https://search.yahoo.co.jp/search?p=timekeeper|21,136|00:03|`#`|
    |case 1|https://www.google.com/search?q=timekeeper|7,153|00:03|`#`|
    |case 2|https://www.google.com/search?q=timekeeper|7,153|00:03|`#`|
    |Average|-|5,985|00:02| |

## Table sorted by Size

You can sort rows by size, of course.

        @Test
        void test_HTTPGetAndSaveResponse_sortBySize_ascending() {
            Timekeeper tk = new Timekeeper()
            Measurement interactions = new Measurement.Builder(
                    "get URL, save HTML into file", ["Case", "URL"])
                    .sortBySize(Measurement.ROW_ORDER.ASCENDING).
                    build();
            tk.add(interactions)
            // interact with URL, save the HTML into files
            processURLs(urlList, outDir_, interactions)
            // print the report
            tk.report(outDir_.resolve("sortBySize_ascending.md"))
        }

The output is like this:

    ## get URL, save HTML into file

    |Case|URL|size|duration|graph|
    |:----|:----|----:|----:|:----|
    |case 1|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:02|`#`|
    |case 2|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:02|`#`|
    |case 3|https://duckduckgo.com/?q=timekeeper&t=h_&ia=web|156|00:04|`#`|
    |case 1|https://www.google.com/search?q=timekeeper|7,165|00:04|`#`|
    |case 2|https://www.google.com/search?q=timekeeper|7,165|00:06|`#`|
    |case 1|https://search.yahoo.co.jp/search?p=timekeeper|21,217|00:03|`#`|
    |Average|-|6,002|00:03| |

## List of supported sortBy\*() methods

Please have a look at the source code of

-   [Measurement.Builder](https://github.com/kazurayam/timekeeper/blob/master/src/main/java/com/kazurayam/timekeeper/Measurement.java)

to find out full list of `sortBy*()` methods supported.

# Download

The artifact is available at the Maven Central repository:

-   <https://mvnrepository.com/artifact/com.kazurayam/timekeeper>

# Dependencies

Timekeeper was tested on Java8.

See `build.gradle` at <https://github.com/kazurayam/timekeeper/> for external dependencies.

# Repository

-   <https://github.com/kazurayam/timekeeper>
