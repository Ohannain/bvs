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
        report.addDataPoint("period_loans", periodLoans);

        long activeLoans = loans.stream()
            .filter(l -> l.getStatus() == LoanStatus.ACTIVE)
            .count();
        report.addDataPoint("active_loans", activeLoans);

        long overdueLoans = loans.stream()
            .filter(l ->
                l.getStatus() == LoanStatus.OVERDUE ||
                (l.getDueDate() != null &&
                 l.getDueDate().isBefore(LocalDate.now()) &&
                 l.getStatus() != LoanStatus.RETURNED)
            )
            .count();
        report.addDataPoint("overdue_loans", overdueLoans);

        return report;
    }
}
