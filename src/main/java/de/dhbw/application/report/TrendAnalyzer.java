package de.dhbw.application.report;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaType;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TrendAnalyzer {

    public static Map<String, Integer> analyzeMostBorrowedMedia(List<Loan> loans, List<Media> mediaList) {
        Map<String, Integer> borrowCount = new HashMap<>();

        for (Loan loan : loans) {
            borrowCount.merge(loan.getMediaId(), 1, Integer::sum);
        }

        // Sort by count descending
        return borrowCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static Map<MediaType, Long> analyzeMediaTypePopularity(List<Loan> loans, List<Media> mediaList) {
        Map<String, Media> mediaMap = mediaList.stream()
                .collect(Collectors.toMap(Media::getMediaId, m -> m));

        return loans.stream()
                .map(Loan::getMediaId)
                .filter(mediaMap::containsKey)
                .map(id -> mediaMap.get(id).getMediaType())
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
    }

    public static Map<String, Long> analyzeGenrePopularity(List<Loan> loans, List<Media> mediaList) {
        Map<String, Media> mediaMap = mediaList.stream()
                .collect(Collectors.toMap(Media::getMediaId, m -> m));

        return loans.stream()
                .map(Loan::getMediaId)
                .filter(mediaMap::containsKey)
                .map(id -> mediaMap.get(id).getCategory())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()));
    }

    public static Map<Integer, Long> analyzeMonthlyTrend(List<Loan> loans, int year) {
        return loans.stream()
                .filter(l -> l.getLoanDate().getYear() == year)
                .collect(Collectors.groupingBy(
                        l -> l.getLoanDate().getMonthValue(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    public static List<String> identifyTrendingMedia(List<Loan> loans, List<Media> mediaList, int recentDays) {
        LocalDate cutoffDate = LocalDate.now().minusDays(recentDays);

        Map<String, Long> recentBorrowings = loans.stream()
                .filter(l -> l.getLoanDate().isAfter(cutoffDate))
                .collect(Collectors.groupingBy(Loan::getMediaId, Collectors.counting()));

        return recentBorrowings.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static double calculateAverageBorrowingRate(List<Loan> loans, int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);

        long recentLoans = loans.stream()
                .filter(l -> l.getLoanDate().isAfter(cutoffDate))
                .count();

        return days > 0 ? (double) recentLoans / days : 0.0;
    }
}
