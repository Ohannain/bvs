package de.dhbw.application.reservation;

import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.reservation.Reservation;
import de.dhbw.domain.reservation.ReservationStatus;
import de.dhbw.domain.user.User;
import de.dhbw.persistence.media.MediaRepository;
import de.dhbw.persistence.reservation.ReservationRepository;
import de.dhbw.persistence.user.UserRepository;
import de.dhbw.util.Logger;
import java.util.List;
import java.util.Optional;
import de.dhbw.util.UUID;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;

    public ReservationService(
        ReservationRepository reservationRepository,
        MediaRepository mediaRepository,
        UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    public Reservation createReservation(UUID userId, UUID mediaId) {
        // Validate user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        User user = userOpt.get();

        if (user.getStatus() != de.dhbw.domain.user.UserStatus.ACTIVE) {
            throw new IllegalStateException("User is not active");
        }

        // Validate media
        List<Media> mediaList = mediaRepository.findById(mediaId);
        if (mediaList.isEmpty()) {
            throw new IllegalArgumentException("Media not found: " + mediaId);
        }
        Media media = mediaList.getFirst();

        // Check if media is already available
        if (media.isAvailable()) {
            throw new IllegalStateException(
                "Media is available, no need for reservation"
            );
        }

        // Check if user already has an active reservation for this media
        List<Reservation> userReservations = reservationRepository.findByUserId(
            userId
        );
        boolean hasActiveReservation = userReservations
            .stream()
            .anyMatch(r -> r.getMediaId().equals(mediaId) && r.isActive());

        if (hasActiveReservation) {
            throw new IllegalStateException(
                "User already has an active reservation for this media"
            );
        }

        // Create reservation
        UUID reservationId = generateReservationId();
        Reservation reservation = new Reservation(
            reservationId,
            userId,
            mediaId
        );
        reservation.setStatus(ReservationStatus.ACTIVE);

        // Update user
        user.addReservation(reservationId);
        userRepository.update(user);

        // Update media status if needed
        if (media.getStatus() != MediaStatus.RESERVED) {
            media.setStatus(MediaStatus.RESERVED);
            mediaRepository.update(media);
        }

        reservationRepository.save(reservation);
        Logger.info(
            "Created reservation " +
                reservationId +
                " for user " +
                userId +
                " and media " +
                mediaId
        );

        return reservation;
    }

    public void fulfillReservation(UUID reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(
            reservationId
        );
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException(
                "Reservation not found: " + reservationId
            );
        }

        Reservation reservation = reservationOpt.get();

        if (!reservation.isActive()) {
            throw new IllegalStateException("Reservation is not active");
        }

        reservation.fulfill();
        reservationRepository.update(reservation);

        // Update user
        Optional<User> userOpt = userRepository.findById(
            reservation.getUserId()
        );
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.removeReservation(reservationId);
            userRepository.update(user);
        }

        Logger.info("Fulfilled reservation: " + reservationId);
    }

    public void cancelReservation(UUID reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(
            reservationId
        );
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException(
                "Reservation not found: " + reservationId
            );
        }

        Reservation reservation = reservationOpt.get();
        reservation.cancel();
        reservationRepository.update(reservation);

        // Update user
        Optional<User> userOpt = userRepository.findById(
            reservation.getUserId()
        );
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.removeReservation(reservationId);
            userRepository.update(user);
        }

        // Check if media should be unmarked as reserved
        List<Reservation> mediaReservations =
            reservationRepository.findByMediaId(reservation.getMediaId());
        boolean hasActiveReservations = mediaReservations
            .stream()
            .anyMatch(
                r -> !r.getReservationId().equals(reservationId) && r.isActive()
            );

        if (!hasActiveReservations) {
            List<Media> mediaList = mediaRepository.findById(
                reservation.getMediaId()
            );
            if (!mediaList.isEmpty()) {
                Media media = mediaList.getFirst();
                if (media.getStatus() == MediaStatus.RESERVED) {
                    media.setStatus(MediaStatus.AVAILABLE);
                    mediaRepository.update(media);
                }
            }
        }

        Logger.info("Cancelled reservation: " + reservationId);
    }

    public void checkExpiredReservations() {
        List<Reservation> activeReservations =
            reservationRepository.findByStatus(ReservationStatus.ACTIVE);
        int expiredCount = 0;

        for (Reservation reservation : activeReservations) {
            if (reservation.isExpired()) {
                reservation.markAsExpired();
                reservationRepository.update(reservation);

                // Update user
                Optional<User> userOpt = userRepository.findById(
                    reservation.getUserId()
                );
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.removeReservation(reservation.getReservationId());
                    userRepository.update(user);
                }

                expiredCount++;
            }
        }

        if (expiredCount > 0) {
            Logger.info("Marked " + expiredCount + " reservations as expired");
        }
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByUserId(UUID userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getReservationsByMediaId(UUID mediaId) {
        return reservationRepository.findByMediaId(mediaId);
    }

    public List<Reservation> getActiveReservations() {
        return reservationRepository.findByStatus(ReservationStatus.ACTIVE);
    }

    public Optional<Reservation> getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId);
    }

    private UUID generateReservationId() {
        UUID reservationId;
        do {
            reservationId = UUID.nextReservationId();
        } while (reservationRepository.exists(reservationId));
        return reservationId;
    }
}
