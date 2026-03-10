package de.dhbw.domain.media;

public class DVD extends Media {
    private int durationMinutes;
    private String director;
    private String genre;
    private String ageRating;
    private String language;
    private String subtitles;

    public DVD() {
        super();
    }

    public DVD(String mediaId, String title, String director, String publisher) {
        super(mediaId, title, director, publisher);
        this.director = director;
    }


    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
        this.author = director; 
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(String subtitles) {
        this.subtitles = subtitles;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.DVD;
    }

    @Override
    public String toString() {
        return "DVD{" +
                "mediaId='" + mediaId + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + durationMinutes + " min" +
                ", status=" + status +
                '}';
    }
}
