package de.dhbw.application.user;

import de.dhbw.domain.user.User;
import de.dhbw.domain.user.UserStatus;
import de.dhbw.persistence.user.UserRepository;
import de.dhbw.util.Config;
import de.dhbw.util.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import de.dhbw.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String firstName, String lastName, String email, String phone, String address) {
        UUID userId = generateUserId();
        User user = new User(userId, firstName, lastName, email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRegistrationDate(LocalDate.now());

        List<String> errors = UserValidator.validate(user);
        if (!errors.isEmpty()) {
            Logger.error("User validation failed: " + errors);
            throw new IllegalArgumentException("Invalid user data: " + String.join(", ", errors));
        }

        userRepository.save(user);
        Logger.info("Created new user: " + userId);
        return user;
    }

    public List<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByName(name);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUser(User user) {
        List<String> errors = UserValidator.validate(user);
        if (!errors.isEmpty()) {
            Logger.error("User validation failed: " + errors);
            throw new IllegalArgumentException("Invalid user data: " + String.join(", ", errors));
        }

        userRepository.update(user);
        Logger.info("Updated user: " + user.getUserId());
    }

    public void deleteUser(UUID userId) {
        List<User> userList = userRepository.findById(userId);
        if (userList.isEmpty()) {
            Logger.warn("No loan found with id " + userId);
        }

        User user = userList.getFirst();
        if (!user.getBorrowedMediaIds().isEmpty()) {
            throw new IllegalStateException("Cannot delete user with active loans");
        }
        if (user.getOutstandingFines() > 0) {
            throw new IllegalStateException("Cannot delete user with outstanding fines");
        }
        userRepository.delete(userId);
        Logger.info("Deleted user: " + userId);
    }

    public void suspendUser(UUID userId, String reason) {
        List<User> userList = userRepository.findById(userId);
        if (userList.isEmpty()) {
            Logger.warn("No loan found with id " + userId);
        }

        User user = userList.getFirst();
        user.setStatus(UserStatus.SUSPENDED);
        user.setWarningCount(user.getWarningCount() + 1);
        userRepository.update(user);
        Logger.info("Suspended user: " + userId + " - Reason: " + reason);
    }

    public void activateUser(UUID userId) {
        List<User> userList = userRepository.findById(userId);
        if (userList.isEmpty()) {
            Logger.warn("No loan found with id " + userId);
        }

        User user = userList.getFirst();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.update(user);
        Logger.info("Activated user: " + userId);
    }

    public void blockUser(UUID userId, String reason) {
        List<User> userList = userRepository.findById(userId);
        if (userList.isEmpty()) {
            Logger.warn("No loan found with id " + userId);
        }

        User user = userList.getFirst();
        user.setStatus(UserStatus.BLOCKED);
        userRepository.update(user);
        Logger.info("Blocked user: " + userId + " - Reason: " + reason);
    }

    public boolean canUserBorrow(UUID userId) {
        List<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.getFirst();
        return user.canBorrow();
    }

    public void addFine(UUID userId, double amount) {
        List<User> userList = userRepository.findById(userId);
        if (userList.isEmpty()) {
            Logger.warn("No loan found with id " + userId);
        }

        User user = userList.getFirst();
        user.setOutstandingFines(user.getOutstandingFines() + amount);

        if (user.getOutstandingFines() >= Config.MAX_OUTSTANDING_FINES && user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.SUSPENDED);
            Logger.warn("User " + userId + " auto-suspended due to high fines");
        }

        userRepository.update(user);
        Logger.info("Added fine of " + amount + " to user: " + userId);
    }

    public void payFine(UUID userId, double amount) {
        List<User> userList = userRepository.findById(userId);
        if (userList.isEmpty()) {
            Logger.warn("No loan found with id " + userId);
        }

        User user = userList.getFirst();
        double newFines = Math.max(0, user.getOutstandingFines() - amount);
        user.setOutstandingFines(newFines);

        if (newFines < Config.MAX_OUTSTANDING_FINES && user.getStatus() == UserStatus.SUSPENDED) {
            user.setStatus(UserStatus.ACTIVE);
            Logger.info("User " + userId + " reactivated after fine payment");
        }

        userRepository.update(user);
        Logger.info("Paid fine of " + amount + " for user: " + userId);
    }

    private UUID generateUserId() {
        UUID userId;
        do {
            userId = UUID.nextUserId();
        } while (userRepository.findById(userId).isEmpty());
        return userId;
    }
}
