package de.dhbw;

import de.dhbw.application.fine.FineService;
import de.dhbw.application.loan.LoanService;
import de.dhbw.application.media.MediaService;
import de.dhbw.application.reservation.ReservationService;
import de.dhbw.application.user.UserService;
import de.dhbw.persistence.fine.FineRepository;
import de.dhbw.persistence.fine.JsonFineRepository;
import de.dhbw.persistence.loan.JsonLoanRepository;
import de.dhbw.persistence.loan.LoanRepository;
import de.dhbw.persistence.media.JsonMediaRepository;
import de.dhbw.persistence.media.MediaRepository;
import de.dhbw.persistence.reservation.JsonReservationRepository;
import de.dhbw.persistence.reservation.ReservationRepository;
import de.dhbw.persistence.user.JsonUserRepository;
import de.dhbw.persistence.user.UserRepository;
import de.dhbw.ui.InputHandler;
import de.dhbw.ui.MainMenu;
import de.dhbw.ui.OutputFormatter;
import de.dhbw.util.Logger;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        // Enable UTF-8 console output
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            // Fallback if UTF-8 setup fails
        }

        Logger.setConsoleOutput(true);
        Logger.setLevel(Logger.Level.INFO);

        OutputFormatter.printHeader("Library Management System");
        System.out.println("Welcome to the Library Management System!");
        System.out.println("Initializing...");

        try {
            // Ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                Logger.info("Created data directory");
            }

            // Initialize repositories
            UserRepository userRepository = new JsonUserRepository();
            MediaRepository mediaRepository = new JsonMediaRepository();
            LoanRepository loanRepository = new JsonLoanRepository();
            FineRepository fineRepository = new JsonFineRepository();
            ReservationRepository reservationRepository = new JsonReservationRepository();

            Logger.info("Repositories initialized");

            // Initialize services
            UserService userService = new UserService(userRepository);
            MediaService mediaService = new MediaService(mediaRepository);
            LoanService loanService = new LoanService(loanRepository, mediaRepository, userRepository);
            FineService fineService = new FineService(loanService, fineRepository);
            ReservationService reservationService = new ReservationService(reservationRepository, mediaRepository, userRepository);

            Logger.info("Services initialized");

            // Initialize UI
            InputHandler inputHandler = new InputHandler();
            MainMenu mainMenu = new MainMenu(
                    inputHandler,
                    userService,
                    mediaService,
                    loanService,
                    reservationService,
                    fineService
            );

            Logger.info("UI initialized");

            // Run scheduled tasks
            Logger.info("Running scheduled tasks...");
            reservationService.checkExpiredReservations();

            System.out.println("System ready!");
            System.out.println();

            // Display main menu
            mainMenu.display();

            // Cleanup
            System.out.println("\nShutting down Library Management System...");
            inputHandler.close();
            Logger.info("System shutdown complete");

        } catch (Exception e) {
            System.err.println("FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
            Logger.error("Fatal error during system initialization", e);
            System.exit(1);
        }
    }
}
