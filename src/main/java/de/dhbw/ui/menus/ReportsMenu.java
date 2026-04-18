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
import de.dhbw.application.report.MahnDataCollector;
import de.dhbw.domain.report.Report;

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
        addMenuItem("Generate Mahn Report", this::generateMahnReport);
    }

private void generateMahnReport() {
    String rawUserId = inputHandler.readString("Enter User ID: ");
    Optional<UUID> userIdOpt = parseUuid(rawUserId, "user ID");
    if (userIdOpt.isEmpty()) {
        return;
    }

    Optional<de.dhbw.domain.user.User> userOpt = userService.getUserById(userIdOpt.get());
    if (userOpt.isEmpty()) {
        OutputFormatter.printError("User not found.");
        return;
    }

    Report report = MahnDataCollector.generate(userOpt.get(), fineService.getAllFines());

    OutputFormatter.printHeader(report.getTitle());

    Object userNameObj = report.getDataPoint("user_name");
    Object totalFinesObj = report.getDataPoint("total_fines");
    Object totalFineAmountObj = report.getDataPoint("total_fine_amount");

        String userName = userNameObj != null ? userNameObj.toString() : "Unknown User";
        int totalFines = totalFinesObj instanceof Number ? ((Number) totalFinesObj).intValue() : 0;
        double totalFineAmount = totalFineAmountObj instanceof Number ? ((Number) totalFineAmountObj).doubleValue() : 0.0;

    System.out.println("User: " + userName);
    System.out.println("Total Fine Count: " + totalFines);
    System.out.println("Total Fine Amount: €" + String.format("%.2f", totalFineAmount));

    System.out.println();
    System.out.println("Fine Details:");
    System.out.println("-".repeat(36));

    if (totalFines == 0) {
        System.out.println("No fines found for this user.");
    } else {
        List<Fine> fineDetails = new ArrayList<>();
        for (Map.Entry<String, Object> entry : report.getDataPoints().entrySet()) {
            if (!entry.getKey().startsWith("fine ")) {
                continue;
            }
            if (entry.getValue() instanceof Fine fine) {
                fineDetails.add(fine);
            }
        }

        Map<FineStatus, Integer> statusOrder = new HashMap<>();
        statusOrder.put(FineStatus.OVERDUE, 0);
        statusOrder.put(FineStatus.PENDING, 1);
        statusOrder.put(FineStatus.WAIVED, 2);
        statusOrder.put(FineStatus.PAID, 3);

        fineDetails.sort(Comparator
            .comparingInt((Fine f) -> statusOrder.getOrDefault(f.getStatus(), Integer.MAX_VALUE))
            .thenComparing(Fine::getIssueDate, Comparator.nullsLast(Comparator.naturalOrder())));

        String headerFormat = "%-3s | %-10s | %-7s | %-7s%n";
        String rowFormat = "%03d | %-10s | %-7s | %7s%n";

        System.out.printf(headerFormat, "No.", "Date", "Status", "Amount");
        System.out.println("-".repeat(36));

        int index = 1;
        for (Fine fine : fineDetails) {
            String date = fine.getIssueDate() != null ? DateUtils.format(fine.getIssueDate()) : "N/A";
            String status = fine.getStatus() != null ? fine.getStatus().name() : "UNKNOWN";
            String amount = "€" + String.format("%.2f", fine.getAmount());

            System.out.printf(rowFormat,
                index++,
                date,
                status,
                amount
            );
        }
    }
    System.out.println("-".repeat(36));
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
