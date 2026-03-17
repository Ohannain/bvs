package de.dhbw.persistence.fine;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.fine.FineStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FineRepository {
    void save(Fine fine);
    void update(Fine fine);
    void delete(UUID id);
    Optional<Fine> findById(UUID id);
    List<Fine> findAll();
    List<Fine> findByUserId(UUID userId);
    List<Fine> findByStatus(FineStatus status);
}
