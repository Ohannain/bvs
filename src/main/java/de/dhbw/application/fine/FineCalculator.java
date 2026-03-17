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
        return 1;
    }

    /**
     * calculateLostItemFine calculates a fine for a lost loaned item.
     */
    public static double calculateLostItemFine(Media media) {
        return 1;
    }

    /**
     * calculateDamageFine calculates a fine depending on the damage made to a loaned item
     */
    public static double calculateDamageFine(Media media, DamageLevel damageLevel) {
        double replacementCost = 0;

        return switch (damageLevel) {
            case MINOR -> replacementCost * 0.25;
            case MODERATE -> replacementCost * 0.5;
            case SEVERE -> replacementCost * 0.75;
            case TOTAL -> replacementCost;
            default -> 0;
        };
    }
}
