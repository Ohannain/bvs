package de.dhbw.domain.media;

import de.dhbw.util.UUID;

public class DVD extends Film {

    public DVD() {
        super();
    }

    public DVD(UUID mediaId, String title, String director, String publisher) {
        super(mediaId, title, director, publisher);
        this.director = director;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.DVD;
    }

    @Override
    /**
     * Executes the to string operation.
     */
    public String toString() {
        return (
            "DVD{" +
            "mediaId='" +
            mediaId +
            '\'' +
            ", title='" +
            title +
            '\'' +
            ", director='" +
            director +
            '\'' +
            ", genre='" +
            genre +
            '\'' +
            ", duration=" +
            durationMinutes +
            " min" +
            ", status=" +
            status +
            '}'
        );
    }
}
