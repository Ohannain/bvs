package de.dhbw.application.loan;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.media.MediaType;
import de.dhbw.domain.media.Book;
import de.dhbw.domain.media.DVD;
import de.dhbw.domain.user.User;
import de.dhbw.persistence.loan.LoanRepository;
import de.dhbw.persistence.media.MediaRepository;
import de.dhbw.persistence.media.MockMediaRepository;
import de.dhbw.persistence.loan.JsonLoanRepository;
import de.dhbw.persistence.user.UserRepository;
import de.dhbw.persistence.user.FakeUserRepository;
import de.dhbw.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LoanService using MockMediaRepository and FakeUserRepository.
 * Tests that LoanService correctly updates media status and user state through repository interactions.
 */
class LoanServiceIntegrationTest {

    private LoanRepository loanRepository;
    private MediaRepository mediaRepository;
    private UserRepository userRepository;
    private LoanService loanService;

    @BeforeEach
    void setup() {
        loanRepository = new JsonLoanRepository();
        mediaRepository = new MockMediaRepository();
        userRepository = new FakeUserRepository();

        loanService = new LoanService(loanRepository, mediaRepository, userRepository);
    }

    /**
     * Helper: Create a user in the repository
     */
    private User createTestUser(String firstName, String lastName, String email) {
        User user = new User(UUID.randomUUID(), firstName, lastName, email);
        user.setPhone("0123456789");
        user.setAddress("Test Street");
        userRepository.save(user);
        return user;
    }

    /**
     * Helper: Create media in the repository
     */
    private Media createTestMedia(String title, String author, MediaType type) {
        UUID mediaId = UUID.randomUUID();
        Media media;

        if (type == MediaType.BOOK) {
            media = new Book(mediaId, title, author, "Test Publisher");
        } else if (type == MediaType.DVD) {
            media = new DVD(mediaId, title, author, "Test Publisher");
        } else {
            media = new Book(mediaId, title, author, "Test Publisher");
        }

        media.setStatus(MediaStatus.AVAILABLE);
        mediaRepository.save(media);
        return media;
    }

    @Test
    void testBorrowSingleMedia_UpdatesMediaStatus() {
        User user = createTestUser("John", "Doe", "john@example.com");
        Media book = createTestMedia("The Great Book", "Author Name", MediaType.BOOK);

        MockMediaRepository mockMedia = (MockMediaRepository) mediaRepository;
        mockMedia.resetCallCounts();

        Loan loan = loanService.borrowMedia(user.getUserId(), List.of(book.getMediaId()));

        assertNotNull(loan);
        assertEquals(user.getUserId(), loan.getUserId());

        List<Media> borrowedMediaList = mediaRepository.findById(book.getMediaId());
        assertEquals(1, borrowedMediaList.size());
        Media borrowedMedia = borrowedMediaList.getFirst();
        assertEquals(MediaStatus.BORROWED, borrowedMedia.getStatus());
        assertEquals(user.getUserId().toString(), borrowedMedia.getCurrentBorrowerId());

        assertEquals(1, mockMedia.getUpdateCount());
    }

    @Test
    void testBorrowMultipleMedia_VerifiesSideEffects() {
        User user = createTestUser("Jane", "Smith", "jane@example.com");
        Media book1 = createTestMedia("Book 1", "Author 1", MediaType.BOOK);
        Media book2 = createTestMedia("Book 2", "Author 2", MediaType.BOOK);
        Media dvd = createTestMedia("Movie", "Director", MediaType.DVD);

        MockMediaRepository mockMedia = (MockMediaRepository) mediaRepository;
        mockMedia.resetCallCounts();
        int updateCountBefore = mockMedia.getUpdateCount();

        Loan loan = loanService.borrowMedia(user.getUserId(),
            List.of(book1.getMediaId(), book2.getMediaId(), dvd.getMediaId()));

        for (UUID mediaId : List.of(book1.getMediaId(), book2.getMediaId(), dvd.getMediaId())) {
            List<Media> mediaList = mediaRepository.findById(mediaId);
            assertEquals(MediaStatus.BORROWED, mediaList.getFirst().getStatus());
        }

        int expectedUpdates = updateCountBefore + 3;
        assertEquals(expectedUpdates, mockMedia.getUpdateCount());

        assertTrue(mockMedia.getCallCount("update") >= 3);
    }

    @Test
    void testBorrowMedia_UserBorrowedListIsUpdated() {
        User user = createTestUser("Bob", "Johnson", "bob@example.com");
        Media media = createTestMedia("Test Book", "Test Author", MediaType.BOOK);

        loanService.borrowMedia(user.getUserId(), List.of(media.getMediaId()));

        List<User> updatedUsers = userRepository.findById(user.getUserId());
        assertTrue(updatedUsers.getFirst().getBorrowedMediaIds().contains(media.getMediaId()));
    }

    @Test
    void testBorrowMedia_UnavailableMediaThrowsException() {
        User user = createTestUser("Test", "User", "test@example.com");
        Media media = createTestMedia("Unavailable Book", "Author", MediaType.BOOK);

        media.setStatus(MediaStatus.RESERVED);
        mediaRepository.update(media);

        assertThrows(IllegalStateException.class, () -> {
            loanService.borrowMedia(user.getUserId(), List.of(media.getMediaId()));
        });
    }

    @Test
    void testBorrowMedia_UserNotFoundThrowsException() {
        Media media = createTestMedia("Some Book", "Some Author", MediaType.BOOK);
        UUID nonExistentUserId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> {
            loanService.borrowMedia(nonExistentUserId, List.of(media.getMediaId()));
        });
    }

    @Test
    void testBorrowMedia_MediaNotFoundThrowsException() {
        User user = createTestUser("Test", "User", "test@example.com");
        UUID nonExistentMediaId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> {
            loanService.borrowMedia(user.getUserId(), List.of(nonExistentMediaId));
        });
    }

    @Test
    void testBorrowMedia_EmptyMediaListThrowsException() {
        User user = createTestUser("Test", "User", "test@example.com");

        assertThrows(IllegalArgumentException.class, () -> {
            loanService.borrowMedia(user.getUserId(), List.of());
        });
    }

    @Test
    void testBorrowMedia_ExceedsBorrowLimit() {
        User user = createTestUser("Limited", "Borrower", "limited@example.com");

        List<UUID> mediaIds = List.of(
            createTestMedia("Book 1", "Author 1", MediaType.BOOK).getMediaId(),
            createTestMedia("Book 2", "Author 2", MediaType.BOOK).getMediaId(),
            createTestMedia("Book 3", "Author 3", MediaType.BOOK).getMediaId(),
            createTestMedia("Book 4", "Author 4", MediaType.BOOK).getMediaId(),
            createTestMedia("Book 5", "Author 5", MediaType.BOOK).getMediaId(),
            createTestMedia("Book 6", "Author 6", MediaType.BOOK).getMediaId()
        );

        assertThrows(IllegalStateException.class, () -> {
            loanService.borrowMedia(user.getUserId(), mediaIds);
        });
    }

    @Test
    void testMockRepositoryCallCounting() {
        User user = createTestUser("Call", "Counter", "call@example.com");
        Media book = createTestMedia("Test Book", "Author", MediaType.BOOK);

        MockMediaRepository mockMedia = (MockMediaRepository) mediaRepository;
        mockMedia.resetCallCounts();

        loanService.borrowMedia(user.getUserId(), List.of(book.getMediaId()));

        assertTrue(mockMedia.getCallCount("findById") >= 1);
        assertEquals(1, mockMedia.getCallCount("update"));
        assertEquals(0, mockMedia.getCallCount("delete"));

        int totalCalls = mockMedia.getTotalCallCount();
        assertTrue(totalCalls > 0);
    }

    @Test
    void testCleanupBetweenTests() {
        User user1 = createTestUser("User", "One", "user1@example.com");
        Media media1 = createTestMedia("Media 1", "Author 1", MediaType.BOOK);

        MockMediaRepository mockMedia = (MockMediaRepository) mediaRepository;
        FakeUserRepository fakeUser = (FakeUserRepository) userRepository;

        assertEquals(1, mockMedia.size());
        assertEquals(1, fakeUser.size());

        mockMedia.clear();
        fakeUser.clear();

        assertEquals(0, mockMedia.size());
        assertEquals(0, fakeUser.size());
        assertEquals(0, mockMedia.getTotalCallCount());
    }

    @Test
    void testDueDate_IsCalculatedBasedOnMediaType() {
        User user = createTestUser("Date", "Test", "date@example.com");

        Media book = createTestMedia("Book", "Author", MediaType.BOOK);

        Loan loan = loanService.borrowMedia(user.getUserId(), List.of(book.getMediaId()));

        assertNotNull(loan.getDueDate());
        LocalDate expectedDueDate = LocalDate.now().plusDays(MediaType.BOOK.getDefaultLoanDays());
        assertEquals(expectedDueDate, loan.getDueDate());
    }
}
