package de.dhbw.domain.media;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    // --- Book ---

    @Test
    void bookDefaultStatusAvailable() {
        Book book = new Book();
        assertEquals(MediaStatus.AVAILABLE, book.getStatus());
    }

    @Test
    void bookConstructorSetsFields() {
        Book book = new Book("B1", "Clean Code", "Robert Martin", "Prentice Hall");
        assertEquals("B1", book.getMediaId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("Prentice Hall", book.getPublisher());
    }

    @Test
    void bookMediaType() {
        assertEquals(MediaType.BOOK, new Book().getMediaType());
    }

    @Test
    void bookIsAvailableWhenStatusAvailable() {
        Book book = new Book();
        assertTrue(book.isAvailable());
        assertFalse(book.isBorrowed());
    }

    @Test
    void bookSetBorrowedStatus() {
        Book book = new Book();
        book.setStatus(MediaStatus.BORROWED);
        assertTrue(book.isBorrowed());
        assertFalse(book.isAvailable());
    }

    @Test
    void bookPages() {
        Book book = new Book();
        book.setPages(350);
        assertEquals(350, book.getPages());
    }

    @Test
    void bookIsWrittenMedia() {
        assertTrue(new Book() instanceof WrittenMedia);
    }

    @Test
    void eBookMediaType() {
        assertEquals(MediaType.EBOOK, new EBook().getMediaType());
    }

    @Test
    void bookIsOverdueWhenDueDatePassed() {
        Book book = new Book();
        book.setStatus(MediaStatus.BORROWED);
        book.setDueDate(LocalDate.now().minusDays(2));
        assertTrue(book.isOverdue());
        assertTrue(book.getDaysOverdue() >= 2);
    }

    @Test
    void bookNotOverdueWhenDueDateFuture() {
        Book book = new Book();
        book.setStatus(MediaStatus.BORROWED);
        book.setDueDate(LocalDate.now().plusDays(5));
        assertFalse(book.isOverdue());
        assertEquals(0, book.getDaysOverdue());
    }

    // --- DVD ---

    @Test
    void dvdMediaType() {
        assertEquals(MediaType.DVD, new DVD().getMediaType());
    }

    @Test
    void dvdConstructor() {
        DVD dvd = new DVD("D1", "Inception", "Christopher Nolan", "Warner");
        assertEquals("D1", dvd.getMediaId());
        assertEquals("Inception", dvd.getTitle());
    }

    @Test
    void dvdDuration() {
        DVD dvd = new DVD();
        dvd.setDurationMinutes(148);
        assertEquals(148, dvd.getDurationMinutes());
    }

    @Test
    void dvdIsFilm() {
        assertTrue(new DVD() instanceof Film);
    }

    @Test
    void bluRayMediaType() {
        assertEquals(MediaType.BLURAY, new BluRay().getMediaType());
    }

    @Test
    void bluRayConstructor() {
        BluRay bluRay = new BluRay("BR1", "Dune", "Denis Villeneuve", "Warner");
        assertEquals("BR1", bluRay.getMediaId());
        assertEquals("Dune", bluRay.getTitle());
        assertEquals("Denis Villeneuve", bluRay.getDirector());
    }

    // --- CD ---

    @Test
    void cdMediaType() {
        assertEquals(MediaType.CD, new CD().getMediaType());
    }

    @Test
    void cdConstructor() {
        CD cd = new CD("C1", "Abbey Road", "The Beatles", "Apple Records");
        assertEquals("C1", cd.getMediaId());
        assertEquals("Abbey Road", cd.getTitle());
    }

    @Test
    void cdTrackCount() {
        CD cd = new CD();
        cd.setTrackCount(17);
        assertEquals(17, cd.getTrackCount());
    }

    // --- MediaStatus enum ---

    @Test
    void mediaStatusValues() {
        assertEquals(7, MediaStatus.values().length);
        assertNotNull(MediaStatus.AVAILABLE);
        assertNotNull(MediaStatus.BORROWED);
        assertNotNull(MediaStatus.RESERVED);
        assertNotNull(MediaStatus.DAMAGED);
        assertNotNull(MediaStatus.LOST);
    }

    // --- MediaType enum ---

    @Test
    void mediaTypeDefaultLoanDays() {
        assertEquals(30, MediaType.BOOK.getDefaultLoanDays());
        assertEquals(7,  MediaType.DVD.getDefaultLoanDays());
        assertEquals(7,  MediaType.BLURAY.getDefaultLoanDays());
        assertEquals(14, MediaType.CD.getDefaultLoanDays());
    }

    @Test
    void mediaTypeDailyFineRate() {
        assertEquals(0.50, MediaType.BOOK.getDailyFineRate(), 0.001);
        assertEquals(1.00, MediaType.DVD.getDailyFineRate(), 0.001);
        assertEquals(1.20, MediaType.BLURAY.getDailyFineRate(), 0.001);
        assertEquals(0.75, MediaType.CD.getDailyFineRate(), 0.001);
    }
}
