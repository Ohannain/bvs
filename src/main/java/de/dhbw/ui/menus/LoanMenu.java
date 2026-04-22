package de.dhbw.ui.menus;

import de.dhbw.application.loan.LoanService;
import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.ui.InputHandler;
import de.dhbw.ui.Menu;
import de.dhbw.ui.OutputFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import de.dhbw.util.UUID;

public class LoanMenu extends Menu {
    private final LoanService loanService;

    public LoanMenu(String title, InputHandler inputHandler, LoanService loanService) {
        super(title, inputHandler);
        this.loanService = loanService;

        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("Borrow Media", this::borrowMedia);
        addMenuItem("Return Media", this::returnMedia);
        addMenuItem("Renew Loan", this::renewLoan);
        addMenuItem("View All Loans", this::viewAllLoans);
        addMenuItem("View Overdue Loans", this::viewOverdueLoans);
        addMenuItem("View User Loans", this::viewUserLoans);
    }

    private void borrowMedia() {
        OutputFormatter.printHeader("Borrow Media");

        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<UUID> userUuid = parseUuid(userId, "User ID");
        if (userUuid.isEmpty()) {
            return;
        }

        String mediaIdsInput = inputHandler.readNonEmptyString("Enter Media ID(s) (comma-separated): ");
        Optional<List<UUID>> mediaIds = parseUuidList(mediaIdsInput, "Media ID");
        if (mediaIds.isEmpty()) {
            return;
        }

        try {
            Loan loan = loanService.borrowMedia(userUuid.get(), mediaIds.get());
            OutputFormatter.printSuccess("Media borrowed successfully!");
            OutputFormatter.printLoan(loan);
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void returnMedia() {
        String loanId = inputHandler.readNonEmptyString("Enter Loan ID: ");
        Optional<UUID> loanUuid = parseUuid(loanId, "Loan ID");
        if (loanUuid.isEmpty()) {
            return;
        }

        try {
            Optional<Loan> loanOpt = loanService.getLoanById(loanUuid.get());
            if (loanOpt.isEmpty()) {
                OutputFormatter.printWarning("Loan not found.");
                return;
            }

            Loan loan = loanOpt.get();
            loanService.returnMedia(loanUuid.get(), List.copyOf(loan.getMediaIds()));
            OutputFormatter.printSuccess("Media returned successfully!");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void renewLoan() {
        String loanId = inputHandler.readNonEmptyString("Enter Loan ID: ");
        Optional<UUID> loanUuid = parseUuid(loanId, "Loan ID");
        if (loanUuid.isEmpty()) {
            return;
        }
        LocalDate newDate = inputHandler.readDate("New Due Date");

        try {
            loanService.renewLoan(loanUuid.get(), newDate);
            OutputFormatter.printSuccess("Loan renewed successfully!");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void viewAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        OutputFormatter.printLoanList(loans);
    }

    private void viewOverdueLoans() {
        List<Loan> loans = loanService
                .getAllLoans()
                .stream()
                .filter(
                        loan ->
                                loan.getStatus() == LoanStatus.OVERDUE ||
                                        (loan.getDueDate() != null &&
                                                loan.getDueDate().isBefore(LocalDate.now()) &&
                                                loan.getStatus() != LoanStatus.RETURNED)
                )
                .toList();
        OutputFormatter.printLoanList(loans);
    }

    private void viewUserLoans() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<UUID> userUuid = parseUuid(userId, "User ID");
        if (userUuid.isEmpty()) {
            return;
        }
        List<Loan> loans = loanService.getLoansByUserId(userUuid.get());
        OutputFormatter.printLoanList(loans);
    }

    private Optional<UUID> parseUuid(String rawId, String idLabel) {
        try {
            return Optional.of(UUID.fromString(rawId));
        } catch (IllegalArgumentException e) {
            OutputFormatter.printError("Invalid " + idLabel + " format. Please enter an ID (e.g. USR00001).");
            return Optional.empty();
        }
    }

    private Optional<List<UUID>> parseUuidList(String rawIds, String idLabel) {
        String[] parts = rawIds.split(",");
        List<UUID> ids = new ArrayList<>();

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            Optional<UUID> parsedId = parseUuid(trimmed, idLabel);
            if (parsedId.isEmpty()) {
                return Optional.empty();
            }
            ids.add(parsedId.get());
        }

        if (ids.isEmpty()) {
            OutputFormatter.printError("Please provide at least one valid " + idLabel + ".");
            return Optional.empty();
        }

        return Optional.of(ids);
    }
}
