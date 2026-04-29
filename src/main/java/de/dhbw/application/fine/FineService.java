package de.dhbw.application.fine;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.fine.FineStatus;
import de.dhbw.persistence.fine.FineRepository;
import de.dhbw.application.loan.LoanService;
import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.application.fine.FineCalculator;
import de.dhbw.domain.fine.Fine;
import de.dhbw.util.Config;
import java.time.LocalDate;
import de.dhbw.util.Logger;

import java.util.List;
import java.util.Optional;
import de.dhbw.util.UUID;

public class FineService {
    private final FineRepository fineRepository;
    private final LoanService loanService;

    public FineService(
        LoanService loanService,
        FineRepository fineRepository
    ) {
        this.loanService = loanService;
        this.fineRepository = fineRepository;
    }

    /**
     * payFine changes the remaining amount of the fine.
     */
    public void payFine(UUID fineId, double amount) {
        Optional<Fine> fineOptional = fineRepository.findById(fineId);
        if (fineOptional.isEmpty()) {
            Logger.warn("Fine not found: " + fineId);
        }

        Fine fine = fineOptional.get();

        if (fine.getStatus() == FineStatus.PAID) {
            Logger.warn("Fine is already paid!");
        }

        double fineAmount = fine.getAmount();

        if (amount < fineAmount) {
            fine.setAmount(fineAmount - amount);
            Logger.info("Fine with id " + fineId + "has been partially paid!");
        } else {
            fine.setAmount(0.0);
            fine.setStatus(FineStatus.PAID);
            Logger.info("Fine with id " + fineId + "has been fully paid!");
        }

        fineRepository.update(fine);
    }

    /**
     * waiveFine cancels a fine without paying it off
     */
    public void waiveFine(UUID fineId, String note) {
        Optional<Fine> fineOptional = fineRepository.findById(fineId);
        if (fineOptional.isEmpty()) {
            Logger.warn("Fine not found: " + fineId);
        }

        Fine fine = fineOptional.get();
        fine.setStatus(FineStatus.WAIVED);
        if (note != null && !note.isEmpty()) {
            fine.setNote(note);
        }
        fineRepository.update(fine);

        Logger.info("Fine with id " + fineId + "has been waived!");
    }

    /**
     * createFine creates accepts a Fine and saves it to the repository
     */
    public void createFine(Fine fine) {
        fineRepository.save(fine);
        Logger.info("Fine created with id " + fine.getFineId() + "!");
    }

    /**
     * generateFines scans loans and creates fines for overdue loans where no fine exists yet.
     * Returns the number of fines created.
     */
    public int generateFines() {
        int created = 0;
        if (loanService == null) {
            Logger.warn("LoanService not available, cannot generate fines.");
            return created;
        }

        List<Loan> loans = loanService.getAllLoans();
        for (Loan loan : loans) {
            if (loan.getStatus() != LoanStatus.OVERDUE) continue;

            List<Fine> existing = fineRepository.findByLoanId(loan.getLoanId());
            if (existing != null && !existing.isEmpty()) continue;

            double amount = FineCalculator.calculateOverdueFine(loan);
            if (amount <= 0.0) continue;

            Fine fine = new Fine(loan.getUserId());
            fine.setLoanId(loan.getLoanId());
            fine.setAmount(amount);
            fine.setDueDate(LocalDate.now().plusDays(30));
            fineRepository.save(fine);
            created++;
            Logger.info("Generated fine " + fine.getFineId() + " for overdue loan " + loan.getLoanId());
        }

        Logger.info("Finished generating fines. Created: " + created);
        return created;
    }

    /**
     * getAllFines gets a list of all fines
     */
    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }
    
    /**
     * getFineById gets a fine by its id
     */
    public Optional<Fine> getFineById(UUID fineId) {
        return fineRepository.findById(fineId);
    }

    /**
     * getFinesByUserId gets all fines associated with a user
     */
    public List<Fine> getFinesByUserId(UUID userId) {
        return fineRepository.findByUserId(userId);
    }

    /**
     * getFinesByStatus gets all fines with a certain status
     */
    public List<Fine> getFinesByStatus(FineStatus status) {
        return fineRepository.findByStatus(status);
    }
}
