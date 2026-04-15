package de.dhbw.ui;

import de.dhbw.application.fine.FineService;
import de.dhbw.application.loan.LoanService;
import de.dhbw.application.media.MediaService;
import de.dhbw.application.reservation.ReservationService;
import de.dhbw.application.user.UserService;
import de.dhbw.ui.menus.*;

public class MainMenu extends Menu {
    private final UserService userService;
    private final MediaService mediaService;
    private final LoanService loanService;
    private final FineService fineService;

    public MainMenu(
        InputHandler inputHandler,
        UserService userService,
        MediaService mediaService,
        LoanService loanService,
        FineService fineService,
    ) {
        super("Main Menu", inputHandler);

        this.userService = userService;
        this.mediaService = mediaService;
        this.loanService = loanService;
        this.fineService = fineService;

        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("User Management", () -> new UserMenu("User Management", inputHandler, userService).display());
        addMenuItem("Media Management", () -> new MediaMenu("Media Management", inputHandler, mediaService).display());
        addMenuItem("Loan Management", () -> new LoanMenu("Loan Management", inputHandler, loanService).display());
        addMenuItem("Fine Management", () -> new FineMenu("Fine Management", inputHandler, fineService).display());
        addMenuItem("Reports Management", () -> new ReportsMenu("Reports Management", inputHandler, loanService, userService, mediaService, fineService).display());
    }

    @Override
    protected boolean isMainMenu() {
        return true;
    }
}
