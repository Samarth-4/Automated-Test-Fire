package com.testfire.automation.tests;

import com.testfire.automation.base.BaseTest;
import com.testfire.automation.config.ConfigReader;
import com.testfire.automation.pages.AccountDetailsPage;
import com.testfire.automation.pages.MyAccountPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Business scenario step 5: the amount against the "Electric Bill" description in the
 * "Debits" table must be present. Accounts are checked one by one because the entry
 * only exists on some of them.
 */
public class ElectricBillTest extends BaseTest {

    private static final String DESCRIPTION = "Electric Bill";

    @Test(dataProvider = "userData", dataProviderClass = DataProviders.class,
            description = "Electric Bill amount is shown in the Debits table")
    public void electricBillAmountShouldBeShown(String scenario, String username, String password) {
        MyAccountPage myAccount = loginAs(username, password);
        Assert.assertTrue(myAccount.isLoaded(), "My Account page should load for " + username);

        myAccount = myAccount.clickMyAccountTab();
        List<String> accounts = myAccount.getAccountNames();

        String amount = null;
        String foundIn = null;
        for (int i = 0; i < accounts.size(); i++) {
            AccountDetailsPage details = myAccount.viewAccountDetails(i);
            if (details.isLoaded() && details.hasDebit(DESCRIPTION)) {
                amount = details.getDebitAmount(DESCRIPTION);
                foundIn = accounts.get(i);
                break;
            }
            myAccount = details.clickMyAccountTab();
        }

        Assert.assertNotNull(amount,
                "No '" + DESCRIPTION + "' entry in the Debits table for any account of " + username
                        + " (checked: " + accounts + ")");
        Assert.assertFalse(amount.isBlank(), "Amount for '" + DESCRIPTION + "' in " + foundIn + " is empty");
        Assert.assertTrue(amount.matches(".*\\d.*"),
                "Amount for '" + DESCRIPTION + "' should contain a number but was '" + amount + "'");

        String expected = ConfigReader.get("expected.electric.bill.amount");
        if (!expected.isBlank()) {
            Assert.assertEquals(amount, expected, "Unexpected Electric Bill amount in " + foundIn);
        }
    }
}
