package com.kazurayam.timekeeper.demo

import com.kazurayam.ashotwrapper.AShotWrapper
import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Record
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


    WebDriver driver_
    static Path outdir_

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
        Measurement ms = tk.newMeasurement("How long it takes to take screenshot", ["URL"])
        //
        Path csv = Paths.get(".").resolve("src/test/fixtures/URLs.csv");
        for (Tuple t in getURLs(csv)) {
            String url = t.get(0)
            String filename = t.get(1)
            driver_.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
            driver_.navigate().to(url)
            //
            Record rc = ms.formRecord()
            rc.putAttribute("URL", url)
            rc.setStartAt(LocalDateTime.now())
            //
            this.saveFullPageScreenshot(driver_, outdir_, filename)
            //
            rc.setEndAt(LocalDateTime.now())
            ms.add(rc)
        }
        tk.report(outdir_.resolve("report.md"))
    }

    private void saveFullPageScreenshot(WebDriver driver, Path outDir, String fileName) {
        BufferedImage image = AShotWrapper.takeEntirePageImage(driver,
                new AShotWrapper.Options.Builder().build());
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
    private List<Tuple2> getURLs(Path csv) {
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
