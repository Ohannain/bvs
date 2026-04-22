package de.dhbw.application.report;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.report.Report;
import de.dhbw.domain.report.ReportType;
import de.dhbw.domain.user.User;
import de.dhbw.util.UUID;

import java.util.List;
import java.util.stream.Collectors;

public class MahnDataCollector {

    public static Report generate(
        User user,
        List<Fine> fines
    ) {
        UUID reportId = UUID.nextReportId();
        Report report = new Report(
            reportId,
            ReportType.MAHN,
            "Mahn Report - " + user.getFirstName() + " " + user.getLastName()
        );

        List<Fine> userFines = fines
            .stream()
            .filter(f -> f.getUserId().equals(user.getUserId()))
            .collect(Collectors.toList());

        double totalFineAmount = userFines.stream().mapToDouble(Fine::getAmount).sum();

        report.addDataPoint("user_id", user.getUserId());
        report.addDataPoint("user_name", user.getFirstName() + " " + user.getLastName());
        report.addDataPoint("total_fines", userFines.size());
        report.addDataPoint("total_fine_amount", totalFineAmount);
        userFines.stream().forEach(f -> {
            report.addDataPoint("fine " + f.getFineId(), f);
        });

        return report;
    }
}
