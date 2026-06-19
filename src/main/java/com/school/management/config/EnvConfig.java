package com.school.management.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvConfig {
    private static final Map<String, String> properties = new HashMap<>();

    static {
        loadDotEnv();
    }

    private static void loadDotEnv() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            System.err.println(".env file not found at " + envFile.getAbsolutePath());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eqIdx = line.indexOf('=');
                if (eqIdx > 0) {
                    String key = line.substring(0, eqIdx).trim();
                    String value = line.substring(eqIdx + 1).trim();
                    // Strip enclosing quotes if any
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    } else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }
                    properties.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return properties.get(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
}
