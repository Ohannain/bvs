package de.dhbw.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    
    public static <T> String toJson(T object) {
        return gson.toJson(object);
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }
    
    public static <T> T fromJson(String json, Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }
    
    public static <T> void writeToFile(String filePath, T object) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(object, writer);
        }
    }

    /**
     * Reads data from the source.
     */
    public static <T> T readFromFile(String filePath, Class<T> clazz)throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        }
    }

    /**
     * Reads data from the source.
     */
    public static <T> T readFromFile(String filePath, Type type)throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, type);
        }
    }

    /**
     * Reads data from the source.
     */
    public static <T> List<T> readListFromFile(String filePath, Class<T> clazz) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            List<T> result = gson.fromJson(reader, listType);
            return result != null ? result : new ArrayList<>();
        }
    }
    
    /**
     * Writes data to the target.
     */
    public static <T> void writeListToFile(String filePath, List<T> list) throws IOException {
        writeToFile(filePath, list);
    }
    
    /**
     * Executes the file exists operation.
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Executes the ensure file exists operation.
     */
    public static void ensureFileExists(String filePath, String defaultContent) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                writer.write(defaultContent);
            }
        }
    }
    
    private static class LocalDateAdapter extends com.google.gson.TypeAdapter<LocalDate> {
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
            String dateStr = in.nextString();
            try {
                return LocalDate.parse(dateStr);
            } catch (java.time.format.DateTimeParseException e) {
                throw new JsonSyntaxException("Invalid date format: '" + dateStr + "'", e);
            }
        }
    }
}
