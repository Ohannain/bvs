package de.dhbw.persistence.media;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.dhbw.domain.media.*;
import de.dhbw.util.Config;
import de.dhbw.util.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import de.dhbw.util.UUID;
import java.util.stream.Collectors;

public class JsonMediaRepository implements MediaRepository {
    private final String filePath;
    private List<Media> mediaList;
    private final Gson gson;

    public JsonMediaRepository() {
        this.filePath = Config.MEDIA_FILE;
        this.mediaList = new ArrayList<>();
        this.gson = createGsonWithPolymorphism();
        loadMedia();
    }

    public JsonMediaRepository(String filePath) {
        this.filePath = filePath;
        this.mediaList = new ArrayList<>();
        this.gson = createGsonWithPolymorphism();
        loadMedia();
    }

    private Gson createGsonWithPolymorphism() {
        RuntimeTypeAdapterFactory<Media> mediaAdapterFactory = RuntimeTypeAdapterFactory
                .of(Media.class, "type")
                .registerSubtype(Book.class, "BOOK")
                .registerSubtype(DVD.class, "DVD")
            .registerSubtype(BluRay.class, "BLURAY")
            .registerSubtype(EBook.class, "EBOOK")
                .registerSubtype(CD.class, "CD");

        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(UUID.class, new UUIDAdapter())
                .registerTypeAdapterFactory(mediaAdapterFactory)
                .create();
    }

    private void loadMedia() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                this.mediaList = new ArrayList<>();
                saveMedia();
                Logger.info("Media file not found. Created new file at " + filePath);
                return;
            }

            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<Media>>(){}.getType();
                List<Media> loaded = gson.fromJson(reader, listType);
                this.mediaList = loaded != null ? loaded : new ArrayList<>();
                Logger.info("Loaded " + mediaList.size() + " media items from " + filePath);
            }
        } catch (IOException e) {
            Logger.warn("Could not load media from file: " + e.getMessage());
            this.mediaList = new ArrayList<>();
        }
    }

    private void saveMedia() {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(mediaList, writer);
                Logger.debug("Saved " + mediaList.size() + " media items to " + filePath);
            }
        } catch (IOException e) {
            Logger.error("Failed to save media: " + e.getMessage());
        }
    }

    @Override
    public void save(Media media) {
        if (media == null || media.getMediaId() == null) {
            Logger.error("Cannot save null media or media with null ID");
            return;
        }
        mediaList.removeIf(m -> m.getMediaId().equals(media.getMediaId()));
        mediaList.add(media);
        saveMedia();
        Logger.info("Saved media: " + media.getMediaId());
    }

    @Override
    public List<Media> findById(UUID mediaId) {
        if (mediaId == null) {
            return List.of();
        }
        return mediaList.stream()
                .filter(m -> mediaId.equals(m.getMediaId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Media> findAll() {
        return new ArrayList<>(mediaList);
    }

    @Override
    public List<Media> findByTitle(String title) {
        if (title == null || title.isBlank()) {
            return List.of();
        }
        String searchTerm = title.trim().toLowerCase(Locale.ROOT);
        return mediaList.stream()
                .filter(m -> m.getTitle() != null && m.getTitle().toLowerCase(Locale.ROOT).contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Media> findByAuthor(String author) {
        if (author == null || author.isBlank()) {
            return List.of();
        }
        String searchTerm = author.trim().toLowerCase(Locale.ROOT);
        return mediaList.stream()
                .filter(m -> m.getAuthor() != null && m.getAuthor().toLowerCase(Locale.ROOT).contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Media> findByType(MediaType type) {
        return mediaList.stream()
                .filter(m -> m.getMediaType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Media> findByStatus(MediaStatus status) {
        return mediaList.stream()
                .filter(m -> m.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Media media) {
        save(media);
    }

    @Override
    public void delete(UUID mediaId) {
        boolean removed = mediaList.removeIf(m -> mediaId != null && mediaId.equals(m.getMediaId()));
        if (removed) {
            saveMedia();
            Logger.info("Deleted media: " + mediaId);
        }
    }

    @Override
    /**
     * Executes the exists operation.
     */
    public boolean exists(UUID mediaId) {
        if (mediaId == null) {
            return false;
        }
        return mediaList.stream().anyMatch(m -> mediaId.equals(m.getMediaId()));
    }

    // LocalDate adapter
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        /**
         * Writes data to the target.
         */
        public void write(com.google.gson.stream.JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        /**
         * Reads data from the source.
         */
        public LocalDate read(com.google.gson.stream.JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDate.parse(in.nextString());
        }
    }

    private static class UUIDAdapter extends TypeAdapter<UUID> {
        @Override
        public void write(com.google.gson.stream.JsonWriter out, UUID value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public UUID read(com.google.gson.stream.JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            if (in.peek() == com.google.gson.stream.JsonToken.STRING) {
                return UUID.fromString(in.nextString());
            }

            if (in.peek() == com.google.gson.stream.JsonToken.BEGIN_OBJECT) {
                in.beginObject();
                String value = null;
                while (in.hasNext()) {
                    String name = in.nextName();
                    if ("value".equals(name) && in.peek() == com.google.gson.stream.JsonToken.STRING) {
                        value = in.nextString();
                    } else {
                        in.skipValue();
                    }
                }
                in.endObject();
                return value == null ? null : UUID.fromString(value);
            }

            throw new JsonParseException("Invalid UUID token: " + in.peek());
        }
    }

    private static class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
        private final Class<?> baseType;
        private final String typeFieldName;
        private final java.util.Map<String, Class<?>> labelToSubtype = new java.util.LinkedHashMap<>();
        private final java.util.Map<Class<?>, String> subtypeToLabel = new java.util.LinkedHashMap<>();

        /**
         * Executes the runtime type adapter factory operation.
         */
        private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
            this.baseType = baseType;
            this.typeFieldName = typeFieldName;
        }

        /**
         * Executes the of operation.
         */
        public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
            return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName);
        }

        /**
         * Executes the register subtype operation.
         */
        public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
            labelToSubtype.put(label, type);
            subtypeToLabel.put(type, label);
            return this;
        }

        @Override
        /**
         * Executes the create operation.
         */
        public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
            if (!baseType.isAssignableFrom(type.getRawType())) {
                return null;
            }

            final java.util.Map<String, TypeAdapter<?>> labelToDelegate = new java.util.LinkedHashMap<>();
            final java.util.Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new java.util.LinkedHashMap<>();

            for (java.util.Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
                TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
                labelToDelegate.put(entry.getKey(), delegate);
                subtypeToDelegate.put(entry.getValue(), delegate);
            }

            return new TypeAdapter<R>() {
                @Override
                /**
                 * Reads data from the source.
                 */
                public R read(com.google.gson.stream.JsonReader in) throws IOException {
                    JsonElement jsonElement = JsonParser.parseReader(in);
                    JsonElement labelJsonElement = jsonElement.getAsJsonObject().get(typeFieldName);
                    if (labelJsonElement == null) {
                        throw new JsonParseException("cannot deserialize " + baseType + " because it does not define a field named " + typeFieldName);
                    }
                    String label = labelJsonElement.getAsString();
                    @SuppressWarnings("unchecked")
                    TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                    if (delegate == null) {
                        throw new JsonParseException("cannot deserialize " + baseType + " subtype named " + label);
                    }
                    return delegate.fromJsonTree(jsonElement);
                }

                @Override
                /**
                 * Writes data to the target.
                 */
                public void write(com.google.gson.stream.JsonWriter out, R value) throws IOException {
                    Class<?> srcType = value.getClass();
                    String label = subtypeToLabel.get(srcType);
                    @SuppressWarnings("unchecked")
                    TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
                    if (delegate == null) {
                        throw new JsonParseException("cannot serialize " + srcType.getName());
                    }
                    JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                    JsonObject clone = new JsonObject();
                    clone.add(typeFieldName, new JsonPrimitive(label));
                    for (java.util.Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                        clone.add(e.getKey(), e.getValue());
                    }
                    gson.toJson(clone, out);
                }
            };
        }
    }
}
