package de.dhbw.domain.media;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Media {

    protected UUID mediaId;
    protected String title;
    protected String author;
    protected String publisher;
    protected LocalDate publicationDate;
    protected String isbn;
    protected String category;
    protected MediaStatus status;
    protected String location;
    protected String description;
    protected double replacementCost;
    protected String currentBorrowerId;
    protected LocalDate borrowDate;
    protected LocalDate dueDate;

    public Media() {
        this.status = MediaStatus.AVAILABLE;
    }

    public Media(UUID mediaId, String title, String author, String publisher) {
        this();
        this.mediaId = mediaId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(MediaStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(double replacementCost) {
        this.replacementCost = replacementCost;
    }

    public String getCurrentBorrowerId() {
        return currentBorrowerId;
    }

    public void setCurrentBorrowerId(String currentBorrowerId) {
        this.currentBorrowerId = currentBorrowerId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isAvailable() {
        return status == MediaStatus.AVAILABLE;
    }

    public boolean isBorrowed() {
        return status == MediaStatus.BORROWED;
    }

    /**
     * Checks whether the reserved.
     */
    public boolean isReserved() {
        return status == MediaStatus.RESERVED;
    }

    /**
     * Checks whether the overdue.
     */
    public boolean isOverdue() {
        return (
            isBorrowed() && dueDate != null && LocalDate.now().isAfter(dueDate)
        );
    }

    /**
     * Returns the days overdue.
     */
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return LocalDate.now().toEpochDay() - dueDate.toEpochDay();
    }

    public abstract MediaType getMediaType();

    @Override
    /**
     * Executes the to string operation.
     */
    public String toString() {
        return (
            "Media{" +
            "mediaId='" +
            mediaId +
            '\'' +
            ", title='" +
            title +
            '\'' +
            ", author='" +
            author +
            '\'' +
            ", status=" +
            status +
            ", type=" +
            getMediaType() +
            '}'
        );
    }
}
