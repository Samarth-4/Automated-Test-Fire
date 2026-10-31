package com.testfire.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/** "My Account" page with the "View Account Details" drop-down and the Go button. */
public class MyAccountPage extends BankPage {

    private static final By ACCOUNT_DROPDOWN = By.id("listAccounts");
    private static final By GO_BUTTON = By.id("btnGetAccount");

    public MyAccountPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            return wait.until(ExpectedConditions.urlContains("main.jsp"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public List<String> getAccountNames() {
        return getDropdownOptions(ACCOUNT_DROPDOWN);
    }

    /** Scenario steps 3-4: choose a value in "View Account Details" and click "Go". */
    public AccountDetailsPage viewAccountDetails(int accountIndex) {
        selectByIndex(ACCOUNT_DROPDOWN, accountIndex);
        click(GO_BUTTON);
        return new AccountDetailsPage(driver);
    }
}
