package de.dhbw.persistence.media;

import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.media.MediaType;
import java.util.List;
import de.dhbw.util.UUID;

public interface MediaRepository {
    void save(Media media);
    List<Media> findById(UUID mediaId);
    List<Media> findAll();
    List<Media> findByTitle(String title);
    List<Media> findByAuthor(String author);
    List<Media> findByType(MediaType type);
    List<Media> findByStatus(MediaStatus status);
    void update(Media media);
    void delete(UUID mediaId);
    boolean exists(UUID mediaId);
}
