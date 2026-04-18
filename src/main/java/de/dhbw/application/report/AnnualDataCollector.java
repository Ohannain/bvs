package de.dhbw.application.report;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.report.Report;
import de.dhbw.domain.report.ReportType;
import de.dhbw.domain.user.User;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import de.dhbw.util.UUID;
import java.util.stream.Collectors;

public class AnnualDataCollector {

    public static Report generate(
        int year,
        List<User> users,
        List<Media> media,
        List<Loan> loans,
        List<Fine> fines
    ) {
        UUID reportId = UUID.nextReportId();
        Report report = new Report(
            reportId,
            ReportType.ANNUAL,
            "Annual Report " + year
        );

        report.addDataPoint("year", year);
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        report.setStartDate(startDate);
        report.setEndDate(endDate);


        //user data
        report.addDataPoint("total_users", users.size());

        long newUsers = users
            .stream()
            .filter(u -> u.getRegistrationDate().getYear() == year)
            .count();
        report.addDataPoint("new_users", newUsers);

        //media data
        report.addDataPoint("total_media", media.size());

        //fine data
        report.addDataPoint("total_fines", fines.size());

        List<Fine> yearFines = fines
            .stream()
            .filter(f -> f.getIssueDate().getYear() == year)
            .collect(Collectors.toList());
        report.addDataPoint("year_fines", yearFines.size());

        double totalFineAmount = yearFines
            .stream()
            .mapToDouble(Fine::getAmount)
            .sum();
        report.addDataPoint("total_fine_amount", totalFineAmount);

        //loan data
        report.addDataPoint("total_loans", loans.size());

        List<Loan> yearLoans = loans
            .stream()
            .filter(
                l ->
                    l.getIssueDate().getYear() == year ||
                    l.getDueDate().getYear() == year
            )
            .collect(Collectors.toList());
        report.addDataPoint("year_loans", yearLoans.size());

        long overdueLoans = yearLoans
            .stream()
            .filter(l -> l.getStatus() == LoanStatus.OVERDUE)
            .count();
        report.addDataPoint("overdue_loans", overdueLoans);

        return report;
    }
}
