package de.dhbw.ui.menus;

import de.dhbw.application.fine.FineService;
import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.fine.FineStatus;
import de.dhbw.ui.InputHandler;
import de.dhbw.ui.Menu;
import de.dhbw.ui.OutputFormatter;

import java.util.List;
import java.util.Optional;
import de.dhbw.util.UUID;

public class FineMenu extends Menu {
    private final FineService fineService;

    public FineMenu(String title, InputHandler inputHandler, FineService fineService) {
        super(title, inputHandler);

        this.fineService = fineService;

        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("Generate Fines for Overdue Loans", this::generateFines);
        addMenuItem("Pay Fine", this::payFine);
        addMenuItem("Waive Fine", this::waiveFine);
        addMenuItem("View All Fines", this::viewAllFines);
        addMenuItem("View Pending Fines", this::viewPendingFines);
        addMenuItem("View User Fines", this::viewUserFines);
    }

    private void generateFines() {
        OutputFormatter.printWarning(
                "Automatic fine generation is not available in the current FineService implementation."
        );
    }

    private void payFine() {
        String fineId = inputHandler.readNonEmptyString("Enter Fine ID: ");
        Optional<UUID> fineUuid = UUID.parseUuid(fineId, "Fine ID");
        if (fineUuid.isEmpty()) {
            return;
        }

        Optional<Fine> fineOpt = fineService.getFineById(fineUuid.get());
        if (fineOpt.isEmpty()) {
            OutputFormatter.printWarning("Fine not found.");
            return;
        }

        Fine fine = fineOpt.get();
        OutputFormatter.printFine(fine);

        double amount = inputHandler.readDouble("Enter payment amount: ");

        try {
            fineService.payFine(fineUuid.get(), amount);
            OutputFormatter.printSuccess("Fine paid successfully!");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void waiveFine() {
        String fineId = inputHandler.readNonEmptyString("Enter Fine ID: ");
        String reason = inputHandler.readNonEmptyString("Reason for waiving: ");
        Optional<UUID> fineUuid = UUID.parseUuid(fineId, "Fine ID");
        if (fineUuid.isEmpty()) {
            return;
        }

        try {
            fineService.waiveFine(fineUuid.get(), reason);
            OutputFormatter.printSuccess("Fine waived successfully.");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void viewAllFines() {
        List<Fine> fines = fineService.getAllFines();
        OutputFormatter.printFineList(fines);
    }

    private void viewPendingFines() {
        List<Fine> fines = fineService.getFinesByStatus(FineStatus.PENDING);
        OutputFormatter.printFineList(fines);
    }

    private void viewUserFines() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<UUID> userUuid = UUID.parseUuid(userId, "User ID");
        if (userUuid.isEmpty()) {
            return;
        }
        List<Fine> fines = fineService.getFinesByUserId(userUuid.get());
        OutputFormatter.printFineList(fines);
    }
}
