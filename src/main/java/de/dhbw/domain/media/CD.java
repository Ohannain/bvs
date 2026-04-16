package de.dhbw.domain.media;

import de.dhbw.util.UUID;

public class CD extends Media {
    private int durationMinutes;
    private String artist;
    private String genre;
    private String recordLabel;
    private int trackCount;
    private String albumType;

    public CD() {
        super();
    }

    public CD(UUID mediaId, String title, String artist, String recordLabel) {
        super(mediaId, title, artist, recordLabel);
        this.artist = artist;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
        this.author = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRecordLabel() {
        return recordLabel;
    }

    public void setRecordLabel(String recordLabel) {
        this.recordLabel = recordLabel;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.CD;
    }

    @Override
    /**
     * Executes the to string operation.
     */
    public String toString() {
        return "CD{" +
                "mediaId='" + mediaId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", tracks=" + trackCount +
                ", status=" + status +
                '}';
    }
}
