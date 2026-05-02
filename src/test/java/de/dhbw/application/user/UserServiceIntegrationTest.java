package de.dhbw.application.user;

import de.dhbw.domain.user.User;
import de.dhbw.domain.user.UserStatus;
import de.dhbw.persistence.user.UserRepository;
import de.dhbw.persistence.user.FakeUserRepository;
import de.dhbw.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserService using FakeUserRepository.
 * Tests business logic without requiring file access or database.
 */
class UserServiceIntegrationTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = new FakeUserRepository();
        userService = new UserService(userRepository);
    }

    @Test
    void testCreateAndRetrieveUser() {
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String phone = "0123456789";
        String address = "123 Main St";

        User createdUser = userService.createUser(firstName, lastName, email, phone, address);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());
        assertEquals(firstName, createdUser.getFirstName());
        assertEquals(lastName, createdUser.getLastName());
        assertEquals(email, createdUser.getEmail());

        List<User> retrievedUsers = userService.getUserById(createdUser.getUserId());

        assertEquals(1, retrievedUsers.size());
        assertEquals(createdUser.getUserId(), retrievedUsers.getFirst().getUserId());
    }

    @Test
    void testGetAllUsers() {
        userService.createUser("Alice", "Smith", "alice@example.com", "1111111111", "1 Oak Ave");
        userService.createUser("Bob", "Jones", "bob@example.com", "2222222222", "2 Pine St");
        userService.createUser("Carol", "Brown", "carol@example.com", "3333333333", "3 Elm Dr");

        List<User> allUsers = userService.getAllUsers();

        assertEquals(3, allUsers.size());
    }

    @Test
    void testSearchUsersByName() {
        userService.createUser("John", "Smith", "john@example.com", "1111111111", "1 Oak Ave");
        userService.createUser("Jane", "Smith", "jane@example.com", "2222222222", "2 Oak Ave");
        userService.createUser("Robert", "Johnson", "robert@example.com", "3333333333", "3 Johnson Ln");

        List<User> smithUsers = userService.searchUsersByName("Smith");

        assertEquals(2, smithUsers.size());
        assertTrue(smithUsers.stream().allMatch(u ->
                u.getLastName().contains("Smith") || u.getFirstName().contains("Smith")));
    }

    @Test
    void testFindUserByEmail() {
        String email = "test@example.com";
        userService.createUser("Test", "User", email, "1111111111", "Test St");

        Optional<User> foundUser = userService.getUserByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }

    @Test
    void testFindUserByEmailNotFound() {
        Optional<User> notFound = userService.getUserByEmail("nonexistent@example.com");

        assertTrue(notFound.isEmpty());
    }

    @Test
    void testUpdateUser() {
        User user = userService.createUser("Original", "Name", "original@example.com", "1111111111", "Original St");

        user.setFirstName("Updated");
        user.setLastName("Name");
        userService.updateUser(user);

        List<User> retrievedUsers = userService.getUserById(user.getUserId());
        assertEquals("Updated", retrievedUsers.getFirst().getFirstName());
    }

    @Test
    void testSuspendUser() {
        User user = userService.createUser("Test", "User", "test@example.com", "1111111111", "Test St");
        assertEquals(UserStatus.ACTIVE, user.getStatus());

        userService.suspendUser(user.getUserId(), "Too many fines");

        List<User> suspendedUsers = userService.getUserById(user.getUserId());
        assertEquals(UserStatus.SUSPENDED, suspendedUsers.getFirst().getStatus());
        assertEquals(1, suspendedUsers.getFirst().getWarningCount());
    }

    @Test
    void testActivateUser() {
        User user = userService.createUser("Test", "User", "test@example.com", "1111111111", "Test St");
        userService.suspendUser(user.getUserId(), "Too many fines");

        userService.activateUser(user.getUserId());

        List<User> activatedUsers = userService.getUserById(user.getUserId());
        assertEquals(UserStatus.ACTIVE, activatedUsers.getFirst().getStatus());
    }

    @Test
    void testDeleteUserWithoutLoans() {
        User user = userService.createUser("Test", "User", "test@example.com", "1111111111", "Test St");

        userService.deleteUser(user.getUserId());

        List<User> retrievedUsers = userService.getUserById(user.getUserId());
        assertTrue(retrievedUsers.isEmpty());
    }

    @Test
    void testCannotDeleteUserWithLoans() {
        User user = userService.createUser("Test", "User", "test@example.com", "1111111111", "Test St");
        user.addBorrowedMedia(UUID.randomUUID());
        userService.updateUser(user);

        assertThrows(IllegalStateException.class, () -> {
            userService.deleteUser(user.getUserId());
        });
    }

    @Test
    void testCannotDeleteUserWithFines() {
        User user = userService.createUser("Test", "User", "test@example.com", "1111111111", "Test St");
        user.setOutstandingFines(10.50);
        userService.updateUser(user);

        assertThrows(IllegalStateException.class, () -> {
            userService.deleteUser(user.getUserId());
        });
    }

    @Test
    void testRepositoryIsCleaned() {
        userService.createUser("Test", "User", "test@example.com", "1111111111", "Test St");

        FakeUserRepository fakeRepo = (FakeUserRepository) userRepository;
        assertEquals(1, fakeRepo.size());

        fakeRepo.clear();

        assertEquals(0, fakeRepo.size());
        List<User> allUsers = userService.getAllUsers();
        assertTrue(allUsers.isEmpty());
    }

    @Test
    void testInvalidUserDataThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("Test", "User", "invalid-email", "1111111111", "Test St");
        });
    }
}