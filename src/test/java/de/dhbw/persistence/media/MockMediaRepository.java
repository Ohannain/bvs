package de.dhbw.persistence.media;

import de.dhbw.domain.media.Media;
import de.dhbw.domain.media.MediaStatus;
import de.dhbw.domain.media.MediaType;
import de.dhbw.util.UUID;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock-Implementierung des MediaRepository für Tests.
 * Zählt Aufrufe, simuliert Verfügbarkeit und verifiziert Seiteneffekte im LoanService.
 * 
 * Verwendung:
 *   MockMediaRepository mockRepo = new MockMediaRepository();
 *   LoanService service = new LoanService(loanRepo, mockRepo, userRepo);
 *   service.borrowMedia(...);
 *   assert mockRepo.getUpdateCount() > 0;
 *   assert mockRepo.getCallCount("update") == 2;
 */
public class MockMediaRepository implements MediaRepository {
    private final Map<UUID, Media> media = new HashMap<>();
    private final Map<String, Integer> callCounts = new HashMap<>();
    private final Set<UUID> unavailableMediaIds = new HashSet<>();

    public MockMediaRepository() {
        initCallCounts();
    }

    private void initCallCounts() {
        callCounts.put("save", 0);
        callCounts.put("findById", 0);
        callCounts.put("findAll", 0);
        callCounts.put("findByTitle", 0);
        callCounts.put("findByAuthor", 0);
        callCounts.put("findByType", 0);
        callCounts.put("findByStatus", 0);
        callCounts.put("update", 0);
        callCounts.put("delete", 0);
        callCounts.put("exists", 0);
    }

    @Override
    public void save(Media medium) {
        callCounts.put("save", callCounts.get("save") + 1);
        if (medium == null || medium.getMediaId() == null) {
            throw new IllegalArgumentException("Media und MediaId dürfen nicht null sein");
        }
        media.put(medium.getMediaId(), medium);
    }

    @Override
    public List<Media> findById(UUID mediaId) {
        callCounts.put("findById", callCounts.get("findById") + 1);
        if (mediaId == null) {
            return Collections.emptyList();
        }
        Media foundMedia = media.get(mediaId);
        return foundMedia != null ? List.of(foundMedia) : Collections.emptyList();
    }

    @Override
    public List<Media> findAll() {
        callCounts.put("findAll", callCounts.get("findAll") + 1);
        return new ArrayList<>(media.values());
    }

    @Override
    public List<Media> findByTitle(String title) {
        callCounts.put("findByTitle", callCounts.get("findByTitle") + 1);
        if (title == null || title.isBlank()) {
            return Collections.emptyList();
        }
        String searchTitle = title.toLowerCase();
        return media.values().stream()
                .filter(m -> m.getTitle().toLowerCase().contains(searchTitle))
                .collect(Collectors.toList());
    }

    @Override
    public List<Media> findByAuthor(String author) {
        callCounts.put("findByAuthor", callCounts.get("findByAuthor") + 1);
        if (author == null || author.isBlank()) {
            return Collections.emptyList();
        }
        String searchAuthor = author.toLowerCase();
        return media.values().stream()
                .filter(m -> m.getAuthor() != null && m.getAuthor().toLowerCase().contains(searchAuthor))
                .collect(Collectors.toList());
    }

    @Override
    public List<Media> findByType(MediaType type) {
        callCounts.put("findByType", callCounts.get("findByType") + 1);
        if (type == null) {
            return Collections.emptyList();
        }
        return media.values().stream()
                .filter(m -> m.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Media> findByStatus(MediaStatus status) {
        callCounts.put("findByStatus", callCounts.get("findByStatus") + 1);
        if (status == null) {
            return Collections.emptyList();
        }
        return media.values().stream()
                .filter(m -> m.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Media medium) {
        callCounts.put("update", callCounts.get("update") + 1);
        if (medium == null || medium.getMediaId() == null) {
            throw new IllegalArgumentException("Media und MediaId dürfen nicht null sein");
        }
        if (!media.containsKey(medium.getMediaId())) {
            throw new IllegalStateException("Media mit ID " + medium.getMediaId() + " existiert nicht");
        }
        media.put(medium.getMediaId(), medium);
    }

    @Override
    public void delete(UUID mediaId) {
        callCounts.put("delete", callCounts.get("delete") + 1);
        if (mediaId == null) {
            throw new IllegalArgumentException("MediaId darf nicht null sein");
        }
        media.remove(mediaId);
    }

    @Override
    public boolean exists(UUID mediaId) {
        callCounts.put("exists", callCounts.get("exists") + 1);
        if (mediaId == null) {
            return false;
        }
        return media.containsKey(mediaId) && !unavailableMediaIds.contains(mediaId);
    }

    // ===== Mock-spezifische Methoden =====

    /**
     * Gibt die Anzahl der Aufrufe einer bestimmten Methode zurück
     */
    public int getCallCount(String methodName) {
        return callCounts.getOrDefault(methodName, 0);
    }

    /**
     * Gibt die Gesamtzahl aller Aufrufe zurück
     */
    public int getTotalCallCount() {
        return callCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Gibt die Anzahl der Update-Aufrufe zurück (Seiteneffekt-Verifizierung)
     */
    public int getUpdateCount() {
        return getCallCount("update");
    }

    /**
     * Markiert ein Medium als nicht verfügbar (simuliert Verfügbarkeitsprüfung)
     */
    public void makeUnavailable(UUID mediaId) {
        unavailableMediaIds.add(mediaId);
    }

    /**
     * Markiert ein Medium als wieder verfügbar
     */
    public void makeAvailable(UUID mediaId) {
        unavailableMediaIds.remove(mediaId);
    }

    /**
     * Prüft, ob ein Medium als nicht verfügbar markiert ist
     */
    public boolean isUnavailable(UUID mediaId) {
        return unavailableMediaIds.contains(mediaId);
    }

    /**
     * Setzt alle Call-Counter zurück
     */
    public void resetCallCounts() {
        initCallCounts();
    }

    /**
     * Löscht alle Medien und setzt Counter zurück (für Test-Setup)
     */
    public void clear() {
        media.clear();
        unavailableMediaIds.clear();
        resetCallCounts();
    }

    /**
     * Gibt alle registrierten Call-Counts aus (für Debugging)
     */
    public Map<String, Integer> getAllCallCounts() {
        return new HashMap<>(callCounts);
    }

    /**
     * Gibt die Anzahl der gespeicherten Medien zurück
     */
    public int size() {
        return media.size();
    }
}
