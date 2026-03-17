package de.dhbw.persistence.user;

import de.dhbw.domain.user.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(String userId);
    List<User> findAll();
    List<User> findByName(String name);
    Optional<User> findByEmail(String email);
    void update(User user);
    void delete(String userId);
    boolean exists(String userId);
}
