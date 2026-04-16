package de.dhbw.domain.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import de.dhbw.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void defaultConstructorSetsDefaults() {
        User user = new User();
        assertEquals(UserRole.MEMBER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertNotNull(user.getBorrowedMediaIds());
        assertTrue(user.getBorrowedMediaIds().isEmpty());
        assertNotNull(user.getReservationIds());
        assertTrue(user.getReservationIds().isEmpty());
        assertEquals(0.0, user.getOutstandingFines(), 0.001);
        assertEquals(5, user.getMaxBorrowLimit());
        assertEquals(0, user.getWarningCount());
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    // how the fuck does one test random UUIDs?
    void fullConstructorSetsFields() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Max", "Muster", "max@example.com");
        assertEquals(userId, user.getUserId());
        assertEquals("Max", user.getFirstName());
        assertEquals("Muster", user.getLastName());
        assertEquals("max@example.com", user.getEmail());
    }

    @Test
    void settersWork() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setUserId(userId);
        user.setFirstName("Anna");
        user.setLastName("Schmidt");
        user.setEmail("anna@example.com");
        user.setPhone("0123");
        user.setRole(UserRole.LIBRARIAN);
        user.setStatus(UserStatus.SUSPENDED);

        assertEquals(userId, user.getUserId());
        assertEquals("Anna", user.getFirstName());
        assertEquals("Schmidt", user.getLastName());
        assertEquals("anna@example.com", user.getEmail());
        assertEquals("0123", user.getPhone());
        assertEquals(UserRole.LIBRARIAN, user.getRole());
        assertEquals(UserStatus.SUSPENDED, user.getStatus());
    }

    @Test
    void userRoleEnumValues() {
        assertNotNull(UserRole.ADMIN);
        assertNotNull(UserRole.LIBRARIAN);
        assertNotNull(UserRole.MEMBER);
        assertNotNull(UserRole.STUDENT);
        assertNotNull(UserRole.GUEST);
        assertEquals(5, UserRole.values().length);
    }

    @Test
    void userStatusEnumValues() {
        assertNotNull(UserStatus.ACTIVE);
        assertNotNull(UserStatus.INACTIVE);
        assertNotNull(UserStatus.SUSPENDED);
        assertNotNull(UserStatus.BLOCKED);
        assertNotNull(UserStatus.PENDING);
        assertEquals(5, UserStatus.values().length);
    }

    @Test
    void borrowedMediaListMutable() {
        User user = new User();
        user.getBorrowedMediaIds().add(UUID.randomUUID()); //UUID testing again idk
        assertEquals(1, user.getBorrowedMediaIds().size());
    }

    @Test
    void finesAndWarnings() {
        User user = new User();
        user.setOutstandingFines(9.99);
        user.setWarningCount(2);
        assertEquals(9.99, user.getOutstandingFines(), 0.001);
        assertEquals(2, user.getWarningCount());
    }

    @Test
    void registrationDateIsToday() {
        LocalDate today = LocalDate.now();
        User user = new User();
        assertEquals(today, user.getRegistrationDate());
    }
}
