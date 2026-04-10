package de.dhbw.application.media;

import de.dhbw.domain.media.*;
import de.dhbw.persistence.media.MediaRepository;
import de.dhbw.util.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MediaService {
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media createBook(String title, String author, String publisher, String isbn, int pages, String genre) {
        String mediaId = generateMediaId("B");
        Book book = new Book(mediaId, title, author, publisher);
        book.setIsbn(isbn);
        book.setPages(pages);
        book.setGenre(genre);
        book.setStatus(MediaStatus.AVAILABLE);
        
        mediaRepository.save(book);
        Logger.info("Created new book: " + mediaId);
        return book;
    }

    public Media createDVD(String title, String director, String publisher, int duration, String genre, String ageRating) {
        String mediaId = generateMediaId("D");
        DVD dvd = new DVD(mediaId, title, director, publisher);
        dvd.setDurationMinutes(duration);
        dvd.setGenre(genre);
        dvd.setAgeRating(ageRating);
        dvd.setStatus(MediaStatus.AVAILABLE);
        
        mediaRepository.save(dvd);
        Logger.info("Created new DVD: " + mediaId);
        return dvd;
    }

    public Media createBluRay(String title, String director, String publisher, int duration, String genre, String ageRating, String resolution) {
        String mediaId = generateMediaId("BR");
        BluRay bluRay = new BluRay(mediaId, title, director, publisher);
        bluRay.setDurationMinutes(duration);
        bluRay.setGenre(genre);
        bluRay.setAgeRating(ageRating);
        bluRay.setResolution(resolution);
        bluRay.setStatus(MediaStatus.AVAILABLE);

        mediaRepository.save(bluRay);
        Logger.info("Created new BluRay: " + mediaId);
        return bluRay;
    }

    public Media createEBook(String title, String author, String publisher, String isbn, int pages, String genre, String fileFormat) {
        String mediaId = generateMediaId("EB");
        EBook eBook = new EBook(mediaId, title, author, publisher);
        eBook.setIsbn(isbn);
        eBook.setPages(pages);
        eBook.setGenre(genre);
        eBook.setFileFormat(fileFormat);
        eBook.setStatus(MediaStatus.AVAILABLE);

        mediaRepository.save(eBook);
        Logger.info("Created new EBook: " + mediaId);
        return eBook;
    }

    public Media createCD(String title, String artist, String recordLabel, int duration, String genre, int trackCount) {
        String mediaId = generateMediaId("C");
        CD cd = new CD(mediaId, title, artist, recordLabel);
        cd.setDurationMinutes(duration);
        cd.setGenre(genre);
        cd.setTrackCount(trackCount);
        cd.setStatus(MediaStatus.AVAILABLE);
        
        mediaRepository.save(cd);
        Logger.info("Created new CD: " + mediaId);
        return cd;
    }

    public Optional<Media> getMediaById(String mediaId) {
        return mediaRepository.findById(mediaId);
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public List<Media> searchByTitle(String title) {
        return mediaRepository.findByTitle(title);
    }

    public List<Media> searchByAuthor(String author) {
        return mediaRepository.findByAuthor(author);
    }

    public List<Media> getAvailableMedia() {
        return mediaRepository.findByStatus(MediaStatus.AVAILABLE);
    }

    public List<Media> getBorrowedMedia() {
        return mediaRepository.findByStatus(MediaStatus.BORROWED);
    }

    public List<Media> getMediaByType(MediaType type) {
        return mediaRepository.findByType(type);
    }

    public void updateMedia(Media media) {
        if (media == null || media.getMediaId() == null) {
            throw new IllegalArgumentException("Invalid media object");
        }
        mediaRepository.update(media);
        Logger.info("Updated media: " + media.getMediaId());
    }

    public void deleteMedia(String mediaId) {
        Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
        if (mediaOpt.isPresent()) {
            Media media = mediaOpt.get();
            if (media.getStatus() == MediaStatus.BORROWED) {
                throw new IllegalStateException("Cannot delete borrowed media");
            }
            mediaRepository.delete(mediaId);
            Logger.info("Deleted media: " + mediaId);
        } else {
            throw new IllegalArgumentException("Media not found: " + mediaId);
        }
    }

    public void setMediaStatus(String mediaId, MediaStatus status) {
        Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
        if (mediaOpt.isPresent()) {
            Media media = mediaOpt.get();
            media.setStatus(status);
            mediaRepository.update(media);
            Logger.info("Set media " + mediaId + " status to " + status);
        } else {
            throw new IllegalArgumentException("Media not found: " + mediaId);
        }
    }

    public boolean isMediaAvailable(String mediaId) {
        Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
        return mediaOpt.map(Media::isAvailable).orElse(false);
    }

    public List<Media> searchMedia(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return List.of();
        }
        List<Media> allMedia = mediaRepository.findAll();
        String search = searchTerm.trim().toLowerCase(Locale.ROOT);
        
        return allMedia.stream()
                .filter(m -> 
                    (m.getTitle() != null && m.getTitle().toLowerCase(Locale.ROOT).contains(search)) ||
                    (m.getAuthor() != null && m.getAuthor().toLowerCase(Locale.ROOT).contains(search)) ||
                    (m.getIsbn() != null && m.getIsbn().toLowerCase(Locale.ROOT).contains(search)) ||
                    (m.getCategory() != null && m.getCategory().toLowerCase(Locale.ROOT).contains(search))
                )
                .collect(Collectors.toList());
    }

    /**
     * Generates a new value.
     */
    private String generateMediaId(String prefix) {
        String mediaId;
        do {
            mediaId = prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (mediaRepository.exists(mediaId));
        return mediaId;
    }
}
