package de.dhbw.util;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Human-readable ID value object used across the application.
 *
 * Examples: USR00001, MED00142, LON00077.
 */
public final class UUID implements Comparable<UUID>, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Map<String, AtomicInteger> COUNTERS = new ConcurrentHashMap<>();
    private final String value;

    private UUID(String value) {
        this.value = value;
    }

    public static UUID fromString(String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("ID must not be empty");
        }

        return new UUID(normalized.toUpperCase(Locale.ROOT));
    }

    public static UUID randomUUID() {
        return next("ID");
    }

    public static UUID nameUUIDFromBytes(byte[] input) {
        if (input == null || input.length == 0) {
            return randomUUID();
        }

        int hash = Math.abs(java.util.Arrays.hashCode(input));
        return new UUID(String.format(Locale.ROOT, "ID%05d", hash % 100000));
    }

    public static UUID next(String prefix) {
        String normalizedPrefix = normalizePrefix(prefix);
        AtomicInteger counter = COUNTERS.computeIfAbsent(normalizedPrefix, key -> new AtomicInteger(0));
        int nextValue = counter.incrementAndGet();

        return new UUID(String.format(Locale.ROOT, "%s%05d", normalizedPrefix, nextValue));
    }

    public static UUID nextUserId() {
        return next("USR");
    }

    public static UUID nextMediaId() {
        return next("MED");
    }

    public static UUID nextLoanId() {
        return next("LON");
    }

    public static UUID nextFineId() {
        return next("FIN");
    }

    public static UUID nextReservationId() {
        return next("RSV");
    }

    public static UUID nextReportId() {
        return next("RPT");
    }

    public byte[] getBytes() {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(UUID other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UUID)) {
            return false;
        }
        UUID that = (UUID) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private static String normalizePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix must not be null");
        }

        String normalized = prefix.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z]", "");
        if (normalized.length() < 2 || normalized.length() > 5) {
            throw new IllegalArgumentException("Prefix must contain 2-5 letters");
        }

        return normalized;
    }
}
