package de.dhbw.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    // simple POJO for serialisation tests
    static class Point {
        int x;
        int y;
        Point() {}
        Point(int x, int y) { this.x = x; this.y = y; }
    }

    @Test
    void toJsonProducesJson() {
        String json = JsonUtils.toJson(new Point(1, 2));
        assertTrue(json.contains("\"x\""));
        assertTrue(json.contains("1"));
    }

    @Test
    void fromJsonRestoresObject() {
        String json = "{\"x\":3,\"y\":4}";
        Point p = JsonUtils.fromJson(json, Point.class);
        assertNotNull(p);
        assertEquals(3, p.x);
        assertEquals(4, p.y);
    }

    @Test
    void fromJsonInvalidReturnsNull() {
        assertNull(JsonUtils.fromJson("not json {{{", Point.class));
    }

    @Test
    void roundTripList() {
        List<Point> list = List.of(new Point(1, 2), new Point(3, 4));
        String json = JsonUtils.toJson(list);
        assertNotNull(json);
        assertTrue(json.contains("\"x\""));
    }

    @Test
    void fileExistsReturnsFalseForMissing() {
        assertFalse(JsonUtils.fileExists("nonexistent_file_xyz.json"));
    }
}
