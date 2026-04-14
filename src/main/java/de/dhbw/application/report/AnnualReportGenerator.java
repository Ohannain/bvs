package de.dhbw.application.report;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.report.Report;
import de.dhbw.domain.report.ReportType;
import de.dhbw.domain.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AnnualReportGenerator {

    public static Report generate(int year, List<User> users, List<Media> media,
                                 List<Loan> loans, List<Fine> fines) {
        UUID reportId = "REP" + UUID.randomUUID();
        Report report = new Report(reportId, ReportType.ANNUAL, "Annual Report " + year);

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        // Filter data for the year
        List<Loan> yearLoans = loans.stream()
                .filter(l -> l.getLoanDate().getYear() == year)
                .collect(Collectors.toList());

        List<Fine> yearFines = fines.stream()
                .filter(f -> f.getIssueDate().getYear() == year)
                .collect(Collectors.toList());

        // Calculate statistics
        report.addDataPoint("year", year);
        report.addDataPoint("total_users", users.size());
        report.addDataPoint("total_media", media.size());
        report.addDataPoint("total_loans", yearLoans.size());
        report.addDataPoint("total_fines", yearFines.size());

        double totalFineAmount = yearFines.stream()
                .mapToDouble(Fine::getAmount)
                .sum();
        report.addDataPoint("total_fine_amount", totalFineAmount);

        long overdueLoans = yearLoans.stream()
                .filter(Loan::isOverdue)
                .count();
        report.addDataPoint("overdue_loans", overdueLoans);

        // Build summary
        StringBuilder summary = new StringBuilder();
        summary.append("Annual Report for ").append(year).append("\n");
        summary.append("Total Users: ").append(users.size()).append("\n");
        summary.append("Total Media Items: ").append(media.size()).append("\n");
        summary.append("Total Loans: ").append(yearLoans.size()).append("\n");
        summary.append("Total Fines: ").append(yearFines.size()).append("\n");
        summary.append("Total Fine Amount: €").append(String.format("%.2f", totalFineAmount));

        report.setSummary(summary.toString());

        return report;
    }
}
