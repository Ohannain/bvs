package de.dhbw.application.fine;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.fine.FineStatus;
import de.dhbw.persistence.fine.FineRepository;

import java.util.List;
import java.util.UUID;

public class FineService {
    private final FineRepository fineRepository;

    public FineService(
        FineRepository fineRepository
    ) {
        this.fineRepository = fineRepository;
    }

    public void payFine(UUID fineId, double amount) {

    }

    public void waiveFine(UUID fineId, String note) {

    }

    public void createFine(UUID loanId, UUID userId) {

    }

    public List<Fine> getAllFines() {

    }

    public Fine getFineById(UUID fineId) {

    }

    public List<Fine> getFinesByUserId(UUID userId) {

    }

    public List<Fine> getFinesByStatus(FineStatus status) {

    }

    /**
     * Generates a unique id for a fine.
     *
     * @return {UUID} fineId - the generated unique id.
     */
    private UUID generateFineId() {
        UUID fineId;
        do {
            fineId = UUID.randomUUID();
        } while (fineRepository.findById(fineId).isPresent());
        return fineId;
    }
}
