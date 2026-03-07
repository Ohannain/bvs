package de.dhbw.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void formatReturnsGermanPattern() {
        LocalDate date = LocalDate.of(2024, 3, 5);
        assertEquals("05.03.2024", DateUtils.format(date));
    }

    @Test
    void formatNullReturnsEmpty() {
        assertEquals("", DateUtils.format(null));
    }

    @Test
    void formatISOReturnsISO() {
        LocalDate date = LocalDate.of(2024, 3, 5);
        assertEquals("2024-03-05", DateUtils.formatISO(date));
    }

    @Test
    void parseGermanFormat() {
        LocalDate result = DateUtils.parse("05.03.2024");
        assertEquals(LocalDate.of(2024, 3, 5), result);
    }

    @Test
    void parseISOFormat() {
        LocalDate result = DateUtils.parse("2024-03-05");
        assertEquals(LocalDate.of(2024, 3, 5), result);
    }

    @Test
    void parseNullReturnsNull() {
        assertNull(DateUtils.parse(null));
    }

    @Test
    void parseEmptyReturnsNull() {
        assertNull(DateUtils.parse("  "));
    }

    @Test
    void parseInvalidReturnsNull() {
        assertNull(DateUtils.parse("not-a-date"));
    }

    @Test
    void daysBetween() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end   = LocalDate.of(2024, 1, 11);
        assertEquals(10, DateUtils.daysBetween(start, end));
    }

    @Test
    void daysBetweenWithNullReturnsZero() {
        assertEquals(0, DateUtils.daysBetween(null, LocalDate.now()));
        assertEquals(0, DateUtils.daysBetween(LocalDate.now(), null));
    }

    @Test
    void isOverdueFutureDateReturnsFalse() {
        assertFalse(DateUtils.isOverdue(LocalDate.now().plusDays(5)));
    }

    @Test
    void isOverduePastDateReturnsTrue() {
        assertTrue(DateUtils.isOverdue(LocalDate.now().minusDays(1)));
    }

    @Test
    void isOverdueNullReturnsFalse() {
        assertFalse(DateUtils.isOverdue(null));
    }

    @Test
    void daysOverdueNotOverdueReturnsZero() {
        assertEquals(0, DateUtils.daysOverdue(LocalDate.now().plusDays(3)));
    }

    @Test
    void daysOverduePositive() {
        assertTrue(DateUtils.daysOverdue(LocalDate.now().minusDays(3)) >= 3);
    }

    @Test
    void addDays() {
        LocalDate base = LocalDate.of(2024, 1, 1);
        assertEquals(LocalDate.of(2024, 1, 11), DateUtils.addDays(base, 10));
    }

    @Test
    void addDaysNullUsesToday() {
        assertEquals(LocalDate.now().plusDays(5), DateUtils.addDays(null, 5));
    }

    @Test
    void today() {
        assertEquals(LocalDate.now(), DateUtils.today());
    }

    @Test
    void isFuture() {
        assertTrue(DateUtils.isFuture(LocalDate.now().plusDays(1)));
        assertFalse(DateUtils.isFuture(LocalDate.now().minusDays(1)));
        assertFalse(DateUtils.isFuture(null));
    }

    @Test
    void isPast() {
        assertTrue(DateUtils.isPast(LocalDate.now().minusDays(1)));
        assertFalse(DateUtils.isPast(LocalDate.now().plusDays(1)));
        assertFalse(DateUtils.isPast(null));
    }

    @Test
    void isToday() {
        assertTrue(DateUtils.isToday(LocalDate.now()));
        assertFalse(DateUtils.isToday(LocalDate.now().plusDays(1)));
        assertFalse(DateUtils.isToday(null));
    }
}
