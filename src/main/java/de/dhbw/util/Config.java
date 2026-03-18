package de.dhbw.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();
    
    public static final String DATA_DIR = "data/";
    public static final String USERS_FILE = DATA_DIR + "users.json";
    public static final String MEDIA_FILE = DATA_DIR + "media.json";
    public static final String LOANS_FILE = DATA_DIR + "loans.json";
    public static final String FINES_FILE = DATA_DIR + "fines.json";
    public static final String RESERVATIONS_FILE = DATA_DIR + "reservations.json";
    public static final String REPORTS_FILE = DATA_DIR + "reports.json";
    
    public static final int DEFAULT_BOOK_LOAN_DAYS = 30;
    public static final int DEFAULT_DVD_LOAN_DAYS = 7;
    public static final int DEFAULT_CD_LOAN_DAYS = 14;
    public static final int DEFAULT_MAGAZINE_LOAN_DAYS = 7;
    
    public static final double DEFAULT_FINE_RATE_PER_DAY = 0.50;
    public static final double MAX_OUTSTANDING_FINES = 50.0;
    public static final int MAX_BORROW_LIMIT = 5;
    public static final int MAX_RENEWALS = 3;
    public static final int RESERVATION_EXPIRY_DAYS = 7;
    
    public static final int WARNING_THRESHOLD = 3;
    public static final int SUSPENSION_DAYS = 30;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            Logger.info("Configuration loaded from " + CONFIG_FILE);
        } catch (IOException e) {
            Logger.warn("Could not load config file, using defaults: " + e.getMessage());
        }
    }
    
    /**
     * Returns the string property.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Returns the int property.
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Returns the double property.
     */
    public static double getDoubleProperty(String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Returns the boolean property.
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
