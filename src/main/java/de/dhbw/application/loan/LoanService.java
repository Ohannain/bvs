package de.dhbw.application.loan;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.persistence.loan.LoanRepository;
import de.dhbw.persistence.media.MediaRepository;
import de.dhbw.persistence.user.UserRepository;
import de.dhbw.util.Logger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public void returnMedia(UUID loanId, List<UUID> mediaIds) {
        Optional<Loan> loanOptional = loanRepository.findById(loanId);
        if (loanOptional.isEmpty()) {
            Logger.warn("No loan found with id " + loanId);
        }

        Loan loan = loanOptional.get();
        List<UUID> currentLoanedMediaIds = loan.getMediaIds();

        for (UUID mediaId : mediaIds) {
            currentLoanedMediaIds.remove(mediaId);
            Optional<Media> mediaOptional = mediaRepository.findById(mediaId);
            if (mediaOptional.isPresent()) {
                Media media = mediaOptional.get();
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
            Optional<Media> mediaOptional = mediaRepository.findById(mediaId);
            if (mediaOptional.isEmpty()) {
                Logger.warn("No media found with id " + mediaId);
                continue;
            }

            Media media = mediaOptional.get();
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

    /**
     * generateLoanId generates a unique id for a loan.
     *
     * @return {UUID} loanId - the generated unique id.
     */
    private UUID generateLoanId() {
        UUID loanId;
        do {
            loanId = UUID.randomUUID();
        } while (loanRepository.findById(loanId).isPresent());
        return loanId;
    }
}
