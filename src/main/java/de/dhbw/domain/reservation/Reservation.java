package de.dhbw.domain.reservation;

import java.time.LocalDate;
import java.util.UUID

public class Reservation {
    // these are only a best guess to what might be needed and are subject to change
    private UUID reservationId;
    private UUID userId;
    private UUID mediaId;
    private LocalDate reservationDate;
    private LocalDate expiryDate;
    private LocalDate fulfilledDate;
    private ReservationStatus status;
    private int priority;
    private String notes;

    public Reservation() {
        this.reservationDate = LocalDate.now();
        this.expiryDate = reservationDate.plusDays(7);
        this.status = ReservationStatus.ACTIVE;
        this.priority = 0;
    }

    public Reservation(String reservationId, String userId, String mediaId) {
        this();
        this.reservationId = reservationId;
        this.userId = userId;
        this.mediaId = mediaId;
    }

    // Getters and Setters
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getFulfilledDate() {
        return fulfilledDate;
    }

    public void setFulfilledDate(LocalDate fulfilledDate) {
        this.fulfilledDate = fulfilledDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isActive() {
        return status == ReservationStatus.ACTIVE;
    }

    // maybe move this and markAsExpired together?
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate) && status == ReservationStatus.ACTIVE;
    }

    public void fulfill() {
        this.status = ReservationStatus.FULFILLED;
        this.fulfilledDate = LocalDate.now();
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void markAsExpired() {
        this.status = ReservationStatus.EXPIRED;
    }

    //return the reservation data as a nice string
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", userId='" + userId + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", status=" + status +
                ", reservationDate=" + reservationDate +
                '}';
    }

}
