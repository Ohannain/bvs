// this fucker probably needs a rewrite

package de.dhbw.application.report;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaType;
import de.dhbw.persistence.media.MediaRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TrendAnalyzer {
    private final MediaRepository mediaRepository;

    public TrendAnalyzer(MediaRepository mediaRepository) {
            this.mediaRepository = mediaRepository;
    }

    public static Map<UUID, Integer> analyzeMostBorrowedMedia(List<Loan> loans, List<Media> mediaList) {
        Map<UUID, Integer> borrowCount = new HashMap<>();

        for (Loan loan : loans) {
            for (UUID mediaId : loan.getMediaIds()) {
                borrowCount.merge(mediaId, 1, Integer::sum);
            }
        }

        // Sort by count descending
        return borrowCount.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static Map<MediaType, Long> analyzeMediaTypePopularity(List<Loan> loans, List<Media> mediaList) {
        Map<UUID, Media> mediaMap = mediaList.stream()
                .collect(Collectors.toMap(Media::getMediaId, m -> m));

        return loans.stream()
                .map(Loan::getMediaIds)
                .filter(mediaMap::containsKey)
                .map(id -> mediaMap.get(id).getMediaType())
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
    }

    public static Map<String, Long> analyzeGenrePopularity(List<Loan> loans, List<Media> mediaList) {
        Map<UUID, Media> mediaMap = mediaList.stream()
                .collect(Collectors.toMap(Media::getMediaId, m -> m));

        return loans.stream()
                .map(Loan::getMediaIds)
                .filter(mediaMap::containsKey)
                .map(id -> mediaMap.get(id).getCategory())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()));
    }

    public static Map<Object, Long> analyzeMonthlyTrend(List<Loan> loans, int year) {
        return loans.stream()
                .filter(l -> l.getIssueDate().getYear() == year)
                .collect(Collectors.groupingBy(
                        l -> l.getIssueDate().getMonthValue(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    public static Map<UUID, Integer> identifyTrendingMedia(List<Loan> loans, int recentDays) {
        LocalDate cutoffDate = LocalDate.now().minusDays(recentDays);
        Map<UUID, Integer> borrowCount = new HashMap<>();

        loans.stream()
             .filter(l -> l.getIssueDate().isAfter(cutoffDate))
             .forEach(loan -> {
                 for (UUID mediaId : loan.getMediaIds()) {
                     borrowCount.merge(mediaId, 1, Integer::sum);
                 }
             });

        return borrowCount.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }

    public static double calculateAverageBorrowingRate(List<Loan> loans, int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);

        long recentLoans = loans.stream()
                .filter(l -> l.getIssueDate().isAfter(cutoffDate))
                .count();

        return days > 0 ? (double) recentLoans / days : 0.0;
    }
}
