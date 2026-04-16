package de.dhbw.persistence.reservation;

import de.dhbw.domain.reservation.Reservation;
import de.dhbw.domain.reservation.ReservationStatus;
import java.util.List;
import java.util.Optional;
import de.dhbw.util.UUID;

public interface ReservationRepository {
    void save(Reservation reservation);
    Optional<Reservation> findById(UUID reservationId);
    List<Reservation> findAll();
    List<Reservation> findByUserId(UUID userId);
    List<Reservation> findByMediaId(UUID mediaId);
    List<Reservation> findByStatus(ReservationStatus status);
    void update(Reservation reservation);
    void delete(UUID reservationId);
    boolean exists(UUID reservationId);
}
