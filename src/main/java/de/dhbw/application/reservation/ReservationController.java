package de.dhbw.application.reservation;

import de.dhbw.domain.reservation.Reservation;

import java.util.List;
import java.util.Optional;
import de.dhbw.util.UUID;

public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public Reservation makeReservation(UUID userId, UUID mediaId) {
        return reservationService.createReservation(userId, mediaId);
    }

    public void fulfillReservation(UUID reservationId) {
        reservationService.fulfillReservation(reservationId);
    }

    public void cancelReservation(UUID reservationId) {
        reservationService.cancelReservation(reservationId);
    }

    public List<Reservation> getUserReservations(UUID userId) {
        return reservationService.getReservationsByUserId(userId);
    }

    public List<Reservation> getMediaReservations(UUID mediaId) {
        return reservationService.getReservationsByMediaId(mediaId);
    }

    public List<Reservation> getActiveReservations() {
        return reservationService.getActiveReservations();
    }

    public Optional<Reservation> getReservationDetails(UUID reservationId) {
        return reservationService.getReservationById(reservationId);
    }

    public void checkAndExpireReservations() {
        reservationService.checkExpiredReservations();
    }
}
