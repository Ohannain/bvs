package de.dhbw.persistence.loan;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.util.Config;
import de.dhbw.util.JsonUtils;
import de.dhbw.util.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JsonLoanRepository implements LoanRepository {

    private final String filePath;
    private List<Loan> loans;

    /** Creates a new Loan Repository */
    public JsonLoanRepository() {
        this.filePath = Config.LOANS_FILE;
        this.loans = new ArrayList<Loan>();
        loadLoans();
    }

    /**
     * loadLoans loads all stored loans from the JSON file into memory.
     */
    private void loadLoans() {
        try {
            this.loans = JsonUtils.readListFromFile(filePath, Loan.class);
            Logger.info("Loaded " + loans.size() + " loans from " + filePath);
        } catch (IOException e) {
            Logger.warn("Could not load fines from file: " + e.getMessage());
        }
    }

    /**
     * saveLoans writes the current list of loans back to the JSON file.
     * It should be called after any modification to the loans list (add, update, delete) to ensure data persistence.
     */
    private void saveLoans() {
        try {
            JsonUtils.writeListToFile(filePath, loans);
            Logger.debug("Saved " + loans.size() + " loans to " + filePath);
        } catch (IOException e) {
            Logger.error("Failed to save loans: " + e.getMessage());
        }
    }

    /**
     * save add a new loan to the repository and stores the changes inside the JSON file.
     */
    @Override
    public void save(Loan loan) {
        if (loan == null) {
            Logger.error("Cannot save null loan");
            return;
        }
        if (loans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId()))) {
            Logger.warn(
                "Loan with id " +
                    loan.getLoanId() +
                    " already exists. Consider update() for modifications to an existing loan."
            );
        }
        loans.add(loan);
        saveLoans();
        Logger.info("Loan saved: " + loan.getLoanId());
    }

    /**
     * update modifies an existing loan in the repository. The loan to be updates is identified by its unique ID.
     */
    @Override
    public void update(Loan loan) {
        if (loan == null) {
            Logger.error("Cannot update null fine.");
            return;
        }
        loans.removeIf(l -> l.getLoanId().equals(loan.getLoanId()));
        loans.add(loan);
        saveLoans();
        Logger.info("Updated loan: " + loan.getLoanId());
    }

    /**
     * delete removes a loan from the repository and saves changes to the JSON file.
     */
    @Override
    public void delete(UUID loanId) {
        boolean removed = loans.removeIf(l -> l.getLoanId().equals(loanId));
        if (removed) {
            saveLoans();
            Logger.info("Deleted loan: " + loanId);
        }
    }

    /**
     * findAll returns a list of all loans currently stored in the repository. It provides a snapshot of the current state of loans.
     */
    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(loans);
    }

    /**
     * findById searches for a loan by its unique ID. It returns an Optional containing the found fine, or an empty Optional if no fine with the given ID exists.
     */
    @Override
    public Optional<Loan> findById(UUID loanId) {
        return loans
            .stream()
            .filter(l -> l.getLoanId().equals(loanId))
            .findFirst();
    }

    /**
     * findByUserId retrieves all loans associated with a specific user.
     */
    @Override
    public List<Loan> findByUserId(UUID userId) {
        return loans
            .stream()
            .filter(l -> l.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    /**
     * findByStatus returns a list of loans that match the specified status.
     */
    @Override
    public List<Loan> findByStatus(LoanStatus status) {
        return loans
            .stream()
            .filter(l -> l.getStatus() == status)
            .collect(Collectors.toList());
    }
}
