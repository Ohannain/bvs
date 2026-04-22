package de.dhbw.domain.fine;

import java.time.LocalDate;
import de.dhbw.util.UUID;

public class Fine {

    /** the id of the fine */
    private final UUID fineId;
    /** the id of the user being fined */
    private UUID userId;
    /** the loan a user can be fined for */
    private UUID loanId;
    /** the amount a user is being fined */
    private double amount;
    /** the date a fine was issued */
    private final LocalDate issueDate;
    /** the date a fine is due */
    private LocalDate dueDate;
    /** the date a fine was paid off */
    private LocalDate paidDate;
    /** the status of a fine */
    private FineStatus status;
    /** a note attached to the fine */
    private String note;

    /**
     * Creates a new Fine
     *
     * Every fine has an issueDate, is assigned the status PENDING and needs a user assigned to it.
     */
    public Fine(UUID userId) {
        this(userId, LocalDate.now());
    }

    /** Creates a new Fine with a specific issue date. */
    public Fine(UUID userId, LocalDate issueDate) {
        this.fineId = UUID.nextFineId();
        this.issueDate = issueDate;
        this.status = FineStatus.PENDING;
        this.userId = userId;
    }

    /** getFineId gets the id of a fine */
    public UUID getFineId() {
        return this.fineId;
    }

    /** getUserId gets the id of the user being fined */
    public UUID getUserId() {
        return this.userId;
    }

    /** setUserId sets the id of the user being fined */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /** getLoanId gets the id of the associated loan */
    public UUID getLoanId() {
        return this.loanId;
    }

    /** setLoanId sets the id of the associated loan */
    public void setLoanId(UUID loanId) {
        this.loanId = loanId;
    }

    /** getAmount gets the amount fined */
    public double getAmount() {
        return this.amount;
    }

    /** setAmount sets the amount fined */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /** getIssueDate gets the date the fine was issued */
    public LocalDate getIssueDate() {
        return this.issueDate;
    }

    /** getDueDate gets the date, a fine is due */
    public LocalDate getDueDate() {
        return this.dueDate;
    }

    /** setDueDate sets the date, a fine is due */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /** getPaidDate gets the date a fine was paid off */
    public LocalDate getPaidDate() {
        return this.paidDate;
    }

    /** setPaidDate sets the date a fine was paid off */
    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    /** getStatus gets the status of a fine */
    public FineStatus getStatus() {
        return this.status;
    }

    /** setStatus sets the status of a fine */
    public void setStatus(FineStatus status) {
        this.status = status;
    }

    /** getNote gets the note attached to the fine */
    public String getNote() {
        return this.note;
    }

    /** setNote adds a not to the fine */
    public void setNote(String note) {
        this.note = note;
    }

    /** getDaysOverdue gets the amount of days a fine is overdue */
    public double getDaysOverdue() {
        return LocalDate.now().toEpochDay() - dueDate.toEpochDay();
    }
}
