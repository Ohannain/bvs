package de.dhbw.application.user;

import de.dhbw.domain.user.User;
import de.dhbw.domain.user.UserStatus;
import de.dhbw.persistence.user.UserRepository;
import de.dhbw.util.Config;
import de.dhbw.util.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String firstName, String lastName, String email, String phone, String address) {
        String userId = generateUserId();
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

    public Optional<User> getUserById(String userId) {
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

    public void deleteUser(String userId) {
        User user = getRequiredUser(userId);
        if (!user.getBorrowedMediaIds().isEmpty()) {
            throw new IllegalStateException("Cannot delete user with active loans");
        }
        if (user.getOutstandingFines() > 0) {
            throw new IllegalStateException("Cannot delete user with outstanding fines");
        }
        userRepository.delete(userId);
        Logger.info("Deleted user: " + userId);
    }

    public void suspendUser(String userId, String reason) {
        User user = getRequiredUser(userId);
        user.setStatus(UserStatus.SUSPENDED);
        user.setWarningCount(user.getWarningCount() + 1);
        userRepository.update(user);
        Logger.info("Suspended user: " + userId + " - Reason: " + reason);
    }

    public void activateUser(String userId) {
        User user = getRequiredUser(userId);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.update(user);
        Logger.info("Activated user: " + userId);
    }

    public void blockUser(String userId, String reason) {
        User user = getRequiredUser(userId);
        user.setStatus(UserStatus.BLOCKED);
        userRepository.update(user);
        Logger.info("Blocked user: " + userId + " - Reason: " + reason);
    }

    public boolean canUserBorrow(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        return user.canBorrow();
    }

    public void addFine(String userId, double amount) {
        User user = getRequiredUser(userId);
        user.setOutstandingFines(user.getOutstandingFines() + amount);

        if (user.getOutstandingFines() >= Config.MAX_OUTSTANDING_FINES && user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.SUSPENDED);
            Logger.warn("User " + userId + " auto-suspended due to high fines");
        }

        userRepository.update(user);
        Logger.info("Added fine of " + amount + " to user: " + userId);
    }

    public void payFine(String userId, double amount) {
        User user = getRequiredUser(userId);
        double newFines = Math.max(0, user.getOutstandingFines() - amount);
        user.setOutstandingFines(newFines);

        if (newFines < Config.MAX_OUTSTANDING_FINES && user.getStatus() == UserStatus.SUSPENDED) {
            user.setStatus(UserStatus.ACTIVE);
            Logger.info("User " + userId + " reactivated after fine payment");
        }

        userRepository.update(user);
        Logger.info("Paid fine of " + amount + " for user: " + userId);
    }

    private String generateUserId() {
        String userId;
        do {
            userId = "U" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (userRepository.exists(userId));
        return userId;
    }

    private User getRequiredUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
