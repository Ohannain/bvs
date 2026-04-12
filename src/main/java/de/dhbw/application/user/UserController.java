package de.dhbw.application.user;

import de.dhbw.domain.user.User;
import de.dhbw.domain.user.UserStatus;

import java.util.List;
import java.util.Optional;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User registerUser(String firstName, String lastName, String email, String phone, String address) {
        return userService.createUser(firstName, lastName, email, phone, address);
    }

    public Optional<User> findUser(String userId) {
        return userService.getUserById(userId);
    }

    public List<User> searchUsers(String searchTerm) {
        return userService.searchUsersByName(searchTerm);
    }

    public void updateUserDetails(User user) {
        userService.updateUser(user);
    }

    public void suspendUser(String userId, String reason) {
        userService.suspendUser(userId, reason);
    }

    public void activateUser(String userId) {
        userService.activateUser(userId);
    }

    public void blockUser(String userId, String reason) {
        userService.blockUser(userId, reason);
    }

    public boolean checkBorrowingEligibility(String userId) {
        return userService.canUserBorrow(userId);
    }

    public void deleteUser(String userId) {
        userService.deleteUser(userId);
    }
}
