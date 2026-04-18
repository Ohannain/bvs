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
import de.dhbw.application.report.AnnualDataCollector;
import de.dhbw.application.report.FineDataCollector;
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
        addMenuItem("Fine Statistics", this::generateFineReport);
//        addMenuItem("Usage Report", this::generateUsageReport);
//        addMenuItem("Popular Media Report", this::generatePopularityReport);
//        addMenuItem("Overdue Items Report", this::generateOverdueReport);
        addMenuItem("Generate Mahn Report", this::generateMahnReport);
    }

private void generateMahnReport() {
    String rawUserId = inputHandler.readString("Enter User ID: ");
    Optional<UUID> userIdOpt = UUID.parseUuid(rawUserId, "user ID");
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

        Report report = AnnualDataCollector.generate(
            year,
            userService.getAllUsers(),
            mediaService.getAllMedia(),
            loanService.getAllLoans(),
            fineService.getAllFines()
        );
        OutputFormatter.printHeader(report.getTitle());

        System.out.println("\nAll Time Data::");
        System.out.println("-".repeat(60));
        System.out.println("Users: " + report.getDataPoint("total_users"));
        System.out.println("Media: " + report.getDataPoint("total_media"));
        System.out.println("Loans: " + report.getDataPoint("total_loans"));

        System.out.println("\n" + "=".repeat(60) + "\n");

        System.out.println("Year Data for " + report.getDataPoint("year") + ":");
        System.out.println("-".repeat(60));
        System.out.println("New Users: " + report.getDataPoint("new_users"));
        System.out.println("Loans: " + report.getDataPoint("year_loans"));
        System.out.println("Overdue Loans: " + report.getDataPoint("overdue_loans"));
        System.out.println("Fines: " + report.getDataPoint("year_fines"));
        System.out.println("Fine Amount: €" + String.format("%.2f", report.getDataPoint("total_fine_amount")));
    }

    private void generateFineReport() {
        Report report = FineDataCollector.generate(
            fineService.getAllFines()
        );
        OutputFormatter.printHeader(report.getTitle());

        System.out.println("Total Fines: " + report.getDataPoint("total_fines"));
        System.out.println("Total Fine Amount: €" + String.format("%.2f", report.getDataPoint("total_fine_amount")) + "\n");

        System.out.println("Average Fine Amount: €" + String.format("%.2f", report.getDataPoint("average_fine_amount")));
        System.out.println("Max Fine Amount: €" + String.format("%.2f", report.getDataPoint("max_fine_amount")) + "\n");

        System.out.println("Overdue Fines: " + report.getDataPoint("overdue_fines"));
        System.out.println("Overdue Fine Amount: €" + String.format("%.2f", report.getDataPoint("overdue_fine_amount")) + "\n");

        System.out.println("Pending Fines: " + report.getDataPoint("unpaid_fines"));
        System.out.println("Pending Fine Amount: €" + String.format("%.2f", report.getDataPoint("unpaid_fine_amount")) + "\n");

        System.out.println("Paid Fines: " + report.getDataPoint("paid_fines"));
        System.out.println("Paid Fine Amount: €" + String.format("%.2f", report.getDataPoint("paid_fine_amount")) + "\n");

        System.out.println("Waived Fines: " + report.getDataPoint("waived_fines"));
        System.out.println("Waived Fine Amount: €" + String.format("%.2f", report.getDataPoint("waived_fine_amount")));
    }

//    private void generateUsageReport() {
//        LocalDate startDate = inputHandler.readDate("Start Date");
//        LocalDate endDate = inputHandler.readDate("End Date");
//
//
//        OutputFormatter.printHeader("Usage Report");
//        System.out.println(
//                "Period: " + DateUtils.format(startDate) + " to " + DateUtils.format(endDate)
//        );
//        System.out.println("Period Loans: " + periodLoans);
//        System.out.println("Active Loans: " + activeLoans);
//        System.out.println("Overdue Loans: " + overdueLoans);
//    }
//
//    private void generatePopularityReport() {
//        OutputFormatter.printHeader("Most Popular Media");
//
//        Map<UUID, Long> borrowCounts = new LinkedHashMap<>();
//        for (Loan loan : loanService.getAllLoans()) {
//            for (UUID mediaId : loan.getMediaIds()) {
//                borrowCounts.merge(mediaId, 1L, Long::sum);
//            }
//        }
//
//        if (borrowCounts.isEmpty()) {
//            OutputFormatter.printWarning("No loan data available for popularity report.");
//            return;
//        }
//
//        List<Map.Entry<UUID, Long>> topMedia = borrowCounts
//                .entrySet()
//                .stream()
//                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
//                .limit(10)
//                .toList();
//
//        System.out.println("\nTop Borrowed Media Items:");
//        System.out.println("-".repeat(60));
//
//        int rank = 1;
//        for (Map.Entry<UUID, Long> entry : topMedia) {
//            Optional<Media> media = mediaService.getMediaById(entry.getKey());
//            if (media.isPresent()) {
//                System.out.printf(
//                        "%d. %s (%s) - %d borrows%n",
//                        rank,
//                        media.get().getTitle(),
//                        media.get().getMediaType(),
//                        entry.getValue()
//                );
//                rank++;
//            }
//        }
//        System.out.println("-".repeat(60));
//    }
//
//    private void generateOverdueReport() {
//        long overdueCount = loanService
//                .getAllLoans()
//                .stream()
//                .filter(
//                        l ->
//                                l.getStatus() == LoanStatus.OVERDUE ||
//                                        (l.getDueDate() != null &&
//                                                l.getDueDate().isBefore(LocalDate.now()) &&
//                                                l.getStatus() != LoanStatus.RETURNED)
//                )
//                .count();
//
//        OutputFormatter.printHeader("Overdue Items Report");
//        System.out.println("Overdue Items Count: " + overdueCount);
//    }
}
