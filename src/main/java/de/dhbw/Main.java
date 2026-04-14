package de.dhbw;

import de.dhbw.application.media.MediaService;
import de.dhbw.application.user.UserService;
import de.dhbw.persistence.media.JsonMediaRepository;
import de.dhbw.persistence.media.MediaRepository;
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
        try {
            configureUtf8Streams();
            runApplication();

        } catch (Exception e) {
            System.err.println("FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
            Logger.error("Fatal error during system initialization", e);
            System.exit(1);
        }
    }

    private static void configureUtf8Streams() {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            // Continue with platform default encoding if UTF-8 setup fails.
        }
    }

    private static void runApplication() {
        Logger.setConsoleOutput(true);
        Logger.setLevel(Logger.Level.INFO);

        OutputFormatter.printHeader("Library Management System");
        System.out.println("Welcome to the Library Management System!");
        System.out.println("Initializing...");

        ensureDataDirectory();

        UserRepository userRepository = new JsonUserRepository();
        MediaRepository mediaRepository = new JsonMediaRepository();
        Logger.info("Repositories initialized");

        UserService userService = new UserService(userRepository);
        MediaService mediaService = new MediaService(mediaRepository);
        Logger.info("Services initialized");

        try (InputHandler inputHandler = new InputHandler()) {
            MainMenu mainMenu = new MainMenu(inputHandler, userService, mediaService);
            Logger.info("UI initialized");

            System.out.println("System ready!");
            System.out.println();
            mainMenu.display();
        } finally {
            System.out.println("\nShutting down Library Management System...");
            Logger.info("System shutdown complete");
        }
    }

    private static void ensureDataDirectory() {
        File dataDir = new File("data");
        if (!dataDir.exists() && dataDir.mkdirs()) {
            Logger.info("Created data directory");
        }
    }
}