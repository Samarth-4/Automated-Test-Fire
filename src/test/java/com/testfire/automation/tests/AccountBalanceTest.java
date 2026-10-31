package com.testfire.automation.tests;

import com.testfire.automation.base.BaseTest;
import com.testfire.automation.pages.AccountDetailsPage;
import com.testfire.automation.pages.MyAccountPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Business scenario:
 *  1. Navigate to the application
 *  2. Log in
 *  3. My Account tab -> pick a value in "View Account Details" -> Go
 *  4. "Available Balance" in the "Balance Detail" table must not be empty
 */
public class AccountBalanceTest extends BaseTest {

    @Test(dataProvider = "accountData", dataProviderClass = DataProviders.class,
            description = "Available Balance is not empty in the Balance Detail table")
    public void availableBalanceShouldNotBeEmpty(String scenario, String username, String password,
                                                 String accountIndex) {
        int index = Integer.parseInt(accountIndex);

        MyAccountPage myAccount = loginAs(username, password);                       // steps 1-2
        Assert.assertTrue(myAccount.isLoaded(), "My Account page should load for " + username);

        myAccount = myAccount.clickMyAccountTab();                                   // step 3
        List<String> accounts = myAccount.getAccountNames();
        if (index >= accounts.size()) {
            throw new SkipException(username + " has only " + accounts.size() + " accounts; index " + index + " skipped");
        }

        AccountDetailsPage details = myAccount.viewAccountDetails(index);            // step 3 (Go)
        Assert.assertTrue(details.isLoaded(),
                "Balance Detail table should load for account " + accounts.get(index));

        String availableBalance = details.getAvailableBalance();                     // step 4
        Assert.assertFalse(availableBalance.isBlank(),
                "Available Balance should not be empty for account " + accounts.get(index));
        Assert.assertTrue(availableBalance.matches(".*\\d.*"),
                "Available Balance should contain a number but was '" + availableBalance + "'");
    }
}
