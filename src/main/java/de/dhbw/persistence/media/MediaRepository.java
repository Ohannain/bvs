package de.dhbw.persistence.media;

import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.media.MediaType;
import java.util.List;
import java.util.Optional;

public interface MediaRepository {
    void save(Media media);
    Optional<Media> findById(String mediaId);
    List<Media> findAll();
    List<Media> findByTitle(String title);
    List<Media> findByAuthor(String author);
    List<Media> findByType(MediaType type);
    List<Media> findByStatus(MediaStatus status);
    void update(Media media);
    void delete(String mediaId);
    boolean exists(String mediaId);
}
