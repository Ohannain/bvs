package de.dhbw.domain.media;

public enum MediaType {
    BOOK(30, 0.50),
    DVD(7, 1.00),
    CD(14, 0.75),
    MAGAZINE(7, 0.25),
    EBOOK(21, 0.30);

    private final int defaultLoanDays;
    private final double dailyFineRate;

    MediaType(int defaultLoanDays, double dailyFineRate) {
        this.defaultLoanDays = defaultLoanDays;
        this.dailyFineRate = dailyFineRate;
    }

    public int getDefaultLoanDays() {
        return defaultLoanDays;
    }

    public double getDailyFineRate() {
        return dailyFineRate;
    }
}
