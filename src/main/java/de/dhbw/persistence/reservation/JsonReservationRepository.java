package de.dhbw.persistence.reservation;

import de.dhbw.domain.reservation.Reservation;
import de.dhbw.domain.reservation.ReservationStatus;
import de.dhbw.util.Config;
import de.dhbw.util.JsonUtils;
import de.dhbw.util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonReservationRepository implements ReservationRepository {
    private final String filePath;
    private List<Reservation> reservations;

    public JsonReservationRepository() {
        this.filePath = Config.RESERVATIONS_FILE;
        this.reservations = new ArrayList<>();
        loadReservations();
    }

    public JsonReservationRepository(String filePath) {
        this.filePath = filePath;
        this.reservations = new ArrayList<>();
        loadReservations();
    }

    private void loadReservations() {
        try {
            this.reservations = JsonUtils.readListFromFile(filePath, Reservation.class);
            Logger.info("Loaded " + reservations.size() + " reservations from " + filePath);
        } catch (IOException e) {
            Logger.warn("Could not load reservations from file: " + e.getMessage());
            this.reservations = new ArrayList<>();
        }
    }

    private void saveReservations() {
        try {
            JsonUtils.writeListToFile(filePath, reservations);
            Logger.debug("Saved " + reservations.size() + " reservations to " + filePath);
        } catch (IOException e) {
            Logger.error("Failed to save reservations: " + e.getMessage());
        }
    }

    @Override
    public void save(Reservation reservation) {
        if (reservation == null || reservation.getReservationId() == null) {
            Logger.error("Cannot save null reservation or reservation with null ID");
            return;
        }
        reservations.removeIf(r -> r.getReservationId().equals(reservation.getReservationId()));
        reservations.add(reservation);
        saveReservations();
        Logger.info("Saved reservation: " + reservation.getReservationId());
    }

    @Override
    public Optional<Reservation> findById(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst();
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations);
    }

    @Override
    public List<Reservation> findByUserId(String userId) {
        return reservations.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByMediaId(String mediaId) {
        return reservations.stream()
                .filter(r -> r.getMediaId().equals(mediaId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservations.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Reservation reservation) {
        save(reservation);
    }

    @Override
    public void delete(String reservationId) {
        boolean removed = reservations.removeIf(r -> r.getReservationId().equals(reservationId));
        if (removed) {
            saveReservations();
            Logger.info("Deleted reservation: " + reservationId);
        }
    }

    @Override
    public boolean exists(String reservationId) {
        return reservations.stream().anyMatch(r -> r.getReservationId().equals(reservationId));
    }
}
