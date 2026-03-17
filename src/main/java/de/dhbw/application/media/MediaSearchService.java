package de.dhbw.application.media;

import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.media.MediaType;
import de.dhbw.persistence.media.MediaRepository;

import java.util.List;
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
        
        String searchTerm = keyword.toLowerCase();
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
        
        String searchTerm = category.toLowerCase();
        return mediaRepository.findAll().stream()
                .filter(m -> m.getCategory() != null && 
                           m.getCategory().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    public List<Media> searchAvailable() {
        return mediaRepository.findByStatus(MediaStatus.AVAILABLE);
    }

    public List<Media> advancedSearch(String title, String author, MediaType type, MediaStatus status) {
        List<Media> results = mediaRepository.findAll();
        
        if (title != null && !title.trim().isEmpty()) {
            String titleSearch = title.toLowerCase();
            results = results.stream()
                    .filter(m -> m.getTitle() != null && 
                               m.getTitle().toLowerCase().contains(titleSearch))
                    .collect(Collectors.toList());
        }
        
        if (author != null && !author.trim().isEmpty()) {
            String authorSearch = author.toLowerCase();
            results = results.stream()
                    .filter(m -> m.getAuthor() != null && 
                               m.getAuthor().toLowerCase().contains(authorSearch))
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

    private boolean matchesKeyword(Media media, String keyword) {
        return (media.getTitle() != null && media.getTitle().toLowerCase().contains(keyword)) ||
               (media.getAuthor() != null && media.getAuthor().toLowerCase().contains(keyword)) ||
               (media.getPublisher() != null && media.getPublisher().toLowerCase().contains(keyword)) ||
               (media.getIsbn() != null && media.getIsbn().toLowerCase().contains(keyword)) ||
               (media.getCategory() != null && media.getCategory().toLowerCase().contains(keyword)) ||
               (media.getDescription() != null && media.getDescription().toLowerCase().contains(keyword));
    }
}
