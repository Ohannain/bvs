package de.dhbw.application.user;

import de.dhbw.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    public static List<String> validate(User user) {
        List<String> errors = new ArrayList<>();

        if (user == null) {
            errors.add("User cannot be null");
            return errors;
        }

        if (user.getUserId() == null) {
            errors.add("User ID is required");
        }

        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.add("Invalid email format");
        }

        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(user.getPhone().replaceAll("[\\s-]", "")).matches()) {
                errors.add("Invalid phone number format");
            }
        }

        if (user.getMaxBorrowLimit() < 0) {
            errors.add("Borrow limit cannot be negative");
        }

        if (user.getOutstandingFines() < 0) {
            errors.add("Outstanding fines cannot be negative");
        }

        return errors;
    }

    public static boolean isValid(User user) {
        return validate(user).isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null
                && !email.trim().isEmpty()
                && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true;
        }
        String normalized = phone.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(normalized).matches();
    }
}
