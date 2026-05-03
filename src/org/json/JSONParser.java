package org.json;

import java.util.*;

/**
 * Simple recursive descent parser for a subset of JSON.
 * Specifically designed to handle the Ticket to Ride config.json.
 */
class JSONParser {
    private final String json;
    private int pos = 0;

    JSONParser(String json) {
        this.json = json;
    }

    Object parse() {
        skipWhitespace();
        if (pos >= json.length()) return null;

        char c = json.charAt(pos);
        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();
        if (Character.isDigit(c) || c == '-') return parseNumber();
        if (json.startsWith("true", pos)) { pos += 4; return true; }
        if (json.startsWith("false", pos)) { pos += 5; return false; }
        if (json.startsWith("null", pos)) { pos += 4; return null; }

        throw new RuntimeException("Unexpected character at " + pos + ": " + c);
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++; // skip '{'
        while (true) {
            skipWhitespace();
            if (json.charAt(pos) == '}') {
                pos++;
                break;
            }
            String key = parseString();
            skipWhitespace();
            if (json.charAt(pos) != ':') {
                throw new RuntimeException("Expected ':' at " + pos);
            }
            pos++;
            map.put(key, parse());
            skipWhitespace();
            if (json.charAt(pos) == ',') {
                pos++;
            } else if (json.charAt(pos) != '}') {
                throw new RuntimeException("Expected ',' or '}' at " + pos);
            }
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        pos++; // skip '['
        while (true) {
            skipWhitespace();
            if (json.charAt(pos) == ']') {
                pos++;
                break;
            }
            list.add(parse());
            skipWhitespace();
            if (json.charAt(pos) == ',') {
                pos++;
            } else if (json.charAt(pos) != ']') {
                throw new RuntimeException("Expected ',' or ']' at " + pos);
            }
        }
        return list;
    }

    private String parseString() {
        pos++; // skip '"'
        StringBuilder sb = new StringBuilder();
        while (pos < json.length() && json.charAt(pos) != '"') {
            char c = json.charAt(pos);
            if (c == '\\') {
                pos++;
                char next = json.charAt(pos);
                switch (next) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        String hex = json.substring(pos + 1, pos + 5);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                    }
                    default -> sb.append(next);
                }
            } else {
                sb.append(c);
            }
            pos++;
        }
        pos++; // skip '"'
        return sb.toString();
    }

    private Number parseNumber() {
        int start = pos;
        while (pos < json.length() && "0123456789+-.eE".indexOf(json.charAt(pos)) >= 0) {
            pos++;
        }
        String s = json.substring(start, pos);
        if (s.contains(".") || s.contains("e") || s.contains("E")) {
            return Double.parseDouble(s);
        }
        return Integer.parseInt(s);
    }

    private void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }
}
