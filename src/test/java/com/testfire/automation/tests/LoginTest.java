package com.testfire.automation.tests;

import com.testfire.automation.base.BaseTest;
import com.testfire.automation.pages.LandingPage;
import com.testfire.automation.pages.LoginPage;
import com.testfire.automation.pages.MyAccountPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(dataProvider = "loginData", dataProviderClass = DataProviders.class,
            description = "Data-driven login: valid and invalid credentials")
    public void loginDataDriven(String scenario, String username, String password, String expected) {
        LoginPage login = new LandingPage(driver()).open().clickSignIn();

        if ("success".equalsIgnoreCase(expected)) {
            MyAccountPage myAccount = login.loginAs(username, password);
            Assert.assertTrue(myAccount.isLoaded(), "My Account page should load for " + username);
            Assert.assertTrue(myAccount.isSignedIn(), "Sign Off link should be visible for " + username);
        } else {
            login.loginExpectingFailure(username, password);
            Assert.assertTrue(login.isErrorDisplayed(), "Login error should be shown for scenario " + scenario);
        }
    }

    @Test(description = "Signing off returns the user to the login page")
    public void signOffReturnsToLogin() {
        MyAccountPage myAccount = loginAsDefaultUser();
        Assert.assertTrue(myAccount.isLoaded(), "My Account page should load after login");

        myAccount.signOff();
        Assert.assertTrue(driver().getCurrentUrl().contains("login.jsp"),
                "Expected to land on the login page but was " + driver().getCurrentUrl());
    }
}
