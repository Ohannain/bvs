package de.dhbw.domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import de.dhbw.util.UUID;

import de.dhbw.util.Config;

public class User {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate registrationDate;
    private LocalDate birthDate;
    private UserRole role;
    private UserStatus status;
    private List<UUID> borrowedMediaIds;
    private List<UUID> reservationIds;
    private double outstandingFines;
    private int maxBorrowLimit;
    private String password;
    private int warningCount;
    private LocalDate lastLoginDate;

    public User() {
        this.registrationDate = LocalDate.now();
        this.role = UserRole.MEMBER;
        this.status = UserStatus.ACTIVE;
        this.borrowedMediaIds = new ArrayList<>();
        this.reservationIds = new ArrayList<>();
        this.outstandingFines = 0.0;
        this.maxBorrowLimit = Config.MAX_BORROW_LIMIT;
        this.warningCount = 0;
    }

    public User(UUID userId, String firstName, String lastName, String email) {
        this();
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<UUID> getBorrowedMediaIds() {
        return borrowedMediaIds;
    }

    public void setBorrowedMediaIds(List<UUID> borrowedMediaIds) {
        this.borrowedMediaIds = borrowedMediaIds != null ? borrowedMediaIds : new ArrayList<>();
    }

    public List<UUID> getReservationIds() {
        return reservationIds;
    }

    public void setReservationIds(List<UUID> reservationIds) {
        this.reservationIds = reservationIds != null ? reservationIds : new ArrayList<>();
    }

    public double getOutstandingFines() {
        return outstandingFines;
    }

    public void setOutstandingFines(double outstandingFines) {
        this.outstandingFines = outstandingFines;
    }

    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }

    public void setMaxBorrowLimit(int maxBorrowLimit) {
        this.maxBorrowLimit = maxBorrowLimit;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getFullName() {
        String fn = firstName != null ? firstName : "";
        String ln = lastName != null ? lastName : "";
        return (fn + " " + ln).trim();
    }

    /**
     * Checks whether the borrow.
     */
    public boolean canBorrow() {
        return status == UserStatus.ACTIVE &&
               borrowedMediaIds.size() < maxBorrowLimit &&
               outstandingFines < Config.MAX_OUTSTANDING_FINES;
    }

    /**
     * Checks whether the overdue fines.
     */
    public boolean hasOverdueFines() {
        return outstandingFines > 0;
    }

    /**
     * Adds a borrowed media.
     */
    public void addBorrowedMedia(UUID mediaId) {
        if (!borrowedMediaIds.contains(mediaId)) {
            borrowedMediaIds.add(mediaId);
        }
    }

    /**
     * Removes a borrowed media.
     */
    public void removeBorrowedMedia(UUID mediaId) {
        borrowedMediaIds.remove(mediaId);
    }

    /**
     * Adds a reservation.
     */
    public void addReservation(UUID reservationId) {
        if (!reservationIds.contains(reservationId)) {
            reservationIds.add(reservationId);
        }
    }

    /**
     * Removes a reservation.
     */
    public void removeReservation(UUID reservationId) {
        reservationIds.remove(reservationId);
    }

    @Override
    /**
     * Executes the to string operation.
     */
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", borrowedMedia=" + borrowedMediaIds.size() +
                ", fines=" + outstandingFines +
                '}';
    }
}
