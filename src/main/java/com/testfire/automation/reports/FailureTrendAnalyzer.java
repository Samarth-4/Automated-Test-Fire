package com.testfire.automation.reports;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Reads the accumulated run history and surfaces failure trends:
 * which tests fail most often, which are flaky, and how each run compares.
 *
 * Run after a few test executions:
 *   mvn -q compile exec:java -Dexec.mainClass=com.testfire.automation.reports.FailureTrendAnalyzer
 */
public final class FailureTrendAnalyzer {

    private FailureTrendAnalyzer() {
    }

    public static void main(String[] args) throws IOException {
        Path history = ResultHistoryWriter.historyFile().toPath();
        if (!Files.exists(history)) {
            System.out.println("No history found at " + history.toAbsolutePath() + ". Run the tests first.");
            return;
        }

        List<String> lines = Files.readAllLines(history, StandardCharsets.UTF_8);
        Map<String, int[]> perTest = new TreeMap<>();            // [executions, failures]
        Map<String, String> lastStatus = new TreeMap<>();
        Map<String, int[]> perRun = new LinkedHashMap<>();       // [total, failed]

        for (int i = 1; i < lines.size(); i++) {
            String[] c = lines.get(i).split(",", -1);
            if (c.length < 7) {
                continue;
            }
            String runId = c[0];
            String test = c[3];
            String status = c[4];

            int[] t = perTest.computeIfAbsent(test, k -> new int[2]);
            t[0]++;
            if ("FAIL".equals(status)) {
                t[1]++;
            }
            lastStatus.put(test, status);

            int[] r = perRun.computeIfAbsent(runId, k -> new int[2]);
            r[0]++;
            if ("FAIL".equals(status)) {
                r[1]++;
            }
        }

        List<Map.Entry<String, int[]>> ranked = new ArrayList<>(perTest.entrySet());
        ranked.sort(Comparator.comparingDouble((Map.Entry<String, int[]> e) -> rate(e.getValue())).reversed());

        printConsole(ranked, lastStatus, perRun);
        Path html = ResultHistoryWriter.historyFile().toPath().getParent().getParent().resolve("failure-trend.html");
        Files.writeString(html, buildHtml(ranked, lastStatus, perRun), StandardCharsets.UTF_8);
        System.out.println("\nHTML summary written to " + html.toAbsolutePath());
    }

    private static double rate(int[] counts) {
        return counts[0] == 0 ? 0 : (counts[1] * 100.0) / counts[0];
    }

    private static boolean isFlaky(int[] counts) {
        return counts[1] > 0 && counts[1] < counts[0];
    }

    private static void printConsole(List<Map.Entry<String, int[]>> ranked, Map<String, String> last,
                                     Map<String, int[]> perRun) {
        System.out.println("\n=== Failure trend by test ===");
        System.out.printf("%-55s %5s %5s %7s %-7s %s%n", "Test", "Runs", "Fail", "Fail%", "Last", "Note");
        for (Map.Entry<String, int[]> e : ranked) {
            int[] c = e.getValue();
            System.out.printf("%-55s %5d %5d %6.1f%% %-7s %s%n", e.getKey(), c[0], c[1], rate(c),
                    last.get(e.getKey()), isFlaky(c) ? "FLAKY" : "");
        }
        System.out.println("\n=== Pass rate by run ===");
        for (Map.Entry<String, int[]> e : perRun.entrySet()) {
            int total = e.getValue()[0];
            int failed = e.getValue()[1];
            System.out.printf("%-17s total=%-4d failed=%-4d pass rate=%.1f%%%n", e.getKey(), total, failed,
                    total == 0 ? 0 : (total - failed) * 100.0 / total);
        }
    }

    private static String buildHtml(List<Map.Entry<String, int[]>> ranked, Map<String, String> last,
                                    Map<String, int[]> perRun) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Failure Trends</title><style>")
                .append("body{font-family:Arial,sans-serif;margin:24px}table{border-collapse:collapse;margin-bottom:32px}")
                .append("th,td{border:1px solid #ccc;padding:6px 12px;text-align:left}th{background:#f0f0f0}")
                .append(".bad{background:#fdd}.flaky{background:#ffe9b3}</style></head><body>")
                .append("<h1>TestFire - Failure Trends</h1><h2>By test</h2><table>")
                .append("<tr><th>Test</th><th>Runs</th><th>Failures</th><th>Failure %</th><th>Last status</th><th>Note</th></tr>");
        for (Map.Entry<String, int[]> e : ranked) {
            int[] c = e.getValue();
            String css = isFlaky(c) ? "flaky" : (c[1] > 0 ? "bad" : "");
            sb.append(String.format("<tr class='%s'><td>%s</td><td>%d</td><td>%d</td><td>%.1f%%</td><td>%s</td><td>%s</td></tr>",
                    css, esc(e.getKey()), c[0], c[1], rate(c), esc(last.get(e.getKey())), isFlaky(c) ? "FLAKY" : ""));
        }
        sb.append("</table><h2>By run</h2><table><tr><th>Run ID</th><th>Total</th><th>Failed</th><th>Pass rate</th></tr>");
        for (Map.Entry<String, int[]> e : perRun.entrySet()) {
            int total = e.getValue()[0];
            int failed = e.getValue()[1];
            sb.append(String.format("<tr><td>%s</td><td>%d</td><td>%d</td><td>%.1f%%</td></tr>",
                    esc(e.getKey()), total, failed, total == 0 ? 0 : (total - failed) * 100.0 / total));
        }
        return sb.append("</table></body></html>").toString();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
