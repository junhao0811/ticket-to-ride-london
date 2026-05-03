package org.json;

import java.util.*;

/**
 * Minimal replacement for org.json.JSONArray to remove external dependencies.
 * Only implements methods used by ConfigLoader.
 */
public class JSONArray implements Iterable<Object> {
    private final List<Object> list;

    public JSONArray(List<Object> list) {
        this.list = list;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private final Iterator<Object> it = list.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Object next() {
                Object o = it.next();
                if (o instanceof Map) {
                    return new JSONObject((Map<String, Object>) o);
                }
                return o;
            }
        };
    }
}
