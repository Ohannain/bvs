package de.dhbw.persistence.user;

import de.dhbw.domain.user.User;
import de.dhbw.util.Config;
import de.dhbw.util.JsonUtils;
import de.dhbw.util.Logger;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import de.dhbw.util.UUID;
import java.util.stream.Collectors;

public class JsonUserRepository implements UserRepository {
    private final String filePath;
    private List<User> userList;

    public JsonUserRepository() {
        this.filePath = Config.USERS_FILE;
        this.userList = new ArrayList<>();
        loadUsers();
    }

    public JsonUserRepository(String filePath) {
        this.filePath = filePath;
        this.userList = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                this.userList = new ArrayList<>();
                saveUsers();
                Logger.info("User file not found. Created new file at " + filePath);
                return;
            }
            this.userList = JsonUtils.readListFromFile(filePath, User.class);
            Logger.info("Loaded " + userList.size() + " users from " + filePath);
        } catch (IOException e) {
            Logger.warn("Could not load users from file: " + e.getMessage());
            this.userList = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try {
            JsonUtils.writeListToFile(filePath, userList);
            Logger.debug("Saved " + userList.size() + " users to " + filePath);
        } catch (IOException e) {
            Logger.error("Failed to save users: " + e.getMessage());
        }
    }

    @Override
    public void save(User user) {
        if (user == null || user.getUserId() == null) {
            Logger.error("Cannot save null user or user with null ID");
            return;
        }
        userList.removeIf(u -> u.getUserId().equals(user.getUserId()));
        userList.add(user);
        saveUsers();
        Logger.info("Saved user: " + user.getUserId());
    }

    @Override
    public List<User> findById(UUID userId) {
        if (userId == null) {
            return List.of();
        }
        return userList.stream()
                .filter(m -> userId.equals(m.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userList);
    }

    @Override
    public List<User> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        String searchTerm = name.trim().toLowerCase(Locale.ROOT);
        return userList.stream()
                .filter(u -> u.getFullName().toLowerCase(Locale.ROOT).contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return userList.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void delete(UUID userId) {
        boolean removed = userList.removeIf(u -> userId != null && userId.equals(u.getUserId()));
        if (removed) {
            saveUsers();
            Logger.info("Deleted user: " + userId);
        }
    }

    @Override
    public boolean exists(UUID userId) {
        if (userId == null) {
            return false;
        }
        return userList.stream().anyMatch(u -> userId.equals(u.getUserId()));
    }
}
