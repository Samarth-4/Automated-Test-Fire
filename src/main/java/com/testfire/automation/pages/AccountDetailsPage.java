package com.testfire.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/** Account page with the "Balance Detail", "Credits" and "Debits" tables. */
public class AccountDetailsPage extends BankPage {

    private static final String LOWER_CASE_TEXT =
            "translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";

    // Label cell "Available balance" and the value cell next to it (case-insensitive match).
    private static final By AVAILABLE_BALANCE_LABEL =
            By.xpath("//td[not(.//td)][contains(" + LOWER_CASE_TEXT + ",'available balance')]");
    private static final By AVAILABLE_BALANCE_VALUE =
            By.xpath("//td[not(.//td)][contains(" + LOWER_CASE_TEXT + ",'available balance')]/following-sibling::td[1]");

    public AccountDetailsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isElementVisible(AVAILABLE_BALANCE_LABEL);
    }

    /** Scenario step 4: text of the Available Balance cell in the Balance Detail table (may be empty). */
    public String getAvailableBalance() {
        WebElement cell = wait.until(ExpectedConditions.presenceOfElementLocated(AVAILABLE_BALANCE_VALUE));
        return cell.getText().trim();
    }

    public boolean hasDebit(String description) {
        return !findDebitRows(description).isEmpty();
    }

    /** Scenario step 5: the amount (last column) of the Debits row whose description matches. */
    public String getDebitAmount(String description) {
        List<WebElement> rows = findDebitRows(description);
        if (rows.isEmpty()) {
            throw new IllegalStateException("No debit row found for description: " + description);
        }
        List<WebElement> cells = rows.get(0).findElements(By.xpath("./td"));
        return cells.get(cells.size() - 1).getText().trim();
    }

    /**
     * Prefers the innermost table that contains the "Debits" heading. If the heading sits outside
     * the table, falls back to any row with that description (an electric bill is always a debit).
     */
    private List<WebElement> findDebitRows(String description) {
        String rowWithDescription = "//tr[td[contains(normalize-space(.),'" + description + "')]]";
        List<WebElement> rows = driver.findElements(By.xpath(
                "//table[not(.//table)][.//*[normalize-space()='Debits']]" + rowWithDescription));
        if (rows.isEmpty()) {
            rows = driver.findElements(By.xpath(rowWithDescription));
        }
        return rows;
    }
}
