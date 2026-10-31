package com.testfire.automation.pages;

import com.testfire.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/** Common Selenium helpers shared by all page objects (explicit waits, no Thread.sleep). */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText().trim();
    }

    /** True if the element becomes visible within the short wait; never throws. */
    protected boolean isElementVisible(By locator) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("short.wait.seconds")))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void selectByIndex(By locator, int index) {
        new Select(waitVisible(locator)).selectByIndex(index);
    }

    protected List<String> getDropdownOptions(By locator) {
        return new Select(waitVisible(locator)).getOptions().stream()
                .map(option -> option.getText().trim())
                .collect(Collectors.toList());
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
