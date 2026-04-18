package de.dhbw.application.report;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.domain.report.Report;
import de.dhbw.domain.report.ReportType;
import de.dhbw.util.DateUtils;
import de.dhbw.util.UUID;

import java.time.LocalDate;
import java.util.List;

public class UsageDataCollector {

    public static Report generate(LocalDate startDate, LocalDate endDate, List<Loan> loans) {
        UUID reportId = UUID.nextReportId();
        Report report = new Report(
            reportId,
            ReportType.USAGE,
            "Usage Report"
        );

        report.setStartDate(startDate);
        report.setEndDate(endDate);

        long periodLoans = loans.stream()
            .filter(l ->
                l.getIssueDate() != null &&
                !l.getIssueDate().isBefore(startDate) &&
                !l.getIssueDate().isAfter(endDate)
            )
            .count();

        long activeLoans = loans.stream()
            .filter(l -> l.getStatus() == LoanStatus.ACTIVE)
            .count();

        long overdueLoans = loans.stream()
            .filter(l ->
                l.getStatus() == LoanStatus.OVERDUE ||
                (l.getDueDate() != null &&
                 l.getDueDate().isBefore(LocalDate.now()) &&
                 l.getStatus() != LoanStatus.RETURNED)
            )
            .count();

        report.addDataPoint("period_loans", periodLoans);
        report.addDataPoint("active_loans", activeLoans);
        report.addDataPoint("overdue_loans", overdueLoans);

        StringBuilder summary = new StringBuilder();
        summary.append("Usage Report\n");
        summary.append("Period: ")
            .append(DateUtils.format(startDate))
            .append(" to ")
            .append(DateUtils.format(endDate))
            .append("\n");
        summary.append("Period Loans: ").append(periodLoans).append("\n");
        summary.append("Active Loans: ").append(activeLoans).append("\n");
        summary.append("Overdue Loans: ").append(overdueLoans);

        report.setSummary(summary.toString());
        return report;
    }
}
