package de.dhbw.ui.menus;

import de.dhbw.application.media.MediaService;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.ui.InputHandler;
import de.dhbw.ui.Menu;
import de.dhbw.ui.OutputFormatter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.dhbw.util.UUID;

public class MediaMenu extends Menu {
    private final MediaService mediaService;

    public MediaMenu(String title, InputHandler inputHandler, MediaService mediaService) {        super(title, inputHandler);
        this.mediaService = mediaService;

        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("Add New Book", this::addBook);
        addMenuItem("Add New Blu-ray", this::addBluray);
        addMenuItem("Add New DVD", this::addDVD);
        addMenuItem("Add New CD", this::addCD);
        addMenuItem("Search Media", this::searchMedia);
        addMenuItem("List All Media", this::listAllMedia);
        addMenuItem("List Available Media", this::listAvailableMedia);
        addMenuItem("Update Media", this::updateMedia);
        addMenuItem("Delete Media", this::deleteMedia);
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

    private void addBluray() {
        OutputFormatter.printHeader("Add New Blu-ray");

        String title = inputHandler.readNonEmptyString("Title: ");
        String director = inputHandler.readNonEmptyString("Director: ");
        String publisher = inputHandler.readString("Publisher: ");
        int duration = inputHandler.readInt("Duration (minutes): ", 1, 1000);
        String genre = inputHandler.readString("Genre: ");
        String ageRating = inputHandler.readString("Age Rating: ");
        String resolution = inputHandler.readString("Resolution: ");

        try {
            Media dvd = mediaService.createBluRay(
                    title,
                    director,
                    publisher,
                    duration,
                    genre,
                    ageRating,
                    resolution
            );
            OutputFormatter.printSuccess("Blu-ray added successfully!");
            OutputFormatter.printMedia(dvd);
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
            Media dvd = mediaService.createDVD(
                    title,
                    director,
                    publisher,
                    duration,
                    genre,
                    ageRating
            );
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
            Media cd = mediaService.createCD(
                    title,
                    artist,
                    recordLabel,
                    duration,
                    genre,
                    trackCount
            );
            OutputFormatter.printSuccess("CD added successfully!");
            OutputFormatter.printMedia(cd);
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void searchMedia() {
        String searchString = inputHandler.readNonEmptyString("Search title or id: ");
                
        List<Media> mediaById = UUID.parseUuid(searchString)
            .map(mediaService::getMediaById)
            .orElseGet(List::of);
        
        List<Media> mediaByTitle = mediaService.searchByTitle(searchString);
        
        List<Media> mediaList = Stream.concat(mediaByTitle.stream(), mediaById.stream())
            .distinct()
            .collect(Collectors.toList());
        
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
        Optional<UUID> mediaUuid = UUID.parseUuid(mediaId);
        if (mediaUuid.isEmpty()) {
            return;
        }
        List<Media> mediaList = mediaService.getMediaById(mediaUuid.get());

        if (mediaList.isEmpty()) {
            OutputFormatter.printWarning("Media not found.");
            return;
        }

        System.out.println("Current Status: " + mediaList.getFirst().getStatus());
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
                default -> mediaList.getFirst().getStatus();
            };

            try {
                mediaService.setMediaStatus(mediaUuid.get(), newStatus);
                OutputFormatter.printSuccess("Media updated successfully.");
            } catch (Exception e) {
                OutputFormatter.printError(e.getMessage());
            }
        }
    }

    private void deleteMedia() {
        String mediaId = inputHandler.readNonEmptyString("Enter Media ID: ");
        Optional<UUID> mediaUuid = UUID.parseUuid(mediaId);
        if (mediaUuid.isEmpty()) {
            return;
        }
        boolean confirm = inputHandler.readBoolean(
                "Are you sure you want to delete this media?"
        );

        if (confirm) {
            try {
                mediaService.deleteMedia(mediaUuid.get());
                OutputFormatter.printSuccess("Media deleted successfully.");
            } catch (Exception e) {
                OutputFormatter.printError(e.getMessage());
            }
        }
    }
}
