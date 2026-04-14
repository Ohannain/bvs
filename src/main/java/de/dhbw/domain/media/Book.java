package de.dhbw.domain.media;

import java.util.UUID;

public class Book extends WrittenMedia {

    private boolean isDigital;

    public Book() {
        super();
    }

    public Book(UUID mediaId, String title, String author, String publisher) {
        super(mediaId, title, author, publisher);
    }

    public boolean isDigital() {
        return isDigital;
    }

    public void setDigital(boolean digital) {
        isDigital = digital;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.BOOK;
    }

    @Override
    public String toString() {
        return (
            "Book{" +
            "mediaId='" +
            mediaId +
            '\'' +
            ", title='" +
            title +
            '\'' +
            ", author='" +
            author +
            '\'' +
            ", genre='" +
            genre +
            '\'' +
            ", pages=" +
            pages +
            ", status=" +
            status +
            '}'
        );
    }
}
