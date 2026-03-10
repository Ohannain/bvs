package de.dhbw.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "library.log";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static boolean enableConsoleOutput = true;
    private static boolean enableFileOutput = false;
    
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }
    
    private static Level currentLevel = Level.INFO;
    
    public static void setLevel(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("Log level cannot be null");
        }
        currentLevel = level;
    }
    
    public static void setConsoleOutput(boolean enabled) {
        enableConsoleOutput = enabled;
    }
    
    public static void setFileOutput(boolean enabled) {
        enableFileOutput = enabled;
    }
    
    private static void log(Level level, String message) {
        if (level.ordinal() < currentLevel.ordinal()) {
            return;
        }
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);
        
        if (enableConsoleOutput) {
            if (level == Level.ERROR || level == Level.WARN) {
                System.err.println(logMessage);
            } else {
                System.out.println(logMessage);
            }
        }
        
        if (enableFileOutput) {
            writeToFile(logMessage);
        }
    }
    
    private static void writeToFile(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    public static void debug(String message) {
        log(Level.DEBUG, message);
    }
    
    public static void info(String message) {
        log(Level.INFO, message);
    }
    
    public static void warn(String message) {
        log(Level.WARN, message);
    }
    
    public static void error(String message) {
        log(Level.ERROR, message);
    }
    
    public static void error(String message, Throwable throwable) {
        if (throwable == null) {
            log(Level.ERROR, message);
            return;
        }
        log(Level.ERROR, message + " - " + throwable.getMessage());
        if (enableConsoleOutput) {
            throwable.printStackTrace();
        }
        if (enableFileOutput) {
            try (FileWriter fw = new FileWriter(LOG_FILE, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                throwable.printStackTrace(pw);
            } catch (IOException e) {
                System.err.println("Failed to write stack trace to log file: " + e.getMessage());
            }
        }
    }
}
