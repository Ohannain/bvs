package de.dhbw.persistence.user;

import de.dhbw.domain.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UUID userId);
    List<User> findAll();
    List<User> findByName(String name);
    Optional<User> findByEmail(String email);
    void update(User user);
    void delete(UUID userId);
    boolean exists(UUID userId);
}
