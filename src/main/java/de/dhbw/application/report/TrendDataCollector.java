package de.dhbw.application.report;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaType;
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
            "Trend Report - " + year + "/" + month
        );

        LocalDate inputMonth = LocalDate.of(year, month, 1);
        LocalDate startDate = inputMonth.minusMonths(5); // inclusive: 6 months total
        LocalDate endDate = inputMonth.withDayOfMonth(inputMonth.lengthOfMonth());

        report.setStartDate(startDate);
        report.setEndDate(endDate);

        Map<UUID, Media> mediaById = media.stream()
            .collect(Collectors.toMap(Media::getMediaId, Function.identity(), (a, b) -> a));

        Map<UUID, Long> bookBorrowCounts = new HashMap<>();

        for (Loan loan : loans) {
            if (loan.getIssueDate() == null) {
                continue;
            }

            LocalDate loanDate = loan.getIssueDate();
            if (loanDate.isBefore(startDate) || loanDate.isAfter(endDate)) {
                continue;
            }

            for (UUID mediaId : loan.getMediaIds()) {
                Media m = mediaById.get(mediaId);
                if (m != null && m.getMediaType() == MediaType.BOOK) {
                    bookBorrowCounts.merge(mediaId, 1L, Long::sum);
                }
            }
        }

        List<Map.Entry<UUID, Long>> topBooks = bookBorrowCounts.entrySet().stream()
            .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
            .limit(10)
            .toList();

        report.addDataPoint("year", year);
        report.addDataPoint("month", month);
        report.addDataPoint("window_start", startDate);
        report.addDataPoint("window_end", endDate);
        report.addDataPoint("total_window_loans", loans.stream()
            .filter(l -> l.getIssueDate() != null
                && !l.getIssueDate().isBefore(startDate)
                && !l.getIssueDate().isAfter(endDate))
            .count());
        report.addDataPoint("top_books_count", topBooks.size());

        for (int i = 0; i < topBooks.size(); i++) {
            UUID mediaId = topBooks.get(i).getKey();
            long borrowCount = topBooks.get(i).getValue();
            Media book = mediaById.get(mediaId);

            int rank = i + 1;
            report.addDataPoint("top_book_" + rank + "_id", mediaId);
            report.addDataPoint("top_book_" + rank + "_title", book != null ? book.getTitle() : "Unknown");
            report.addDataPoint("top_book_" + rank + "_count", borrowCount);
        }

        return report;
    }
}
