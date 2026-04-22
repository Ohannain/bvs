package de.dhbw.application.report;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.report.Report;
import de.dhbw.domain.report.ReportType;
import de.dhbw.util.UUID;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TrendDataCollector {

    public static Report generate(
        int year,
        int month,
        List<Media> media,
        List<Loan> loans
    ) {
        UUID reportId = UUID.nextReportId();
        Report report = new Report(
            reportId,
            ReportType.TREND_ANALYSIS,
            "Trend Report - " + year + "/" + String.format("%02d", month)
        );

        LocalDate inputMonth = LocalDate.of(year, month, 1);
        LocalDate startDate = inputMonth.minusMonths(5); // inclusive: 6 months total
        LocalDate endDate = inputMonth.withDayOfMonth(inputMonth.lengthOfMonth());

        report.setStartDate(startDate);
        report.setEndDate(endDate);


        report.addDataPoint("year", year);
        report.addDataPoint("month", month);
        report.addDataPoint("window_start", startDate);
        report.addDataPoint("window_end", endDate);

        long total_window_loans = loans
            .stream()
            .filter(l -> l.getIssueDate() != null
                && !l.getIssueDate().isBefore(startDate)
                && !l.getIssueDate().isAfter(endDate))
            .count();
        report.addDataPoint("total_window_loans", total_window_loans);

        // Build once for fast media lookup by id
        Map<UUID, Media> mediaById = media.stream()
            .collect(Collectors.toMap(Media::getMediaId, Function.identity()));

        for (int i = 0; i < 6; i++) {
            LocalDate currentMonth = inputMonth.minusMonths(5 - i);

            // All loans issued in this month
            List<Loan> monthLoans = loans.stream()
                .filter(l ->
                       l.getIssueDate() != null
                    && l.getIssueDate().getYear() == currentMonth.getYear()
                    && l.getIssueDate().getMonthValue() == currentMonth.getMonthValue())
                .toList();

            report.addDataPoint(
                "total_loans_month_" + currentMonth.getYear() + "_" + currentMonth.getMonthValue(),
                monthLoans.size()
            );

            // Aggregate loan counts per media across all mediaIds in each loan
            Map<UUID, Long> loanCountByMediaId = monthLoans.stream()
                .map(Loan::getMediaIds)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            // Top 5 most loaned media for this month
            List<Map<String, Object>> top5MostLoaned = loanCountByMediaId
                .entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> {
                    UUID mediaId = entry.getKey();
                    Long loanCount = entry.getValue();
                    Media m = mediaById.get(mediaId);

                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("media_id", mediaId);
                    row.put("title", m != null ? m.getTitle() : "Unknown");
                    row.put("type", m != null ? m.getMediaType() : null);
                    row.put("loan_count", loanCount);
                    return row;
                })
                .toList();

            // One datapoint per month containing that month's top-5 list
            report.addDataPoint(
                "top5_media_month_" + currentMonth.getYear() + "_" + currentMonth.getMonthValue(),
                top5MostLoaned
            );
        }

        return report;
    }
}
