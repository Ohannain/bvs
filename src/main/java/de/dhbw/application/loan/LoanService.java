    package de.dhbw.application.loan;

    import de.dhbw.domain.loan.Loan;
    import de.dhbw.domain.loan.LoanStatus;
    import de.dhbw.domain.media.Media;
    import de.dhbw.domain.media.MediaStatus;
    import de.dhbw.domain.user.User;
    import de.dhbw.persistence.loan.LoanRepository;
    import de.dhbw.persistence.media.MediaRepository;
    import de.dhbw.persistence.user.UserRepository;
    import de.dhbw.util.Config;
    import de.dhbw.util.Logger;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.LinkedHashSet;
    import java.util.List;
    import java.util.Optional;
    import de.dhbw.util.UUID;

    public class LoanService {

        private final LoanRepository loanRepository;
        private final MediaRepository mediaRepository;
        private final UserRepository userRepository;

        public LoanService(
            LoanRepository loanRepository,
            MediaRepository mediaRepository,
            UserRepository userRepository
        ) {
            this.loanRepository = loanRepository;
            this.mediaRepository = mediaRepository;
            this.userRepository = userRepository;
        }

        public Loan borrowMedia(UUID userId, List<UUID> mediaIds) {
            if (mediaIds == null || mediaIds.isEmpty()) {
                throw new IllegalArgumentException("At least one media ID is required");
            }

            List<User> userList = userRepository.findById(userId);
            if (userList.isEmpty()) {
                throw new IllegalArgumentException("User not found: " + userId);
            }

            User user = userList.getFirst();
            if (!user.canBorrow()) {
                throw new IllegalStateException("User is not allowed to borrow media right now");
            }

            List<UUID> uniqueMediaIds = new ArrayList<>(new LinkedHashSet<>(mediaIds));
            if (user.getBorrowedMediaIds().size() + uniqueMediaIds.size() > user.getMaxBorrowLimit()) {
                throw new IllegalStateException("Borrow limit exceeded for user " + userId);
            }

            List<Media> mediaToBorrow = new ArrayList<>();
            for (UUID mediaId : uniqueMediaIds) {
                List<Media> mediaList = mediaRepository.findById(mediaId);
                if (mediaList.isEmpty()) {
                    throw new IllegalArgumentException("Media not found: " + mediaId);
                }

                Media media = mediaList.getFirst();
                if (media.getStatus() != MediaStatus.AVAILABLE) {
                    throw new IllegalStateException("Media is not available: " + mediaId);
                }
                mediaToBorrow.add(media);
            }

            Loan loan = new Loan(userId);
            loan.addMedia(uniqueMediaIds.toArray(UUID[]::new));

            int loanDays = mediaToBorrow
                .stream()
                .map(Media::getMediaType)
                .mapToInt(type -> type != null ? type.getDefaultLoanDays() : Config.DEFAULT_BOOK_LOAN_DAYS)
                .min()
                .orElse(Config.DEFAULT_BOOK_LOAN_DAYS);
            LocalDate dueDate = LocalDate.now().plusDays(loanDays);
            loan.setDueDate(dueDate);

            loanRepository.save(loan);

            for (Media media : mediaToBorrow) {
                media.setStatus(MediaStatus.BORROWED);
                media.setCurrentBorrowerId(userId.toString());
                media.setBorrowDate(loan.getIssueDate());
                media.setDueDate(dueDate);
                mediaRepository.update(media);

                user.addBorrowedMedia(media.getMediaId());
            }
            userRepository.update(user);

            Logger.info("Created loan " + loan.getLoanId() + " for user " + userId + " with " + uniqueMediaIds.size() + " media item(s)");
            return loan;
        }

        public void returnMedia(UUID loanId, List<UUID> mediaIds) {
            Optional<Loan> loanOptional = loanRepository.findById(loanId);
            if (loanOptional.isEmpty()) {
                Logger.warn("No loan found with id " + loanId);
            }

            Loan loan = loanOptional.get();
            List<UUID> currentLoanedMediaIds = loan.getMediaIds();

            for (UUID mediaId : mediaIds) {
                currentLoanedMediaIds.remove(mediaId);
                List<Media> mediaList = mediaRepository.findById(mediaId);
                if (!mediaList.isEmpty()) {
                    Media media = mediaList.getFirst();
                    media.setStatus(MediaStatus.AVAILABLE);
                    media.setCurrentBorrowerId(null);
                    media.setBorrowDate(null);
                    media.setDueDate(null);
                    mediaRepository.update(media);
                }
                Logger.info("Returned media with id " + mediaId);
            }

            if (currentLoanedMediaIds.isEmpty()) {
                loan.setReturnDate(LocalDate.now());
                loan.setStatus(LoanStatus.RETURNED);
            }

            loanRepository.update(loan);
        }

        public void renewLoan(UUID loanId, LocalDate newDate) {
            Optional<Loan> loanOptional = loanRepository.findById(loanId);
            if (loanOptional.isEmpty()) {
                Logger.warn("No loan found with id " + loanId);
            }

            Loan loan = loanOptional.get();
            UUID[] mediaIds = loan.getMediaIds().toArray(UUID[]::new);
            for (UUID mediaId : mediaIds) {
                List<Media> mediaList = mediaRepository.findById(mediaId);
                if (mediaList.isEmpty()) {
                    Logger.warn("No media found with id " + mediaId);
                    continue;
                }

                Media media = mediaList.getFirst();
                media.setDueDate(newDate);
                mediaRepository.update(media);
            }

            loan.setDueDate(newDate);
            loanRepository.update(loan);
            Logger.info("Renewed loan with id " + loanId);
        }

        /**
         * getAllLoans returns a list of all loans
         * @return {List<Loan>} - a list of all loans
         */
        public List<Loan> getAllLoans() {
            return loanRepository.findAll();
        }

        /**
         * getLoanById looks for a given id
         * @param loanId - the id of the loan
         * @return {Optional<Loan>} - the loan if found
         */
        public Optional<Loan> getLoanById(UUID loanId) {
            return loanRepository.findById(loanId);
        }

        /**
         * getLoansByUserId gets all loans associated with a user
         * @param userId - the id of the user
         * @return {List<Loan>} - the loans associated with the user
         */
        public List<Loan> getLoansByUserId(UUID userId) {
            return loanRepository.findByUserId(userId);
        }

        /**
         * getLoansByStatus gets all loans with a certain status
         * @param status - the status of the loans
         * @return {List<Loan>} - a list of loans found for the status
         */
        public List<Loan> getLoansByStatus(LoanStatus status) {
            return loanRepository.findByStatus(status);
        }
    }
