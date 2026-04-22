package de.dhbw.ui;

import de.dhbw.application.fine.FineService;
import de.dhbw.application.loan.LoanService;
import de.dhbw.application.media.MediaService;
import de.dhbw.application.reservation.ReservationService;
import de.dhbw.application.user.UserService;
import de.dhbw.ui.menus.*;

public class MainMenu extends Menu {
    private final UserMenu userMenu;
    private final MediaMenu mediaMenu;
    private final LoanMenu loanMenu;
    private final ReservationsMenu reservationsMenu;
    private final FineMenu fineMenu;
    private final ReportsMenu reportsMenu;

    public MainMenu(
        InputHandler inputHandler,
        UserService userService,
        MediaService mediaService,
        LoanService loanService,
        ReservationService reservationService,
        FineService fineService
    ) {
        super("Main Menu", inputHandler);

        this.userMenu = new UserMenu("User Management", inputHandler, userService);
        this.mediaMenu = new MediaMenu("Media Management", inputHandler, mediaService);
        this.loanMenu = new LoanMenu("Loan Management", inputHandler, loanService);
        this.reservationsMenu = new ReservationsMenu("Reservation Management", inputHandler, reservationService);
        this.fineMenu = new FineMenu("Fine Management", inputHandler, fineService);
        this.reportsMenu = new ReportsMenu("Reports", inputHandler, loanService, userService, mediaService, fineService);

        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("User Management", userMenu::display);
        addMenuItem("Media Management", mediaMenu::display);
        addMenuItem("Loan Management", loanMenu::display);
        addMenuItem("Fine Management", fineMenu::display);
        addMenuItem("Reservations Management", reservationsMenu::display);
        addMenuItem("Reports Management", reportsMenu::display);
    }

    @Override
    protected boolean isMainMenu() {
        return true;
    }
}
