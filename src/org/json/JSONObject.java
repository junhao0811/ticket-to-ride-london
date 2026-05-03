package org.json;

import java.util.*;

/**
 * Minimal replacement for org.json.JSONObject to remove external dependencies.
 * Only implements methods used by ConfigLoader.
 */
public class JSONObject {
    private final Map<String, Object> map;

    public JSONObject(String json) {
        Object parsed = new JSONParser(json).parse();
        if (!(parsed instanceof Map)) {
            throw new RuntimeException("Root of JSON must be an object");
        }
        this.map = (Map<String, Object>) parsed;
    }

    @SuppressWarnings("unchecked")
    public JSONObject(Map<String, Object> map) {
        this.map = map;
    }

    public String getString(String key) {
        Object v = map.get(key);
        return v == null ? null : v.toString();
    }

    public int getInt(String key) {
        Object v = map.get(key);
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(v.toString());
    }

    public double getDouble(String key) {
        Object v = map.get(key);
        if (v instanceof Number n) return n.doubleValue();
        return Double.parseDouble(v.toString());
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJSONObject(String key) {
        Object v = map.get(key);
        if (v instanceof Map) return new JSONObject((Map<String, Object>) v);
        throw new RuntimeException("Key '" + key + "' is not an object");
    }

    @SuppressWarnings("unchecked")
    public JSONArray getJSONArray(String key) {
        Object v = map.get(key);
        if (v instanceof List) return new JSONArray((List<Object>) v);
        throw new RuntimeException("Key '" + key + "' is not an array");
    }
}
