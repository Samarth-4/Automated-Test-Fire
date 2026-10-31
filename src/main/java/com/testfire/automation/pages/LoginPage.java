package com.testfire.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("uid");
    private static final By PASSWORD = By.id("passw");
    private static final By LOGIN_BUTTON = By.name("btnSubmit");
    private static final By ERROR_MESSAGE = By.xpath("//*[contains(text(),'Login Failed')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Logs in and returns the "My Account" page (use for credentials that should succeed). */
    public MyAccountPage loginAs(String username, String password) {
        submitCredentials(username, password);
        return new MyAccountPage(driver);
    }

    /** Submits credentials expected to fail and stays on the login page. */
    public LoginPage loginExpectingFailure(String username, String password) {
        submitCredentials(username, password);
        return this;
    }

    public boolean isErrorDisplayed() {
        return isElementVisible(ERROR_MESSAGE);
    }

    private void submitCredentials(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }
}
