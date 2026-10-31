# TestFire Selenium Automation Framework

Selenium WebDriver + TestNG + Maven framework using the **Page Object Model**, with **parallel
test execution** and **HTML test reports**, for the demo banking application
<http://demo.testfire.net/>.

## Business scenarios covered
| # | Step | Where |
|---|------|-------|
| 1 | Navigate to http://demo.testfire.net/ | `LandingPage.open()` |
| 2 | Log in as admin / admin (also jsmith / demo1234) | `LoginPage.loginAs()` |
| 3 | My Account tab -> pick a value in "View Account Details" -> Go | `BankPage.clickMyAccountTab()`, `MyAccountPage.viewAccountDetails()` |
| 4 | "Available Balance" in the "Balance Detail" table is not empty | `AccountBalanceTest`, `AccountDetailsPage.getAvailableBalance()` |
| 5 | Amount against "Electric Bill" in the "Debits" table | `ElectricBillTest`, `AccountDetailsPage.getDebitAmount()` |

## Tech stack
Java 17 | Selenium 4 | TestNG 7 | Maven | ExtentReports 5 | GitHub Actions

## Project structure
```
.
├── pom.xml
├── testng.xml                          # parallel="methods", thread-count="4"
├── .github/workflows/selenium-tests.yml # CI: headless Chrome + report upload
└── src
    ├── main
    │   ├── java/com/testfire/automation
    │   │   ├── config/ConfigReader.java       # config.properties + -D overrides
    │   │   ├── driver/DriverFactory.java      # ThreadLocal WebDriver (parallel-safe)
    │   │   ├── pages/                         # Page Objects
    │   │   │   ├── BasePage.java              # explicit-wait helpers
    │   │   │   ├── BankPage.java              # My Account tab + Sign Off (post-login pages)
    │   │   │   ├── LandingPage.java
    │   │   │   ├── LoginPage.java
    │   │   │   ├── MyAccountPage.java
    │   │   │   └── AccountDetailsPage.java
    │   │   ├── reports/                       # Extent report, run history, trend analysis
    │   │   └── utils/                         # CsvUtils, ScreenshotUtil
    │   └── resources/config.properties
    └── test
        ├── java/com/testfire/automation
        │   ├── base/BaseTest.java
        │   ├── listeners/TestListener.java    # report + screenshot on failure + history row
        │   └── tests/                         # LoginTest, AccountBalanceTest, ElectricBillTest, DataProviders
        └── resources/testdata/                # login-data.csv, account-data.csv, users-data.csv
```

## Prerequisites
- JDK 17+ and Maven 3.8+
- Chrome, Firefox or Edge (Selenium Manager downloads the matching driver; internet required)

## Run
```bash
mvn clean test                                    # Chrome, 4 parallel browsers
mvn clean test -Dheadless=true                    # headless
mvn clean test -Dbrowser=firefox -Dheadless=true
mvn clean test -Dexpected.electric.bill.amount='$100.00'   # also assert an exact amount
```
Change the number of concurrent browsers in `testng.xml` (`thread-count`, `data-provider-thread-count`).

## Reports
| Output | Location |
|---|---|
| Extent HTML report (screenshot embedded on failure) | `reports/extent/ExtentReport_<runId>.html` |
| TestNG / Surefire reports | `target/surefire-reports/` |
| Failure screenshots (PNG) | `reports/screenshots/` |
| Run history (appended every run) | `reports/history/results-history.csv` |

After a few runs, summarise failure trends (failure rate per test, flaky tests, pass rate per run):
```bash
mvn -q compile exec:java -Dexec.mainClass=com.testfire.automation.reports.FailureTrendAnalyzer
```
This writes `reports/failure-trend.html`.

## Design notes
- **Page Object Model**: locators and page actions are in `pages/`; tests only hold scenario steps and assertions. Page methods return the next page object, so tests read like the business steps.
- **Parallel execution**: `parallel="methods"` + `ThreadLocal<WebDriver>` gives every concurrent test its own browser. `@DataProvider(parallel = true)` runs data rows concurrently.
- **Data-driven**: CSV files feed the data providers, so adding a user or account is a one-line change.
- **Reporting**: `TestListener` builds the Extent report, attaches a screenshot on failure and records every result for trend analysis.
- **CI**: the GitHub Actions workflow runs the suite headless on each push and uploads the reports as an artifact.

## Troubleshooting
Locators are at the top of each page class. If a test fails with `TimeoutException`, open the page in
Chrome DevTools, inspect the element and update the `By` constant. The most likely places are the
"Available balance" label in `AccountDetailsPage` and the `#AccountLink` tab in `BankPage`.
The "Electric Bill" entry exists only on some accounts, so `ElectricBillTest` checks each account in turn.
