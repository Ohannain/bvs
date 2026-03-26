package de.dhbw;

import de.dhbw.domain.media.Book;
import de.dhbw.domain.media.CD;
import de.dhbw.domain.media.DVD;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.user.User;
import de.dhbw.domain.user.UserRole;
import de.dhbw.util.Logger;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            // Fallback if UTF-8 setup fails
        }

        Logger.setConsoleOutput(true);
        Logger.setLevel(Logger.Level.INFO);

        Logger.info("Welcome to the Library Management System!");
        Logger.info("Initializing...");

        try {
            // Test data
            User user = new User("U001", "Max", "Mustermann", "max@example.com");
            user.setRole(UserRole.MEMBER);
            System.out.printf("User    : %s %s [%s]%n", user.getFirstName(), user.getLastName(), user.getRole());

            Book book = new Book("M001", "Clean Code", "Robert C. Martin", "Prentice Hall");
            book.setPages(431); book.setGenre("Software Engineering"); book.setStatus(MediaStatus.AVAILABLE);

            DVD dvd = new DVD("M002", "The Matrix", "Wachowski", "Warner Bros.");
            dvd.setDurationMinutes(136); dvd.setStatus(MediaStatus.AVAILABLE);

            CD cd = new CD("M003", "Kind of Blue", "Miles Davis", "Columbia");
            cd.setTrackCount(5); cd.setDurationMinutes(46); cd.setStatus(MediaStatus.AVAILABLE);

            // Output test data
            System.out.printf("Media   : [BOOK] %s – %d pages,  status: %s%n", book.getTitle(), book.getPages(), book.getStatus());
            System.out.printf("Media   : [DVD]  %s – %d min,    status: %s%n", dvd.getTitle(), dvd.getDurationMinutes(), dvd.getStatus());
            System.out.printf("Media   : [CD]   %s – %d tracks, status: %s%n", cd.getTitle(), cd.getTrackCount(), cd.getStatus());

            System.out.println();
            Logger.info("Initialization completed successfully.");

        } catch (Exception e) {
            System.err.println("FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
            Logger.error("Fatal error during initialization", e);
            System.exit(1);
        }
    }
}
