package de.dhbw.persistence.fine;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.fine.FineStatus;
import de.dhbw.util.Config;
import de.dhbw.util.JsonUtils;
import de.dhbw.util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JsonFineRepository implements FineRepository {
    private final String filePath;
    private List<Fine> fines;
    
    /** Creates a new Fine Repository */
    public JsonFineRepository() {
        this.filePath = Config.FINES_FILE;
        this.fines = new ArrayList<>();
        loadFines();
    }

    /** 
     * loadFines loads all stored fines from the JSON file into memory.
     */
    private void loadFines() {
        try {
            this.fines = JsonUtils.readListFromFile(filePath, Fine.class);
            Logger.info("Loaded " + fines.size() + "fines from " + filePath);
        } catch (IOException e) {
            Logger.warn("Could not load fines from file: " + e.getMessage());
        }
    }

    /**
     * saveFines writes the current list of fines back to the JSON file.
     * It should be called after any modification to the fines list (add, update, delete) to ensure data persistence.
     */
    private void saveFines() {
        try {
            JsonUtils.writeListToFile(filePath, fines);
            Logger.debug("Saved " + fines.size() + "fines to " + filePath);
        } catch (IOException e) {
            Logger.error("Failed to save fines: " + e.getMessage());
        }
    }

    /**
     * save adds a new fine to the repository and stores the changes inside the JSON file.
     */
    @Override
    public void save(Fine fine) {
        if (fine == null) {
            Logger.error("Cannot save null fine");
            return;
        }
        if (fines.stream().anyMatch(f -> f.getFineId().equals(fine.getFineId()))) {
            Logger.warn(" Fine with ID: " + fine.getFineId() + " already exists. Consider update() for modifications to an existing fine.");
        }
        fines.add(fine);
        saveFines();
        Logger.info("Fine saved: " + fine.getFineId());
    }

    /**
     * update modifies an existing fine in the repository. The fine to be updated is identified by its unique ID.
     */
    @Override
    public void update(Fine fine) {
        if (fine == null) {
            Logger.error("Cannot update null fine.");
            return;
        }
        fines.removeIf(f -> f.getFineId().equals(fine.getFineId()));
        fines.add(fine);
        saveFines();
        Logger.info("Updated fine: " + fine.getFineId());
    }

    /**
     * delete removes a fine from the repository and saves changes to the JSON file.
     */
    @Override
    public void delete(UUID fineId) {
        boolean removed = fines.removeIf(f -> f.getFineId().equals(fineId));
        if (removed) {
            saveFines();
            Logger.info("Deleted fine: " + fineId);
        }
    }

    /**
     * findAll returns a list of all fines currently stored in the repository. It provides a snapshot of the current state of fines.
     */
    @Override
    public List<Fine> findAll() {
        return new ArrayList<>(fines);
    }

    /**
     * findById searches for a fine by its unique ID. It returns an Optional containing the found fine, or an empty Optional if no fine with the given ID exists.
     */
    @Override
    public Optional<Fine> findById(UUID fineId) {
        return fines.stream().filter(f -> f.getFineId().equals(fineId)).findFirst();
    }

    /**
     * findByUserId retrieves all fines associated with a specific user. 
     */
    @Override
    public List<Fine> findByUserId(UUID userId) {
        return fines.stream().filter(f -> f.getUserId().equals(userId)).collect(Collectors.toList());
    }

    /**
     * findByStatus returns a list of fines that match the specified status.
     */
    @Override
    public List<Fine> findByStatus(FineStatus status) {
        return fines.stream().filter(f -> f.getStatus() == status).collect(Collectors.toList());
    }
}
