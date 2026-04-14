package de.dhbw;

import de.dhbw.application.fine.FineService;
import de.dhbw.application.loan.LoanService;
import de.dhbw.domain.media.Book;
import de.dhbw.domain.media.CD;
import de.dhbw.domain.media.DVD;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.user.User;
import de.dhbw.domain.user.UserRole;
import de.dhbw.persistence.fine.FineRepository;
import de.dhbw.persistence.fine.JsonFineRepository;
import de.dhbw.persistence.loan.JsonLoanRepository;
import de.dhbw.persistence.loan.LoanRepository;
import de.dhbw.persistence.media.JsonMediaRepository;
import de.dhbw.persistence.media.MediaRepository;
import de.dhbw.persistence.user.JsonUserRepository;
import de.dhbw.persistence.user.UserRepository;
import de.dhbw.util.Logger;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        try {
            System.setOut(
                new PrintStream(System.out, true, StandardCharsets.UTF_8)
            );
            System.setErr(
                new PrintStream(System.err, true, StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            // Fallback if UTF-8 setup fails
        }

        Logger.setConsoleOutput(true);
        Logger.setLevel(Logger.Level.INFO);

        Logger.info("Welcome to the Library Management System!");
        Logger.info("Initializing...");

        try {
            // Test data
            User user = new User(
                UUID.randomUUID(),
                "Max",
                "Mustermann",
                "max@example.com"
            );
            user.setRole(UserRole.MEMBER);
            System.out.printf(
                "User    : %s %s [%s]%n",
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
            );

            Book book = new Book(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                "Prentice Hall"
            );
            book.setPages(431);
            book.setGenre("Software Engineering");
            book.setStatus(MediaStatus.AVAILABLE);

            DVD dvd = new DVD(
                UUID.randomUUID(),
                "The Matrix",
                "Wachowski",
                "Warner Bros."
            );
            dvd.setDurationMinutes(136);
            dvd.setStatus(MediaStatus.AVAILABLE);

            CD cd = new CD(UUID.randomUUID(), "Kind of Blue", "Miles Davis", "Columbia");
            cd.setTrackCount(5);
            cd.setDurationMinutes(46);
            cd.setStatus(MediaStatus.AVAILABLE);

            // Output test data
            System.out.printf(
                "Media   : [BOOK] %s – %d pages,  status: %s%n",
                book.getTitle(),
                book.getPages(),
                book.getStatus()
            );
            System.out.printf(
                "Media   : [DVD]  %s – %d min,    status: %s%n",
                dvd.getTitle(),
                dvd.getDurationMinutes(),
                dvd.getStatus()
            );
            System.out.printf(
                "Media   : [CD]   %s – %d tracks, status: %s%n",
                cd.getTitle(),
                cd.getTrackCount(),
                cd.getStatus()
            );

            System.out.println();
            Logger.info("Initialization completed successfully.");
        } catch (Exception e) {
            System.err.println("FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
            Logger.error("Fatal error during initialization", e);
            System.exit(1);
        }

        try {
            File dataDirectory = new File("data");
            if (!dataDirectory.exists()) {
                dataDirectory.mkdirs();
                Logger.info("Created data directory");
            }

            FineRepository fineRepository = new JsonFineRepository();
            LoanRepository loanRepository = new JsonLoanRepository();
            MediaRepository mediaRepository = new JsonMediaRepository();
            UserRepository userRepository = new JsonUserRepository();

            Logger.info("Repositories initialized");

            FineService fineService = new FineService(fineRepository);
            LoanService loanService = new LoanService(
                loanRepository,
                mediaRepository,
                userRepository
            );

            Logger.info("Services initialized");
        } catch (Exception e) {
            System.err.println("FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
            Logger.error("Fatal error during system initialization", e);
            System.exit(1);
        }
    }
}
