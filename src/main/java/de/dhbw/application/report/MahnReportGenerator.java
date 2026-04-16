//this is absolutely not functional, it doesnt even compile

package de.dhbw.application.report;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.report.Report;
import de.dhbw.domain.report.ReportType;
import de.dhbw.domain.user.User;
import de.dhbw.util.UUID;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class MahnReportGenerator {

    public static Report generate(User user, List<Fine> fines) {
        UUID reportId = UUID.nextReportId();
        Report report = new Report(
            reportId,
            ReportType.MAHN,
            "Mahn Report - " + user.getFirstName() + " " + user.getLastName()
        );

        LocalDate today = LocalDate.now();

        List<Fine> userFines = fines
            .stream()
            .filter(f -> f.getUserId().equals(user.getId()))
            .collect(Collectors.toList());

        double totalFineAmount = userFines.stream().mapToDouble(Fine::getAmount).sum();

        report.addDataPoint("user_id", user.getId());
        report.addDataPoint("user_name", user.getFirstName() + " " + user.getLastName());
        report.addDataPoint("total_fines", userFines.size());
        report.addDataPoint("total_fine_amount", totalFineAmount);

        StringBuilder summary = new StringBuilder();
        summary.append("Mahn Report for ")
            .append(user.getFirstName())
            .append(" ")
            .append(user.getLastName())
            .append("\n");
        summary.append("Total Fines: ").append(userFines.size()).append("\n");
        summary.append("Total Fine Amount: €").append(String.format("%.2f", totalFineAmount));

        if (userFines.isEmpty()) {
            summary.append("\nNo fines found for this user.");
        } else {
            summary.append("\n\nFine Details:");
            for (Fine fine : userFines) {
                long daysSinceCreation = ChronoUnit.DAYS.between(fine.getIssueDate(), today);

                report.addDataPoint("fine_" + fine.getId() + "_amount", fine.getAmount());
                report.addDataPoint("fine_" + fine.getId() + "_days_since_creation", daysSinceCreation);

                summary.append("\n- Fine ")
                    .append(fine.getId())
                    .append(": €")
                    .append(String.format("%.2f", fine.getAmount()))
                    .append(", created ")
                    .append(daysSinceCreation)
                    .append(" day")
                    .append(daysSinceCreation == 1 ? "" : "s")
                    .append(" ago");
            }
        }

        report.setSummary(summary.toString());
        return report;
    }
}
