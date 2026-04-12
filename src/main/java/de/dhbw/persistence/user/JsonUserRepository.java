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
import java.util.stream.Collectors;
    
public class JsonUserRepository implements UserRepository {
    private final String filePath;
    private List<User> users;

    public JsonUserRepository() {
        this.filePath = Config.USERS_FILE;
        this.users = new ArrayList<>();
        loadUsers();
    }

    public JsonUserRepository(String filePath) {
        this.filePath = filePath;
        this.users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                this.users = new ArrayList<>();
                saveUsers();
                Logger.info("User file not found. Created new file at " + filePath);
                return;
            }
            this.users = JsonUtils.readListFromFile(filePath, User.class);
            Logger.info("Loaded " + users.size() + " users from " + filePath);
        } catch (IOException e) {
            Logger.warn("Could not load users from file: " + e.getMessage());
            this.users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try {
            JsonUtils.writeListToFile(filePath, users);
            Logger.debug("Saved " + users.size() + " users to " + filePath);
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
        users.removeIf(u -> u.getUserId().equals(user.getUserId()));
        users.add(user);
        saveUsers();
        Logger.info("Saved user: " + user.getUserId());
    }

    @Override
    public Optional<User> findById(String userId) {
        if (userId == null || userId.isBlank()) {
            return Optional.empty();
        }
        return users.stream()
                .filter(u -> userId.equals(u.getUserId()))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public List<User> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        String searchTerm = name.trim().toLowerCase(Locale.ROOT);
        return users.stream()
                .filter(u -> u.getFullName().toLowerCase(Locale.ROOT).contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return users.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void delete(String userId) {
        boolean removed = users.removeIf(u -> userId != null && userId.equals(u.getUserId()));
        if (removed) {
            saveUsers();
            Logger.info("Deleted user: " + userId);
        }
    }

    @Override
    public boolean exists(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return users.stream().anyMatch(u -> userId.equals(u.getUserId()));
    }
}
