package de.dhbw.application.report;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.fine.FineStatus;
import de.dhbw.domain.report.Report;
import de.dhbw.domain.report.ReportType;
import de.dhbw.util.UUID;
import java.util.List;



public class FineDataCollector {

    public static Report generate(
        List<Fine> fines
    ) {
        UUID reportId = UUID.nextReportId();
        Report report = new Report(
            reportId,
            ReportType.ANNUAL,
            "Fine Report"
        );

        report.addDataPoint("total_fines", fines.size());

        double totalFineAmount = fines
            .stream()
            .mapToDouble(Fine::getAmount)
            .sum();
        report.addDataPoint("total_fine_amount", totalFineAmount);

        double averageFineAmount = fines
            .stream()
            .mapToDouble(Fine::getAmount)
            .average()
            .orElse(0.0);
        report.addDataPoint("average_fine_amount", averageFineAmount);

        double maxFineAmount = fines
            .stream()
            .mapToDouble(Fine::getAmount)
            .max()
            .orElse(0.0);
        report.addDataPoint("max_fine_amount", maxFineAmount);

        List<Fine> paidFines = fines
            .stream()
            .filter(f -> f.getStatus() == FineStatus.PAID)
            .toList();
        report.addDataPoint("paid_fines", paidFines.size());
        double paidFineAmount = paidFines
            .stream()
            .mapToDouble(Fine::getAmount)
            .sum();
        report.addDataPoint("paid_fine_amount", paidFineAmount);

        List<Fine> unpaidFines = fines
            .stream()
            .filter(f -> f.getStatus() == FineStatus.PENDING)
            .toList();
        report.addDataPoint("unpaid_fines", unpaidFines.size());
        double unpaidFineAmount = unpaidFines
            .stream()
            .mapToDouble(Fine::getAmount)
            .sum();
        report.addDataPoint("unpaid_fine_amount", unpaidFineAmount);

        List<Fine> overdueFines = fines
            .stream()
            .filter(f -> f.getStatus() == FineStatus.OVERDUE)
            .toList();
        report.addDataPoint("overdue_fines", overdueFines.size());
        double overdueFineAmount = overdueFines
            .stream()
            .mapToDouble(Fine::getAmount)
            .sum();
        report.addDataPoint("overdue_fine_amount", overdueFineAmount);

        List<Fine> waivedFines = fines
            .stream()
            .filter(f -> f.getStatus() == FineStatus.WAIVED)
            .toList();
        report.addDataPoint("waived_fines", waivedFines.size());
        double waivedFineAmount = waivedFines
            .stream()
            .mapToDouble(Fine::getAmount)
            .sum();
        report.addDataPoint("waived_fine_amount", waivedFineAmount);

    return report;
    }
}
