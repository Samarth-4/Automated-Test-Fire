package com.testfire.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Base class for pages shown after login: owns the "My Account" tab and the Sign Off link. */
public abstract class BankPage extends BasePage {

    // Id first, href as fallback: either one is enough to find the tab.
    private static final By MY_ACCOUNT_TAB = By.cssSelector("#AccountLink, a[href*='main.jsp']");
    private static final By SIGN_OFF_LINK = By.id("LoginLink");

    protected BankPage(WebDriver driver) {
        super(driver);
    }

    /** Scenario step 3: the "My Account" tab. */
    public MyAccountPage clickMyAccountTab() {
        click(MY_ACCOUNT_TAB);
        return new MyAccountPage(driver);
    }

    public boolean isSignedIn() {
        return isElementVisible(SIGN_OFF_LINK)
                && getText(SIGN_OFF_LINK).toLowerCase().contains("sign off");
    }

    public LoginPage signOff() {
        click(SIGN_OFF_LINK);
        return new LoginPage(driver);
    }
}
