package com.testfire.automation.tests;

import com.testfire.automation.utils.CsvUtils;
import org.testng.annotations.DataProvider;

/** Test data lives in src/test/resources/testdata/*.csv. The first column is always a readable scenario label. */
public class DataProviders {

    @DataProvider(name = "loginData", parallel = true)
    public Object[][] loginData() {
        return CsvUtils.read("testdata/login-data.csv");
    }

    @DataProvider(name = "accountData", parallel = true)
    public Object[][] accountData() {
        return CsvUtils.read("testdata/account-data.csv");
    }

    @DataProvider(name = "userData", parallel = true)
    public Object[][] userData() {
        return CsvUtils.read("testdata/users-data.csv");
    }
}
