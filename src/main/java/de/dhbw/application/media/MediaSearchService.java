package de.dhbw.application.media;

import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.media.MediaType;
import de.dhbw.persistence.media.MediaRepository;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MediaSearchService {
    private final MediaRepository mediaRepository;

    public MediaSearchService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<Media> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        String searchTerm = keyword.trim().toLowerCase(Locale.ROOT);
        return mediaRepository.findAll().stream()
                .filter(m -> matchesKeyword(m, searchTerm))
                .collect(Collectors.toList());
    }

    public List<Media> searchByType(MediaType type) {
        return mediaRepository.findByType(type);
    }

    public List<Media> searchByStatus(MediaStatus status) {
        return mediaRepository.findByStatus(status);
    }

    public List<Media> searchByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return List.of();
        }
        
        String searchTerm = category.trim().toLowerCase(Locale.ROOT);
        return mediaRepository.findAll().stream()
                .filter(m -> m.getCategory() != null && 
                           m.getCategory().toLowerCase(Locale.ROOT).contains(searchTerm))
                .collect(Collectors.toList());
    }

    public List<Media> searchAvailable() {
        return mediaRepository.findByStatus(MediaStatus.AVAILABLE);
    }

    public List<Media> advancedSearch(String title, String author, MediaType type, MediaStatus status) {
        List<Media> results = mediaRepository.findAll();
        
        if (title != null && !title.trim().isEmpty()) {
            String titleSearch = title.trim().toLowerCase(Locale.ROOT);
            results = results.stream()
                    .filter(m -> m.getTitle() != null && 
                               m.getTitle().toLowerCase(Locale.ROOT).contains(titleSearch))
                    .collect(Collectors.toList());
        }
        
        if (author != null && !author.trim().isEmpty()) {
            String authorSearch = author.trim().toLowerCase(Locale.ROOT);
            results = results.stream()
                    .filter(m -> m.getAuthor() != null && 
                               m.getAuthor().toLowerCase(Locale.ROOT).contains(authorSearch))
                    .collect(Collectors.toList());
        }
        
        if (type != null) {
            results = results.stream()
                    .filter(m -> m.getMediaType() == type)
                    .collect(Collectors.toList());
        }
        
        if (status != null) {
            results = results.stream()
                    .filter(m -> m.getStatus() == status)
                    .collect(Collectors.toList());
        }
        
        return results;
    }

    /**
     * Executes the matches keyword operation.
     */
    private boolean matchesKeyword(Media media, String keyword) {
        return (media.getTitle() != null && media.getTitle().toLowerCase(Locale.ROOT).contains(keyword)) ||
               (media.getAuthor() != null && media.getAuthor().toLowerCase(Locale.ROOT).contains(keyword)) ||
               (media.getPublisher() != null && media.getPublisher().toLowerCase(Locale.ROOT).contains(keyword)) ||
               (media.getIsbn() != null && media.getIsbn().toLowerCase(Locale.ROOT).contains(keyword)) ||
               (media.getCategory() != null && media.getCategory().toLowerCase(Locale.ROOT).contains(keyword)) ||
               (media.getDescription() != null && media.getDescription().toLowerCase(Locale.ROOT).contains(keyword));
    }
}
