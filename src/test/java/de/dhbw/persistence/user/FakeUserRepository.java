package de.dhbw.persistence.user;

import de.dhbw.domain.user.User;
import de.dhbw.util.UUID;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-Memory Fake-Implementierung des UserRepository für Tests.
 * Ermöglicht Tests komplett ohne Dateizugriffe.
 * 
 * Verwendung:
 *   UserRepository repo = new FakeUserRepository();
 *   UserService service = new UserService(repo);
 */
public class FakeUserRepository implements UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User und UserId dürfen nicht null sein");
        }
        users.put(user.getUserId(), user);
    }

    @Override
    public List<User> findById(UUID userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        User user = users.get(userId);
        return user != null ? List.of(user) : Collections.emptyList();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Collections.emptyList();
        }
        String searchName = name.toLowerCase();
        return users.values().stream()
                .filter(user -> user.getFirstName().toLowerCase().contains(searchName)
                        || user.getLastName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return users.values().stream()
                .filter(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public void update(User user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User und UserId dürfen nicht null sein");
        }
        if (!users.containsKey(user.getUserId())) {
            throw new IllegalStateException("User mit ID " + user.getUserId() + " existiert nicht");
        }
        users.put(user.getUserId(), user);
    }

    @Override
    public void delete(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId darf nicht null sein");
        }
        users.remove(userId);
    }

    @Override
    public boolean exists(UUID userId) {
        if (userId == null) {
            return false;
        }
        return users.containsKey(userId);
    }

    /**
     * Löscht alle Users (nützlich zur Verwendung vor jedem Test)
     */
    public void clear() {
        users.clear();
    }

    /**
     * Gibt die Anzahl der gespeicherten Users zurück
     */
    public int size() {
        return users.size();
    }
}
