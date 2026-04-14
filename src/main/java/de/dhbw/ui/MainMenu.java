package de.dhbw.ui;

import de.dhbw.application.media.MediaService;
import de.dhbw.application.user.UserService;
import de.dhbw.application.user.UserValidator;
import de.dhbw.domain.media.*;
import de.dhbw.domain.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MainMenu extends Menu {
    private final UserService userService;
    private final MediaService mediaService;

    public MainMenu(InputHandler inputHandler,
                   UserService userService,
                   MediaService mediaService) {
        super("Library Management System - Main Menu", inputHandler);
        this.userService = userService;
        this.mediaService = mediaService;
        
        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("User Management", this::showUserMenu);
        addMenuItem("Media Management", this::showMediaMenu);
    }

    @Override
    protected boolean isMainMenu() {
        return true;
    }

    // ==================== USER MANAGEMENT ====================
    private void showUserMenu() {
        Menu userMenu = new Menu("User Management", inputHandler) {
            {
                addMenuItem("Create New User", MainMenu.this::createUser);
                addMenuItem("Search User by ID", MainMenu.this::searchUserById);
                addMenuItem("Search User by Name", MainMenu.this::searchUserByName);
                addMenuItem("List All Users", MainMenu.this::listAllUsers);
                addMenuItem("Update User", MainMenu.this::updateUser);
                addMenuItem("Suspend User", MainMenu.this::suspendUser);
                addMenuItem("Activate User", MainMenu.this::activateUser);
                addMenuItem("Delete User", MainMenu.this::deleteUser);
            }
        };
        userMenu.display();
    }

    private void createUser() {
        OutputFormatter.printHeader("Create New User");
        
        String firstName = inputHandler.readNonEmptyString("First Name: ");
        String lastName = inputHandler.readNonEmptyString("Last Name: ");
        String email = readValidEmail("Email: ");
        String phone = readOptionalValidPhone("Phone (optional): ");
        String address = inputHandler.readString("Address (optional): ");
        
        try {
            User user = userService.createUser(firstName, lastName, email, phone, address);
            OutputFormatter.printSuccess("User created successfully!");
            OutputFormatter.printUser(user);
        } catch (Exception e) {
            OutputFormatter.printError("Failed to create user: " + e.getMessage());
        }
    }

    private void searchUserById() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<User> user = userService.getUserById(UUID.fromString(userId));
        
        if (user.isPresent()) {
            OutputFormatter.printUser(user.get());
        } else {
            OutputFormatter.printWarning("User not found.");
        }
    }

    private void searchUserByName() {
        String name = inputHandler.readNonEmptyString("Enter Name (or part of name): ");
        List<User> users = userService.searchUsersByName(name);
        OutputFormatter.printUserList(users);
    }

    private void listAllUsers() {
        List<User> users = userService.getAllUsers();
        OutputFormatter.printUserList(users);
    }

    private void updateUser() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<User> userOpt = userService.getUserById(UUID.fromString(userId));
        
        if (userOpt.isEmpty()) {
            OutputFormatter.printWarning("User not found.");
            return;
        }
        
        User user = userOpt.get();
        System.out.println("Leave blank to keep current value");
        
        String firstName = inputHandler.readString("First Name [" + user.getFirstName() + "]: ");
        if (!firstName.isEmpty()) user.setFirstName(firstName);
        
        String lastName = inputHandler.readString("Last Name [" + user.getLastName() + "]: ");
        if (!lastName.isEmpty()) user.setLastName(lastName);
        
        String email = inputHandler.readString("Email [" + user.getEmail() + "]: ");
        if (!email.isEmpty()) {
            while (!UserValidator.isValidEmail(email)) {
                OutputFormatter.printWarning("Invalid email format. Please enter a valid email.");
                email = inputHandler.readString("Email [" + user.getEmail() + "]: ");
                if (email.isEmpty()) {
                    break;
                }
            }
            if (!email.isEmpty()) {
                user.setEmail(email);
            }
        }
        
        try {
            userService.updateUser(user);
            OutputFormatter.printSuccess("User updated successfully!");
        } catch (Exception e) {
            OutputFormatter.printError("Failed to update user: " + e.getMessage());
        }
    }

    private String readValidEmail(String prompt) {
        String email;
        do {
            email = inputHandler.readNonEmptyString(prompt);
            if (!UserValidator.isValidEmail(email)) {
                OutputFormatter.printWarning("Invalid email format. Please enter a valid email.");
            }
        } while (!UserValidator.isValidEmail(email));
        return email;
    }

    private String readOptionalValidPhone(String prompt) {
        String phone;
        do {
            phone = inputHandler.readString(prompt);
            if (!UserValidator.isValidPhone(phone)) {
                OutputFormatter.printWarning("Invalid phone number format. Use 10-15 digits (optional leading +).");
            }
        } while (!UserValidator.isValidPhone(phone));
        return phone;
    }

    private void suspendUser() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        String reason = inputHandler.readNonEmptyString("Reason for suspension: ");
        
        try {
            userService.suspendUser(UUID.fromString(userId), reason);
            OutputFormatter.printSuccess("User suspended successfully.");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void activateUser() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        
        try {
            userService.activateUser(UUID.fromString(userId));
            OutputFormatter.printSuccess("User activated successfully.");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void deleteUser() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        boolean confirm = inputHandler.readBoolean("Are you sure you want to delete this user?");
        
        if (confirm) {
            try {
                userService.deleteUser(UUID.fromString(userId));
                OutputFormatter.printSuccess("User deleted successfully.");
            } catch (Exception e) {
                OutputFormatter.printError(e.getMessage());
            }
        }
    }

    // ==================== MEDIA MANAGEMENT ====================
    private void showMediaMenu() {
        Menu mediaMenu = new Menu("Media Management", inputHandler) {
            {
                addMenuItem("Add New Book", MainMenu.this::addBook);
                addMenuItem("Add New DVD", MainMenu.this::addDVD);
                addMenuItem("Add New CD", MainMenu.this::addCD);
                addMenuItem("Search Media by ID", MainMenu.this::searchMediaById);
                addMenuItem("Search Media by Title", MainMenu.this::searchMediaByTitle);
                addMenuItem("List All Media", MainMenu.this::listAllMedia);
                addMenuItem("List Available Media", MainMenu.this::listAvailableMedia);
                addMenuItem("Update Media", MainMenu.this::updateMedia);
                addMenuItem("Delete Media", MainMenu.this::deleteMedia);
            }
        };
        mediaMenu.display();
    }

    private void addBook() {
        OutputFormatter.printHeader("Add New Book");
        
        String title = inputHandler.readNonEmptyString("Title: ");
        String author = inputHandler.readNonEmptyString("Author: ");
        String publisher = inputHandler.readString("Publisher: ");
        String isbn = inputHandler.readString("ISBN: ");
        int pages = inputHandler.readInt("Number of Pages: ", 1, 10000);
        String genre = inputHandler.readString("Genre: ");
        
        try {
            Media book = mediaService.createBook(title, author, publisher, isbn, pages, genre);
            OutputFormatter.printSuccess("Book added successfully!");
            OutputFormatter.printMedia(book);
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void addDVD() {
        OutputFormatter.printHeader("Add New DVD");
        
        String title = inputHandler.readNonEmptyString("Title: ");
        String director = inputHandler.readNonEmptyString("Director: ");
        String publisher = inputHandler.readString("Publisher: ");
        int duration = inputHandler.readInt("Duration (minutes): ", 1, 1000);
        String genre = inputHandler.readString("Genre: ");
        String ageRating = inputHandler.readString("Age Rating: ");
        
        try {
            Media dvd = mediaService.createDVD(title, director, publisher, duration, genre, ageRating);
            OutputFormatter.printSuccess("DVD added successfully!");
            OutputFormatter.printMedia(dvd);
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void addCD() {
        OutputFormatter.printHeader("Add New CD");
        
        String title = inputHandler.readNonEmptyString("Title: ");
        String artist = inputHandler.readNonEmptyString("Artist: ");
        String recordLabel = inputHandler.readString("Record Label: ");
        int duration = inputHandler.readInt("Duration (minutes): ", 1, 500);
        String genre = inputHandler.readString("Genre: ");
        int trackCount = inputHandler.readInt("Track Count: ", 1, 100);
        
        try {
            Media cd = mediaService.createCD(title, artist, recordLabel, duration, genre, trackCount);
            OutputFormatter.printSuccess("CD added successfully!");
            OutputFormatter.printMedia(cd);
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void searchMediaById() {
        String mediaId = inputHandler.readNonEmptyString("Enter Media ID: ");
        Optional<Media> media = mediaService.getMediaById(UUID.fromString(mediaId));
        
        if (media.isPresent()) {
            OutputFormatter.printMedia(media.get());
        } else {
            OutputFormatter.printWarning("Media not found.");
        }
    }

    private void searchMediaByTitle() {
        String title = inputHandler.readNonEmptyString("Enter Title (or part of title): ");
        List<Media> mediaList = mediaService.searchByTitle(title);
        OutputFormatter.printMediaList(mediaList);
    }

    private void listAllMedia() {
        List<Media> mediaList = mediaService.getAllMedia();
        OutputFormatter.printMediaList(mediaList);
    }

    private void listAvailableMedia() {
        List<Media> mediaList = mediaService.getAvailableMedia();
        OutputFormatter.printMediaList(mediaList);
    }

    private void updateMedia() {
        String mediaId = inputHandler.readNonEmptyString("Enter Media ID: ");
        Optional<Media> mediaOpt = mediaService.getMediaById(UUID.fromString(mediaId));
        
        if (mediaOpt.isEmpty()) {
            OutputFormatter.printWarning("Media not found.");
            return;
        }
        
        Media media = mediaOpt.get();
        System.out.println("Current Status: " + media.getStatus());
        System.out.println("1. AVAILABLE");
        System.out.println("2. DAMAGED");
        System.out.println("3. LOST");
        System.out.println("4. IN_REPAIR");
        System.out.println("5. ARCHIVED");
        
        int choice = inputHandler.readInt("Select new status (0 to cancel): ", 0, 5);
        
        if (choice > 0) {
            MediaStatus newStatus = switch (choice) {
                case 1 -> MediaStatus.AVAILABLE;
                case 2 -> MediaStatus.DAMAGED;
                case 3 -> MediaStatus.LOST;
                case 4 -> MediaStatus.IN_REPAIR;
                case 5 -> MediaStatus.ARCHIVED;
                default -> media.getStatus();
            };
            
            try {
                mediaService.setMediaStatus(UUID.fromString(mediaId), newStatus);
                OutputFormatter.printSuccess("Media updated successfully.");
            } catch (Exception e) {
                OutputFormatter.printError(e.getMessage());
            }
        }
    }

    private void deleteMedia() {
        String mediaId = inputHandler.readNonEmptyString("Enter Media ID: ");
        boolean confirm = inputHandler.readBoolean("Are you sure you want to delete this media?");
        
        if (confirm) {
            try {
                mediaService.deleteMedia(UUID.fromString(mediaId));
                OutputFormatter.printSuccess("Media deleted successfully.");
            } catch (Exception e) {
                OutputFormatter.printError(e.getMessage());
            }
        }
    }
}
