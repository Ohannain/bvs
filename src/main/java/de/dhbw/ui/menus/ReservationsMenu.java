package de.dhbw.ui.menus;

import de.dhbw.application.reservation.ReservationService;
import de.dhbw.domain.reservation.Reservation;
import de.dhbw.ui.InputHandler;
import de.dhbw.ui.Menu;
import de.dhbw.ui.OutputFormatter;
import de.dhbw.util.UUID;
import java.util.List;
import java.util.Optional;

public class ReservationsMenu extends Menu {

    private final ReservationService reservationService;

    public ReservationsMenu(
        String title,
        InputHandler inputHandler,
        ReservationService reservationService
    ) {
        super(title, inputHandler);
        this.reservationService = reservationService;

        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("Create Reservation", this::createReservation);
        addMenuItem("Fulfill Reservation", this::fulfillReservation);
        addMenuItem("Cancel Reservation", this::cancelReservation);
        addMenuItem("View All Reservations", this::viewAllReservations);
        addMenuItem("Search Reservation", this::searchReservation);
        addMenuItem("View User Reservations", this::viewUserReservations);
    }

    private void createReservation() {
        OutputFormatter.printHeader("Create Reservation");

        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        String mediaId = inputHandler.readNonEmptyString("Enter Media ID: ");
        Optional<UUID> userUuid = UUID.parseUuid(userId);
        Optional<UUID> mediaUuid = UUID.parseUuid(mediaId);
        if (userUuid.isEmpty() || mediaUuid.isEmpty()) {
            return;
        }

        try {
            Reservation reservation = reservationService.createReservation(
                userUuid.get(),
                mediaUuid.get()
            );
            OutputFormatter.printSuccess("Reservation created successfully!");
            OutputFormatter.printReservation(reservation);
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void fulfillReservation() {
        String reservationId = inputHandler.readNonEmptyString("Enter Reservation ID: ");
        Optional<UUID> reservationUuid = UUID.parseUuid(reservationId);
        if (reservationUuid.isEmpty()) {
            return;
        }

        try {
            reservationService.fulfillReservation(reservationUuid.get());
            OutputFormatter.printSuccess("Reservation fulfilled successfully.");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void cancelReservation() {
        String reservationId = inputHandler.readNonEmptyString("Enter Reservation ID: ");
        Optional<UUID> reservationUuid = UUID.parseUuid(reservationId);
        if (reservationUuid.isEmpty()) {
            return;
        }

        try {
            reservationService.cancelReservation(reservationUuid.get());
            OutputFormatter.printSuccess("Reservation cancelled successfully.");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void viewAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        OutputFormatter.printReservationList(reservations);
    }

    private void viewUserReservations() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<UUID> userUuid = UUID.parseUuid(userId);
        if (userUuid.isEmpty()) {
            return;
        }
        List<Reservation> reservations = reservationService.getReservationsByUserId(
            userUuid.get()
        );
        OutputFormatter.printReservationList(reservations);
    }

    private void searchReservation() {
        String reservationId = inputHandler.readNonEmptyString("Enter Reservation ID: ");
        Optional<UUID> reservationUuid = UUID.parseUuid(reservationId);
        if (reservationUuid.isEmpty()) {
            return;
        }

        Optional<Reservation> resOpt = reservationService.getReservationById(reservationUuid.get());
        if (resOpt.isEmpty()) {
            OutputFormatter.printInfo("Reservation not found.");
            return;
        }

        OutputFormatter.printReservation(resOpt.get());
    }
}
