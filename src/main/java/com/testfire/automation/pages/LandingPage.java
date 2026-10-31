package com.testfire.automation.pages;

import com.testfire.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Public home page of the application (scenario step 1). */
public class LandingPage extends BasePage {

    private static final By SIGN_IN_LINK = By.id("LoginLink");

    public LandingPage(WebDriver driver) {
        super(driver);
    }

    public LandingPage open() {
        driver.get(ConfigReader.get("base.url") + "/");
        return this;
    }

    public LoginPage clickSignIn() {
        click(SIGN_IN_LINK);
        return new LoginPage(driver);
    }
}
