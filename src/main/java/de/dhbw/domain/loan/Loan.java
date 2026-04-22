package de.dhbw.domain.loan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import de.dhbw.util.UUID;

public class Loan {

    /** the id of a loan */
    private final UUID loanId;
    /** the ids of the media loaned */
    private List<UUID> mediaIds;
    /** the id of the user the media was loaned to */
    private UUID userId;
    /** the date the loan was issued */
    private final LocalDate issueDate;
    /** the date the loan is due */
    private LocalDate dueDate;
    /** the date the media was returned */
    private LocalDate returnDate;
    /** the status of the loan */
    private LoanStatus status;
    /** notes concerning the loan */
    private String note;

    /** Creates a new loan.
     *
     * Every loan has an issueDate, is assigned the status ACTIVE and needs a user assigned to it.
     */
    public Loan(UUID userId) {
        this(userId, LocalDate.now());
    }

    /** Creates a new loan with a specific issue date. */
    public Loan(UUID userId, LocalDate issueDate) {
        this.loanId = UUID.nextLoanId();
        this.issueDate = issueDate;
        this.status = LoanStatus.ACTIVE;
        this.userId = userId;
    }

    /** getLoanId gets the id of a loan */
    public UUID getLoanId() {
        return this.loanId;
    }

    /** getMediaIds gets the ids of the media loaned */
    public List<UUID> getMediaIds() {
        if (this.mediaIds == null) {
            this.mediaIds = new ArrayList<>();
        }
        return this.mediaIds;
    }

    /** addMediaId adds media to a loan */
    private void addMediaId(UUID mediaId) {
        List<UUID> mediaIds = getMediaIds();
        if (mediaIds.contains(mediaId)) {
            return;
        }
        mediaIds.add(mediaId);
    }

    /** addMediaIds adds media to a loan */
    public void addMedia(UUID[] mediaId) {
        for (UUID id : mediaId) {
            addMediaId(id);
        }
    }

    /** getUserId gets the id of the user that loaned the media */
    public UUID getUserId() {
        return this.userId;
    }

    /** setUserId sets the id of the user media was loaned to */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /** getIssueDate gets the date the loan was issued */
    public LocalDate getIssueDate() {
        return this.issueDate;
    }

    /** getDueDate gets the date the return of the media is due */
    public LocalDate getDueDate() {
        return this.dueDate;
    }

    /** setDueDate sets the date the return of the media is due */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /** getReturnDate gets the date the loaned media was returned */
    public LocalDate getReturnDate() {
        return this.returnDate;
    }

    /** setReturnDate sets the date media was returned */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /** getNote gets the note attached to the loan */
    public String getNote() {
        return this.note;
    }

    /** setNote attaches a note to a loan */
    public void setNote(String note) {
        this.note = note;
    }

    /** getStatus gets the status of the loan */
    public LoanStatus getStatus() {
        return this.status;
    }

    /** setStatus sets the status of a loan */
    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    /** getDaysOverdue gets the amount of days the return of the media is overdue */
    public double getDaysOverdue() {
        return LocalDate.now().toEpochDay() - dueDate.toEpochDay();
    }
}
