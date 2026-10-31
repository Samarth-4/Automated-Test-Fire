package com.testfire.automation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Minimal CSV reader for data-driven tests (first row is the header, '#' starts a comment). */
public final class CsvUtils {

    private CsvUtils() {
    }

    public static Object[][] read(String classpathResource) {
        try (InputStream in = CsvUtils.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalArgumentException("Test data not found on classpath: " + classpathResource);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            List<Object[]> rows = new ArrayList<>();
            boolean headerSkipped = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] cells = line.split(",", -1);
                for (int i = 0; i < cells.length; i++) {
                    cells[i] = cells[i].trim();
                }
                rows.add(cells);
            }
            return rows.toArray(new Object[0][]);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read " + classpathResource, e);
        }
    }
}
