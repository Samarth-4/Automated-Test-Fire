package com.testfire.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.testfire.automation.driver.DriverFactory;
import com.testfire.automation.reports.ExtentManager;
import com.testfire.automation.reports.ResultHistoryWriter;
import com.testfire.automation.utils.ScreenshotUtil;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Hooks into every test result to (1) build the Extent HTML report, (2) attach a screenshot on failure
 * and (3) append a row to the run-history CSV that powers failure-trend analysis.
 * ThreadLocal keeps the per-test report node safe when tests run in parallel.
 */
public class TestListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();
    private final ExtentReports extent = ExtentManager.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        ExtentTest test = extent.createTest(displayName(result), description == null ? "" : description);
        test.assignCategory(result.getTestClass().getRealClass().getSimpleName());
        CURRENT.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        CURRENT.get().pass("Test passed");
        record(result, "PASS", null);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String base64 = ScreenshotUtil.captureBase64(DriverFactory.getDriver());
        ScreenshotUtil.saveToDisk(base64, displayName(result) + "_" + System.currentTimeMillis());

        if (base64 != null) {
            CURRENT.get().fail(result.getThrowable(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64, "Failure screenshot").build());
        } else {
            CURRENT.get().fail(result.getThrowable());
        }
        record(result, "FAIL", result.getThrowable() == null ? "" : result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = CURRENT.get();
        if (test == null) { // skipped before it started (e.g. configuration failure)
            test = extent.createTest(displayName(result));
            CURRENT.set(test);
        }
        String reason = result.getThrowable() == null ? "Skipped" : result.getThrowable().getMessage();
        test.skip(reason);
        record(result, "SKIP", reason);
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    private void record(ITestResult result, String status, String error) {
        long duration = result.getEndMillis() - result.getStartMillis();
        ResultHistoryWriter.append(result.getTestContext().getSuite().getName(),
                displayName(result), status, duration, error);
    }

    /** method name plus the first data-provider value (always a readable scenario label). */
    private String displayName(ITestResult result) {
        String name = result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        return (params != null && params.length > 0) ? name + "[" + params[0] + "]" : name;
    }
}
