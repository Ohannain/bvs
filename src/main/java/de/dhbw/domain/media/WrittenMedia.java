package de.dhbw.domain.media;

import java.util.UUID;

public abstract class WrittenMedia extends Media {

    protected int pages;
    protected String genre;
    protected String language;
    protected String edition;

    public WrittenMedia() {
        super();
    }

    public WrittenMedia(
        UUID mediaId,
        String title,
        String author,
        String publisher
    ) {
        super(mediaId, title, author, publisher);
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }
}
