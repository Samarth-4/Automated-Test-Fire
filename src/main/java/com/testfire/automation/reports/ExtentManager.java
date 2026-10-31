package com.testfire.automation.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.testfire.automation.config.ConfigReader;

import java.io.File;

public final class ExtentManager {

    private static ExtentReports extent;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String dir = ConfigReader.get("report.dir") + File.separator + "extent";
            new File(dir).mkdirs();
            String path = dir + File.separator + "ExtentReport_" + RunContext.RUN_ID + ".html";

            ExtentSparkReporter spark = new ExtentSparkReporter(path);
            spark.config().setReportName("TestFire Automation Results");
            spark.config().setDocumentTitle("TestFire Test Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Run ID", RunContext.RUN_ID);
            extent.setSystemInfo("Base URL", ConfigReader.get("base.url"));
            extent.setSystemInfo("Browser", ConfigReader.get("browser"));
            extent.setSystemInfo("Headless", ConfigReader.get("headless"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
        }
        return extent;
    }
}
