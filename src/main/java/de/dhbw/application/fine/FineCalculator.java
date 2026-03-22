package de.dhbw.application.fine;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.media.Media;

public class FineCalculator {
    public enum DamageLevel {
        MINOR,
        MODERATE,
        SEVERE,
        TOTAL
    }

    /**
     * calculateOverdueFine calculates a fine according to the days a loan is overdue.
     */
    public static double calculateOverdueFine(Loan loan) {
        if (loan == null) { return 0.0; }
        double fine = 0.0;

        int daysBetween = calculateDaysSince();

        if (daysBetween > 0) { fine = daysBetween * Config.DEFAULT_FINE_RATE_PER_DAY; }

        return fine;
    }

    /**
     * calculateLostItemFine calculates a fine for a lost loaned item.
     */
    public static double calculateLostItemFine(Media media) {
        if (media == null) { return 0.0; }

        return media.replacementCost;
    }

    /**
     * calculateDamageFine calculates a fine depending on the damage made to a loaned item
     */
    public static double calculateDamageFine(Media media, DamageLevel damageLevel) {
        return switch (damageLevel) {
            case MINOR -> media.replacementCost * 0.25;
            case MODERATE -> media.replacementCost * 0.5;
            case SEVERE -> media.replacementCost * 0.75;
            case TOTAL -> media.replacementCost;
            default -> 0;
        };
    }
}
