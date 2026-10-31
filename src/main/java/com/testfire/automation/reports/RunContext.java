package com.testfire.automation.reports;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One id per JVM run; ties together the HTML report and the rows written to the history CSV. */
public final class RunContext {

    public static final String RUN_ID =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

    private RunContext() {
    }
}
