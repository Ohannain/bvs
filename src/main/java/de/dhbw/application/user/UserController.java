package de.dhbw.application.user;

import de.dhbw.domain.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User registerUser(String firstName, String lastName, String email, String phone, String address) {
        return userService.createUser(firstName, lastName, email, phone, address);
    }

    public Optional<User> findUser(UUID userId) {
        return userService.getUserById(userId);
    }

    public List<User> searchUsers(String searchTerm) {
        return userService.searchUsersByName(searchTerm);
    }

    public void updateUserDetails(User user) {
        userService.updateUser(user);
    }

    public void suspendUser(UUID userId, String reason) {
        userService.suspendUser(userId, reason);
    }

    public void activateUser(UUID userId) {
        userService.activateUser(userId);
    }

    public void blockUser(UUID userId, String reason) {
        userService.blockUser(userId, reason);
    }

    public boolean checkBorrowingEligibility(UUID userId) {
        return userService.canUserBorrow(userId);
    }

    public void deleteUser(UUID userId) {
        userService.deleteUser(userId);
    }
}
