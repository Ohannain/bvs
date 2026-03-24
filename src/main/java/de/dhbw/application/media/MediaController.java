package de.dhbw.application.media;

import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.media.MediaType;

import java.util.List;
import java.util.Optional;

public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public Media addBook(String title, String author, String publisher, String isbn, int pages, String genre) {
        return mediaService.createBook(title, author, publisher, isbn, pages, genre);
    }

    public Media addDVD(String title, String director, String publisher, int duration, String genre, String ageRating) {
        return mediaService.createDVD(title, director, publisher, duration, genre, ageRating);
    }

    public Media addBluRay(String title, String director, String publisher, int duration, String genre, String ageRating, String resolution) {
        return mediaService.createBluRay(title, director, publisher, duration, genre, ageRating, resolution);
    }

    public Media addEBook(String title, String author, String publisher, String isbn, int pages, String genre, String fileFormat) {
        return mediaService.createEBook(title, author, publisher, isbn, pages, genre, fileFormat);
    }

    public Media addCD(String title, String artist, String recordLabel, int duration, String genre, int trackCount) {
        return mediaService.createCD(title, artist, recordLabel, duration, genre, trackCount);
    }

    public Optional<Media> findMedia(String mediaId) {
        return mediaService.getMediaById(mediaId);
    }

    public List<Media> searchMedia(String searchTerm) {
        return mediaService.searchMedia(searchTerm);
    }

    public List<Media> listAvailableMedia() {
        return mediaService.getAvailableMedia();
    }

    public List<Media> listMediaByType(MediaType type) {
        return mediaService.getMediaByType(type);
    }

    public void updateMediaStatus(String mediaId, MediaStatus status) {
        mediaService.setMediaStatus(mediaId, status);
    }

    public void updateMedia(Media media) {
        mediaService.updateMedia(media);
    }

    public void deleteMedia(String mediaId) {
        mediaService.deleteMedia(mediaId);
    }

    public boolean checkMediaAvailability(String mediaId) {
        return mediaService.isMediaAvailable(mediaId);
    }
}
