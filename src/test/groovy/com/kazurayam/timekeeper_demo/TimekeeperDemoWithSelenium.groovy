package com.kazurayam.timekeeper_demo

import com.kazurayam.ashotwrapper.AShotWrapper
import com.kazurayam.ashotwrapper.DevicePixelRatioResolver
import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Table
import com.kazurayam.timekeeper.Timekeeper
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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

import static org.junit.jupiter.api.Assertions.*;

// this test could take minutes to finish
@Disabled
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
    void demo_with_selenium_report_Markdown() {
        Timekeeper tk = runSeleniumTest();
        tk.report(outDir_.resolve("report.md"))
        // same as
        //tk.report(outDir_.resolve("report.md"), Timekeeper.FORMAT.MARKDOWN)
    }

    @Test
    void demo_with_selenium_report_CSV() {
        Timekeeper tk = runSeleniumTest();
        tk.report(outDir_.resolve("report.csv"), Timekeeper.FORMAT.CSV)
    }

    Timekeeper runSeleniumTest() {
        Timekeeper tk = new Timekeeper()
        Measurement navigating = new Measurement.Builder(
                "How long it took to navigate to URLs", ["URL"])
                .build()
        tk.add(new Table.Builder(navigating)
                .noLegend()
                .build())
        Measurement screenshooting = new Measurement.Builder(
                "How long it took to take shootshots", ["URL"])
                .build()
        tk.add(new Table.Builder(screenshooting)
                .noLegend()
                .build())
        // process all URLs in the CSV file
        Path csv = Paths.get(".").resolve("src/test/fixtures/URLs.csv");
        for (Tuple t in parseCSVfile(csv)) {
            String url = t.get(0)
            String filename = t.get(1)
            driver_.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            // navigate to the URL, record the duration
            navigating.before(["URL": url])
            driver_.navigate().to(url)
            navigating.after()

            // take a screenshot of the page, record the duration
            screenshooting.before(["URL": url])
            Path imageFile = this.takeFullPageScreenshot(driver_, outDir_, filename)
            screenshooting.after(imageFile.toFile().size())
        }
        return tk
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
