package de.dhbw.ui;

import de.dhbw.domain.media.Media;
import de.dhbw.domain.user.User;
import de.dhbw.util.DateUtils;

import java.util.List;

public class OutputFormatter {
    private static final int SEPARATOR_LENGTH = 80;

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("=".repeat(SEPARATOR_LENGTH));
        System.out.println(centerText(title));
        System.out.println("=".repeat(SEPARATOR_LENGTH));
    }

    public static void printSeparator() {
        System.out.println("-".repeat(SEPARATOR_LENGTH));
    }

    public static void printSuccess(String message) {
        System.out.println("✓ SUCCESS: " + message);
    }

    public static void printError(String message) {
        System.out.println("✗ ERROR: " + message);
    }

    public static void printWarning(String message) {
        System.out.println("⚠ WARNING: " + message);
    }

    public static void printInfo(String message) {
        System.out.println("ℹ INFO: " + message);
    }

    public static void printUser(User user) {
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Name: " + user.getFullName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A"));
        System.out.println("Role: " + user.getRole());
        System.out.println("Status: " + user.getStatus());
        System.out.println("Registration Date: " + DateUtils.format(user.getRegistrationDate()));
        System.out.println("Borrowed Media: " + user.getBorrowedMediaIds().size() + "/" + user.getMaxBorrowLimit());
        System.out.println("Outstanding Fines: €" + String.format("%.2f", user.getOutstandingFines()));
    }

    public static void printUserList(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        System.out.printf("%-15s %-25s %-30s %-15s %-10s%n",
                "User ID", "Name", "Email", "Status", "Borrowed");
        printSeparator();
        for (User user : users) {
            System.out.printf("%-15s %-25s %-30s %-15s %-10s%n",
                    user.getUserId(),
                    truncate(user.getFullName(), 25),
                    truncate(user.getEmail(), 30),
                    user.getStatus(),
                    user.getBorrowedMediaIds().size() + "/" + user.getMaxBorrowLimit());
        }
    }

    public static void printMedia(Media media) {
        System.out.println("Media ID: " + media.getMediaId());
        System.out.println("Type: " + media.getMediaType());
        System.out.println("Title: " + media.getTitle());
        System.out.println("Author/Artist: " + media.getAuthor());
        System.out.println("Publisher: " + media.getPublisher());
        System.out.println("Status: " + media.getStatus());
        if (media.getIsbn() != null) {
            System.out.println("ISBN: " + media.getIsbn());
        }
        if (media.getCurrentBorrowerId() != null) {
            System.out.println("Current Borrower: " + media.getCurrentBorrowerId());
            System.out.println("Due Date: " + DateUtils.format(media.getDueDate()));
        }
    }

    public static void printMediaList(List<Media> mediaList) {
        if (mediaList.isEmpty()) {
            System.out.println("No media found.");
            return;
        }
        System.out.printf("%-12s %-8s %-35s %-25s %-15s%n",
                "Media ID", "Type", "Title", "Author/Artist", "Status");
        printSeparator();
        for (Media media : mediaList) {
            System.out.printf("%-12s %-8s %-35s %-25s %-15s%n",
                    media.getMediaId(),
                    media.getMediaType(),
                    truncate(media.getTitle(), 35),
                    truncate(media.getAuthor(), 25),
                    media.getStatus());
        }
    }

    private static String centerText(String text) {
        int padding = (SEPARATOR_LENGTH - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
