package de.dhbw.persistence.loan;

import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository {
    void save(Loan loan);
    void update(Loan loan);
    void delete(UUID id);
    Optional<Loan> findById(UUID id);
    List<Loan> findAll();
    List<Loan> findByUserId(UUID userId);
    List<Loan> findByStatus(LoanStatus status);
}
