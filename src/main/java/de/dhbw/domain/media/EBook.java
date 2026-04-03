package de.dhbw.domain.media;

public class EBook extends WrittenMedia {
    private String fileFormat;

    public EBook() {
        super();
    }

    public EBook(String mediaId, String title, String author, String publisher) {
        super(mediaId, title, author, publisher);
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.EBOOK;
    }

    @Override
    public String toString() {
        return "EBook{" +
                "mediaId='" + mediaId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", pages=" + pages +
                ", format='" + fileFormat + '\'' +
                ", status=" + status +
                '}';
    }
}
