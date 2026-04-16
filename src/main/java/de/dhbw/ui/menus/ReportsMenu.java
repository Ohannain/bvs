package de.dhbw.ui.menus;

import de.dhbw.application.fine.FineService;
import de.dhbw.application.loan.LoanService;
import de.dhbw.application.media.MediaService;
import de.dhbw.application.user.UserService;
import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.fine.FineStatus;
import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.domain.media.Media;
import de.dhbw.ui.InputHandler;
import de.dhbw.ui.Menu;
import de.dhbw.ui.OutputFormatter;
import de.dhbw.util.DateUtils;
import de.dhbw.util.UUID;

import java.time.LocalDate;
import java.util.*;

public class ReportsMenu extends Menu {
    private final LoanService loanService;
    private final UserService userService;
    private final MediaService mediaService;
    private final FineService fineService;

    public ReportsMenu(
        String title,
        InputHandler inputHandler,
        LoanService loanService,
        UserService userService,
        MediaService mediaService,
        FineService fineService
    ) {
        super(title, inputHandler);
        this.loanService = loanService;
        this.userService = userService;
        this.mediaService = mediaService;
        this.fineService = fineService;

        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("Annual Report", this::generateAnnualReport);
        addMenuItem("Fine Statistics", this::generateFineStatistics);
        addMenuItem("Usage Report", this::generateUsageReport);
        addMenuItem("Popular Media Report", this::generatePopularityReport);
        addMenuItem("Overdue Items Report", this::generateOverdueReport);
    }

    private void generateAnnualReport() {
        int year = inputHandler.readInt("Enter Year: ", 2000, 2100);

        OutputFormatter.printHeader("Annual Report " + year);
        long totalLoans = loanService
                .getAllLoans()
                .stream()
                .filter(l -> l.getIssueDate() != null && l.getIssueDate().getYear() == year)
                .count();
        long totalUsers = userService
                .getAllUsers()
                .stream()
                .filter(
                        u ->
                                u.getRegistrationDate() != null &&
                                        u.getRegistrationDate().getYear() == year
                )
                .count();
        long totalMedia = mediaService.getAllMedia().size();
        long totalFines = fineService
                .getAllFines()
                .stream()
                .filter(f -> f.getIssueDate() != null && f.getIssueDate().getYear() == year)
                .count();

        System.out.println("Total Loans: " + totalLoans);
        System.out.println("Total Users: " + totalUsers);
        System.out.println("Total Media: " + totalMedia);
        System.out.println("Total Fines: " + totalFines);
    }

    private void generateFineStatistics() {
        List<Fine> fines = fineService.getAllFines();
        long pending = fines
                .stream()
                .filter(f -> f.getStatus() == FineStatus.PENDING)
                .count();
        long paid = fines
                .stream()
                .filter(f -> f.getStatus() == FineStatus.PAID)
                .count();
        double pendingAmount = fines
                .stream()
                .filter(f -> f.getStatus() == FineStatus.PENDING)
                .mapToDouble(Fine::getAmount)
                .sum();
        double paidAmount = fines
                .stream()
                .filter(f -> f.getStatus() == FineStatus.PAID)
                .mapToDouble(Fine::getAmount)
                .sum();

        OutputFormatter.printHeader("Fine Statistics");
        System.out.println("Total Fines: " + fines.size());
        System.out.println("Pending Fines: " + pending);
        System.out.println("Paid Fines: " + paid);
        System.out.println("Total Pending Amount: â‚¬" + String.format("%.2f", pendingAmount));
        System.out.println("Total Paid Amount: â‚¬" + String.format("%.2f", paidAmount));
    }

    private void generateUsageReport() {
        LocalDate startDate = inputHandler.readDate("Start Date");
        LocalDate endDate = inputHandler.readDate("End Date");

        long periodLoans = loanService
                .getAllLoans()
                .stream()
                .filter(
                        l ->
                                l.getIssueDate() != null &&
                                        !l.getIssueDate().isBefore(startDate) &&
                                        !l.getIssueDate().isAfter(endDate)
                )
                .count();
        long activeLoans = loanService
                .getAllLoans()
                .stream()
                .filter(l -> l.getStatus() == LoanStatus.ACTIVE)
                .count();
        long overdueLoans = loanService
                .getAllLoans()
                .stream()
                .filter(
                        l ->
                                l.getStatus() == LoanStatus.OVERDUE ||
                                        (l.getDueDate() != null &&
                                                l.getDueDate().isBefore(LocalDate.now()) &&
                                                l.getStatus() != LoanStatus.RETURNED)
                )
                .count();

        OutputFormatter.printHeader("Usage Report");
        System.out.println(
                "Period: " + DateUtils.format(startDate) + " to " + DateUtils.format(endDate)
        );
        System.out.println("Period Loans: " + periodLoans);
        System.out.println("Active Loans: " + activeLoans);
        System.out.println("Overdue Loans: " + overdueLoans);
    }

    private void generatePopularityReport() {
        OutputFormatter.printHeader("Most Popular Media");

        Map<UUID, Long> borrowCounts = new LinkedHashMap<>();
        for (Loan loan : loanService.getAllLoans()) {
            for (UUID mediaId : loan.getMediaIds()) {
                borrowCounts.merge(mediaId, 1L, Long::sum);
            }
        }

        if (borrowCounts.isEmpty()) {
            OutputFormatter.printWarning("No loan data available for popularity report.");
            return;
        }

        List<Map.Entry<UUID, Long>> topMedia = borrowCounts
                .entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .toList();

        System.out.println("\nTop Borrowed Media Items:");
        System.out.println("-".repeat(60));

        int rank = 1;
        for (Map.Entry<UUID, Long> entry : topMedia) {
            Optional<Media> media = mediaService.getMediaById(entry.getKey());
            if (media.isPresent()) {
                System.out.printf(
                        "%d. %s (%s) - %d borrows%n",
                        rank,
                        media.get().getTitle(),
                        media.get().getMediaType(),
                        entry.getValue()
                );
                rank++;
            }
        }
        System.out.println("-".repeat(60));
    }

    private void generateOverdueReport() {
        long overdueCount = loanService
                .getAllLoans()
                .stream()
                .filter(
                        l ->
                                l.getStatus() == LoanStatus.OVERDUE ||
                                        (l.getDueDate() != null &&
                                                l.getDueDate().isBefore(LocalDate.now()) &&
                                                l.getStatus() != LoanStatus.RETURNED)
                )
                .count();

        OutputFormatter.printHeader("Overdue Items Report");
        System.out.println("Overdue Items Count: " + overdueCount);
    }

    private Optional<UUID> parseUuid(String rawId, String idLabel) {
        try {
            return Optional.of(UUID.fromString(rawId));
        } catch (IllegalArgumentException e) {
            OutputFormatter.printError("Invalid " + idLabel + " format. Please enter an ID (e.g. USR00001).");
            return Optional.empty();
        }
    }
}
