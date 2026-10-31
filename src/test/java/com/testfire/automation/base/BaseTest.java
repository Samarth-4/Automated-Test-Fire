package com.testfire.automation.base;

import com.testfire.automation.config.ConfigReader;
import com.testfire.automation.driver.DriverFactory;
import com.testfire.automation.pages.LandingPage;
import com.testfire.automation.pages.MyAccountPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Every test method gets a fresh browser session. Because DriverFactory is ThreadLocal,
 * methods running in parallel never share a browser.
 */
public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    /** Scenario steps 1-2: open the application and sign in. */
    protected MyAccountPage loginAs(String username, String password) {
        return new LandingPage(driver()).open().clickSignIn().loginAs(username, password);
    }

    protected MyAccountPage loginAsDefaultUser() {
        return loginAs(ConfigReader.get("valid.username"), ConfigReader.get("valid.password"));
    }
}
