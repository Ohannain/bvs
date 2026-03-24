package de.dhbw.domain.media;

public class BluRay extends Film {
    private String resolution;

    public BluRay() {
        super();
    }

    public BluRay(String mediaId, String title, String director, String publisher) {
        super(mediaId, title, director, publisher);
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.BLURAY;
    }

    @Override
    public String toString() {
        return "BluRay{" +
                "mediaId='" + mediaId + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + durationMinutes + " min" +
                ", resolution='" + resolution + '\'' +
                ", status=" + status +
                '}';
    }
}
