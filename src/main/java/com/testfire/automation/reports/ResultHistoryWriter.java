package com.testfire.automation.reports;

import com.testfire.automation.config.ConfigReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appends one row per test result to reports/history/results-history.csv.
 * The file is kept between runs, which is what lets FailureTrendAnalyzer spot trends.
 */
public final class ResultHistoryWriter {

    public static final String HEADER = "runId,timestamp,suite,test,status,durationMs,error";
    private static final Object LOCK = new Object();

    private ResultHistoryWriter() {
    }

    public static File historyFile() {
        return new File(ConfigReader.get("report.dir") + File.separator + "history"
                + File.separator + "results-history.csv");
    }

    public static void append(String suite, String test, String status, long durationMs, String error) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String row = String.join(",",
                RunContext.RUN_ID, timestamp, clean(suite), clean(test), status,
                String.valueOf(durationMs), clean(error));

        File file = historyFile();
        synchronized (LOCK) { // tests finish on different threads
            try {
                file.getParentFile().mkdirs();
                boolean isNew = !file.exists();
                try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
                    if (isNew) {
                        out.println(HEADER);
                    }
                    out.println(row);
                }
            } catch (IOException e) {
                System.err.println("Could not write run history: " + e.getMessage());
            }
        }
    }

    /** Keeps each value on one line with no commas so the file stays trivially parseable. */
    private static String clean(String value) {
        if (value == null) {
            return "";
        }
        String single = value.replaceAll("[\\r\\n]+", " ").replace(',', ';').trim();
        return single.length() > 160 ? single.substring(0, 160) : single;
    }
}
