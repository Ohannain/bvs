package de.dhbw.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    @Test
    void setLevelToDebug() {
        Logger.setLevel(Logger.Level.DEBUG);
        // no exception expected
    }

    @Test
    void setLevelToError() {
        Logger.setLevel(Logger.Level.ERROR);
        // no exception expected
    }

    @Test
    void infoDoesNotThrow() {
        Logger.setConsoleOutput(false);
        assertDoesNotThrow(() -> Logger.info("test info"));
    }

    @Test
    void warnDoesNotThrow() {
        Logger.setConsoleOutput(false);
        assertDoesNotThrow(() -> Logger.warn("test warn"));
    }

    @Test
    void errorDoesNotThrow() {
        Logger.setConsoleOutput(false);
        assertDoesNotThrow(() -> Logger.error("test error"));
    }

    @Test
    void debugDoesNotThrow() {
        Logger.setConsoleOutput(false);
        Logger.setLevel(Logger.Level.DEBUG);
        assertDoesNotThrow(() -> Logger.debug("test debug"));
    }

    @Test
    void errorWithThrowableDoesNotThrow() {
        Logger.setConsoleOutput(false);
        assertDoesNotThrow(() -> Logger.error("msg", new RuntimeException("cause")));
    }

    @Test
    void levelOrderIsCorrect() {
        assertTrue(Logger.Level.DEBUG.ordinal() < Logger.Level.INFO.ordinal());
        assertTrue(Logger.Level.INFO.ordinal() < Logger.Level.WARN.ordinal());
        assertTrue(Logger.Level.WARN.ordinal() < Logger.Level.ERROR.ordinal());
    }
}
