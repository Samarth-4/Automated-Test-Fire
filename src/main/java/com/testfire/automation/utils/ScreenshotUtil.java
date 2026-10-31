package com.testfire.automation.utils;

import com.testfire.automation.config.ConfigReader;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    /** Returns the screenshot as Base64 so it can be embedded directly in the HTML report. */
    public static String captureBase64(WebDriver driver) {
        if (driver == null) {
            return null;
        }
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            return null;
        }
    }

    /** Also keeps a PNG copy on disk under reports/screenshots. */
    public static void saveToDisk(String base64, String fileName) {
        if (base64 == null) {
            return;
        }
        try {
            Path dir = Paths.get(ConfigReader.get("report.dir"), "screenshots");
            Files.createDirectories(dir);
            String safeName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.write(dir.resolve(safeName + ".png"), Base64.getDecoder().decode(base64));
        } catch (IOException ignored) {
            // A failed screenshot copy must never fail the test run.
        }
    }
}
