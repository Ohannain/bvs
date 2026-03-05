package de.dhbw.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    public static String format(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DEFAULT_FORMATTER);
    }
    
    public static String formatISO(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(ISO_FORMATTER);
    }
    
    public static LocalDate parse(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DEFAULT_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateString, ISO_FORMATTER);
            } catch (DateTimeParseException ex) {
                Logger.error("Failed to parse date: " + dateString);
                return null;
            }
        }
    }
    
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }
    
    public static boolean isOverdue(LocalDate dueDate) {
        if (dueDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }
    
    public static long daysOverdue(LocalDate dueDate) {
        if (!isOverdue(dueDate)) {
            return 0;
        }
        return daysBetween(dueDate, LocalDate.now());
    }
    
    public static LocalDate addDays(LocalDate date, int days) {
        if (date == null) {
            return LocalDate.now().plusDays(days);
        }
        return date.plusDays(days);
    }
    
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    public static boolean isFuture(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }
    
    public static boolean isPast(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(LocalDate.now());
    }
    
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now());
    }
}
