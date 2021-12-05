package com.kazurayam.timekeeper

import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.OutputType;


import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * learned "How to Take Screenshot in Selenium WebDriver" of Guru99
 * https://www.guru99.com/take-screenshot-selenium-webdriver.html
 */
class TimeKeeperDemo {

    WebDriver driver;
    static Path outDir;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
        outDir = Paths.get(".")
                .resolve("build/tmp/testOutput")
                .resolve(TimeKeeperDemo.class.getSimpleName())
        if (Files.exists(outDir)) {
            outDir.toFile().deleteDir();
        }
        Files.createDirectory(outDir)
    }

    @BeforeEach
    void setupTest() {
        driver = new ChromeDriver();
    }

    @Test
    void test_demo() {
        driver.get("http://demo.guru99.com/V4/")
        Path screenshot = outDir.resolve("V4.png")
        this.takeScreenshot(driver, screenshot);
    }

    private void takeScreenshot(WebDriver driver, Path out) {
        //Convert web driver object to TakeScreenshot
        TakesScreenshot shooter =((TakesScreenshot)driver);
        //Call getScreenshotAs method to create image file
        File tmp = shooter.getScreenshotAs(OutputType.FILE);
        //Copy image into the destination
        Files.copy(tmp.toPath(), out);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
