package de.dhbw.util;

public final class IsbnUtils {
    public static boolean isValidIsbn(String raw) {
        if (raw == null) return false;
        String s = raw.replaceAll("[\\s-]", "");
        if (s.length() == 10) {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                char c = s.charAt(i);
                if (!Character.isDigit(c)) return false;
                sum += (10 - i) * (c - '0');
            }
            char last = s.charAt(9);
            int check = (last == 'X' || last == 'x') ? 10 : (Character.isDigit(last) ? last - '0' : -1);
            if (check < 0) return false;
            sum += check;
            return sum % 11 == 0;
        } else if (s.length() == 13) {
            if (!s.chars().allMatch(Character::isDigit)) return false;
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int d = s.charAt(i) - '0';
                sum += d * ((i % 2 == 0) ? 1 : 3);
            }
            int check = (10 - (sum % 10)) % 10;
            return check == (s.charAt(12) - '0');
        }
        return false;
    }

    public static boolean isIsbnFormatOnly(String raw) {
        if (raw == null) return false;
        String s = raw.replaceAll("[\\s-]", "");
        return s.matches("\\d{13}") || s.matches("\\d{9}[\\dXx]{1}");
    }
}
