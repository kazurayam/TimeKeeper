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

/**
 * learned "How to Take Screenshot in Selenium WebDriver" of Guru99
 * https://www.guru99.com/take-screenshot-selenium-webdriver.html
 */
class TimekeeperDemo {

    private WebDriver driver_
    static private Path outdir_
    private AShotWrapper.Options aswOptions_ = null

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
        outdir_ = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(TimekeeperDemo.class.getSimpleName())
        if (Files.exists(outdir_)) {
            outdir_.toFile().deleteDir();
        }
        Files.createDirectory(outdir_)
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
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
    void test_demo() {
        Timekeeper tk = new Timekeeper()
        Measurement navigation = tk.newMeasurement("How long it took to navigate to URLs", ["URL"])
        Measurement screenshot = tk.newMeasurement("How long it took to take shootshots", ["URL"])
        // process all URLs in the CSV file
        Path csv = Paths.get(".").resolve("src/test/fixtures/URLs.csv");
        for (Tuple t in parseCSVfile(csv)) {
            String url = t.get(0)
            String filename = t.get(1)
            driver_.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
            // navigate to the URL, record the duration
            LocalDateTime beforeNavigate = LocalDateTime.now()
            driver_.navigate().to(url)
            LocalDateTime afterNavigate = LocalDateTime.now()
            navigation.record(["URL": url], beforeNavigate, afterNavigate)
            // take a screenshot of the page, record the duration
            LocalDateTime beforeScreenshot = LocalDateTime.now()
            this.takeFullPageScreenshot(driver_, outdir_, filename)
            LocalDateTime afterScreenshot = LocalDateTime.now()
            screenshot.record(["URL": url], beforeScreenshot, afterScreenshot)
        }
        // now print the report
        tk.report(outdir_.resolve("report.md"))
    }

    private void takeFullPageScreenshot(WebDriver driver, Path outDir, String fileName) {
        // using my AShotWrapper lib at https://kazurayam.github.io/ashotwrapper/
        BufferedImage image = AShotWrapper.takeEntirePageImage(driver, aswOptions_);
        assertNotNull(image);
        File screenshotFile = outDir.resolve(fileName).toFile();
        ImageIO.write(image, "PNG", screenshotFile);
        assertTrue(screenshotFile.exists());
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
