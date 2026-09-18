package com.djapp.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    
private static Properties properties = new Properties();
    private static boolean loaded = false;

    /**
     * Loads config.properties from the resources folder.
     * Only loads once (cached after first call).
     */
    private static void load() {
        if (loaded) return;
        
        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (input == null) {
                System.err.println("❌ config.properties not found in resources folder!");
                return;
            }
            
            properties.load(input);
            loaded = true;
            System.out.println("✅ Config loaded.");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading config: " + e.getMessage());
        }
    }

    public static String get(String key) {
        load();
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        load();
        return properties.getProperty(key, defaultValue);
    }
}
