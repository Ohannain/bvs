package de.dhbw.domain.media;

public class Book extends Media {
    private int pages;
    private String genre;
    private String language;
    private String edition;
    private boolean isDigital;

    public Book() {
        super();
    }

    public Book(String mediaId, String title, String author, String publisher) {
        super(mediaId, title, author, publisher);
    }

    // Getters and Setters
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

    public boolean isDigital() {
        return isDigital;
    }

    public void setDigital(boolean digital) {
        isDigital = digital;
    }

}
