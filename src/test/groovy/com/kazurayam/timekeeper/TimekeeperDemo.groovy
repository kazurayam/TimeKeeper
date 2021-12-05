package com.kazurayam.timekeeper

import com.kazurayam.ashotwrapper.AShotWrapper
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.Dimension
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.OutputType
import org.openqa.selenium.chrome.ChromeOptions
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.Screenshot
import ru.yandex.qatools.ashot.shooting.ShootingStrategy
import java.util.concurrent.TimeUnit;

import java.awt.image.BufferedImage;
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO

import static org.junit.jupiter.api.Assertions.*;

/**
 * learned "How to Take Screenshot in Selenium WebDriver" of Guru99
 * https://www.guru99.com/take-screenshot-selenium-webdriver.html
 */
class TimekeeperDemo {


    WebDriver driver;
    static Path outDir;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
        outDir = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(TimekeeperDemo.class.getSimpleName())
        if (Files.exists(outDir)) {
            outDir.toFile().deleteDir();
        }
        Files.createDirectory(outDir)
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        driver.manage().window().setSize(new Dimension(1200, 800));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void test_demo() {
        Path csv = Paths.get(".").resolve("src/test/fixtures/URLs.csv");
        for (Tuple t in getURLs(csv)) {
            driver.navigate().to(t.get(0))
            this.saveFullPageScreenshot(driver, outDir, t.get(1))
        }
    }

    private void saveFullPageScreenshot(WebDriver driver, Path outDir, String fileName) {
        BufferedImage image = AShotWrapper.takeEntirePageImage(driver,
                new AShotWrapper.Options.Builder().build());
        assertNotNull(image);
        File screenshotFile = outDir.resolve(fileName).toFile();
        ImageIO.write(image, "PNG", screenshotFile);
        assertTrue(screenshotFile.exists());
    }

    private List<Tuple2> getURLs(Path csv) {
        List<Tuple2> result = new ArrayList<Tuple2>()
        List<String> lines = csv.toFile() as List<String>
        for (String line in lines) {
            String[] items = line.split(",")
            if (items.size() >= 2) {
                result.add(new Tuple2(items[0], items[1]))
            }
        }
        return result
    }

}
