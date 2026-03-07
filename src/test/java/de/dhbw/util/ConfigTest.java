package de.dhbw.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void dataDirConstant() {
        assertEquals("data/", Config.DATA_DIR);
    }

    @Test
    void filePathConstants() {
        assertEquals("data/users.json", Config.USERS_FILE);
        assertEquals("data/media.json", Config.MEDIA_FILE);
        assertEquals("data/loans.json", Config.LOANS_FILE);
        assertEquals("data/fines.json", Config.FINES_FILE);
        assertEquals("data/reservations.json", Config.RESERVATIONS_FILE);
        assertEquals("data/reports.json", Config.REPORTS_FILE);
    }

    @Test
    void loanDayDefaults() {
        assertEquals(30, Config.DEFAULT_BOOK_LOAN_DAYS);
        assertEquals(7,  Config.DEFAULT_DVD_LOAN_DAYS);
        assertEquals(14, Config.DEFAULT_CD_LOAN_DAYS);
        assertEquals(7,  Config.DEFAULT_MAGAZINE_LOAN_DAYS);
    }

    @Test
    void fineDefaults() {
        assertEquals(0.50, Config.DEFAULT_FINE_RATE_PER_DAY, 0.001);
        assertEquals(50.0, Config.MAX_OUTSTANDING_FINES, 0.001);
    }

    @Test
    void borrowLimits() {
        assertEquals(5, Config.MAX_BORROW_LIMIT);
        assertEquals(3, Config.MAX_RENEWALS);
        assertEquals(7, Config.RESERVATION_EXPIRY_DAYS);
    }

    @Test
    void getPropertyReturnsDefault() {
        assertEquals("fallback", Config.getProperty("nonexistent.key", "fallback"));
    }

    @Test
    void getIntPropertyReturnsDefault() {
        assertEquals(42, Config.getIntProperty("nonexistent.key", 42));
    }

    @Test
    void getDoublePropertyReturnsDefault() {
        assertEquals(3.14, Config.getDoubleProperty("nonexistent.key", 3.14), 0.001);
    }

    @Test
    void getBooleanPropertyReturnsDefault() {
        assertTrue(Config.getBooleanProperty("nonexistent.key", true));
        assertFalse(Config.getBooleanProperty("nonexistent.key", false));
    }
}
