package de.dhbw.ui.menus;

import de.dhbw.application.loan.LoanService;
import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.ui.InputHandler;
import de.dhbw.ui.Menu;
import de.dhbw.ui.OutputFormatter;
import de.dhbw.util.UUID;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        Optional<UUID> userUuid = UUID.parseUuid(userId);
        if (userUuid.isEmpty()) {
            return;
        }

        String mediaIdsInput = inputHandler.readNonEmptyString(
            "Enter Media ID(s) (comma-separated): "
        );
        Optional<List<UUID>> mediaIds = UUID.parseUuidList(mediaIdsInput);
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
        Optional<UUID> loanUuid = UUID.parseUuid(loanId);
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
        Optional<UUID> loanUuid = UUID.parseUuid(loanId);
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
        Optional<UUID> userUuid = UUID.parseUuid(userId);
        if (userUuid.isEmpty()) {
            return;
        }
        List<Loan> loans = loanService.getLoansByUserId(userUuid.get());
        OutputFormatter.printLoanList(loans);
    }
}
