package ui;

import model.board.SegmentPoint;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Singleton that parses config.json from the classpath once and exposes typed data
 * for city positions, route segment_points, card image paths, and ticket images.
 *
 * <p>City IDs in config are camelCase (e.g. {@code regentsPark}); Java Route objects
 * use display names (e.g. {@code "Regent's Park"}).  Use {@link #javaNameToId} and
 * {@link #CITY_ID_TO_JAVA_NAME} to convert between the two conventions.</p>
 */
public final class ConfigLoader {

    // ── Inner data types ─────────────────────────────────────────────────────

    /** A city as defined in the config map. */
    public record CityConfig(String id, String displayName, double x, double y) {}

    /**
     * A route as defined in config.
     * {@code cartType} is the raw string from config (e.g. "BLUE", "GRAY").
     */
    public record RouteConfig(
            String nodeName,
            String fromId,
            String toId,
            int routeLength,
            String cartType,
            List<SegmentPoint> segments) {}

    /** One transport card type from config, including its deck count and image path. */
    public record TransportCardConfig(String cartType, int cardCount, String imagePath) {}

    /** One destination ticket from config, including its point value and image path. */
    public record TicketImageEntry(String fromId, String toId, int points, String imagePath) {}

    // ── City ID ↔ Java display-name mapping ──────────────────────────────────

    /** Maps config camelCase city ID → Java Route display name. */
    public static final Map<String, String> CITY_ID_TO_JAVA_NAME = Map.ofEntries(
        Map.entry("regentsPark",       "Regent's Park"),
        Map.entry("bakerStreet",       "Baker Street"),
        Map.entry("kingsCross",        "King's Cross"),
        Map.entry("britishMuseum",     "British Museum"),
        Map.entry("hydePark",          "Hyde Park"),
        Map.entry("charterhouse",      "The Charterhouse"),
        Map.entry("stPauls",           "St Paul's"),
        Map.entry("brickLane",         "Brick Lane"),
        Map.entry("towerOfLondon",     "Tower of London"),
        Map.entry("globeTheatre",      "Globe Theatre"),
        Map.entry("waterloo",          "Waterloo"),
        Map.entry("elephantAndCastle", "Elephant & Castle"),
        Map.entry("coventGarden",      "Covent Garden"),
        Map.entry("piccadillyCircus",  "Piccadilly Circus"),
        Map.entry("trafalgarSquare",   "Trafalgar Square"),
        Map.entry("bigBen",            "Big Ben"),
        Map.entry("buckinghamPalace",  "Buckingham Palace")
    );

    // ── Singleton ────────────────────────────────────────────────────────────

    private static ConfigLoader instance;

    /** Returns the shared instance, loading config.json on first call. */
    public static ConfigLoader get() {
        if (instance == null) instance = new ConfigLoader();
        return instance;
    }

    // ── Fields ───────────────────────────────────────────────────────────────

    private final Map<String, CityConfig>      citiesById;
    private final List<RouteConfig>            routes;
    private final List<TransportCardConfig>    transportCards;
    private final Map<String, String>          cardImagePaths; // e.g. "BLUE" → "/img/transport_blue.png"
    private final List<TicketImageEntry>       ticketImages;
    private final Map<String, String>          javaNameToId;
    private final Map<String, Image>           imageCache = new HashMap<>();

    // ── Construction ─────────────────────────────────────────────────────────

    private ConfigLoader() {
        JSONObject root       = loadJson("/config.json");
        JSONObject map        = root.getJSONObject("map");
        JSONObject gameConfig = root.getJSONObject("game_config");

        citiesById     = parseCities(map.getJSONArray("cities"));
        routes         = parseRoutes(map.getJSONArray("routes"));
        transportCards = parseTransportCards(gameConfig.getJSONArray("transport_cards"));
        cardImagePaths = buildCardImagePaths(transportCards);
        ticketImages   = parseTicketImages(gameConfig.getJSONArray("destination_tickets"));

        Map<String, String> rev = new HashMap<>();
        CITY_ID_TO_JAVA_NAME.forEach((id, name) -> rev.put(name, id));
        javaNameToId = Collections.unmodifiableMap(rev);
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /** @return all cities keyed by their camelCase config ID */
    public Map<String, CityConfig> getCitiesById()           { return citiesById; }

    /** @return all route configs from the config map */
    public List<RouteConfig> getRoutes()                     { return routes; }

    /** @return all transport card types with their deck counts and image paths */
    public List<TransportCardConfig> getTransportCards()     { return transportCards; }

    /** @return map of config cart_type string → classpath image path */
    public Map<String, String> getCardImagePaths()           { return cardImagePaths; }

    /** @return all destination ticket configs with point values and image paths */
    public List<TicketImageEntry> getTicketImages()          { return ticketImages; }

    /**
     * Converts a Java Route city display name to its config camelCase ID.
     * Returns an empty string if the name is not in the mapping.
     */
    public String javaNameToId(String javaName) {
        return javaNameToId.getOrDefault(javaName, "");
    }

    /**
     * Finds the RouteConfig whose endpoint city IDs match the given Java city name pair
     * (checked in both directions).  Returns null if no match is found.
     */
    public RouteConfig findRoute(String cityAJava, String cityBJava) {
        String fromId = javaNameToId(cityAJava);
        String toId   = javaNameToId(cityBJava);
        for (RouteConfig rc : routes) {
            if ((rc.fromId().equals(fromId) && rc.toId().equals(toId))
             || (rc.fromId().equals(toId)   && rc.toId().equals(fromId))) {
                return rc;
            }
        }
        return null;
    }

    /**
     * Finds a TicketImageEntry whose city-pair matches the given Java city names
     * (checked in both directions).  Returns null if no match exists.
     */
    public TicketImageEntry findTicketImage(String cityAJava, String cityBJava) {
        String fromId = javaNameToId(cityAJava);
        String toId   = javaNameToId(cityBJava);
        for (TicketImageEntry t : ticketImages) {
            if ((t.fromId().equals(fromId) && t.toId().equals(toId))
             || (t.fromId().equals(toId)   && t.toId().equals(fromId))) {
                return t;
            }
        }
        return null;
    }

    /**
     * Loads an image from the classpath, caching the result.
     * Returns null if the resource is missing or unreadable.
     */
    public Image loadImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) return null;
        String path = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        if (imageCache.containsKey(path)) return imageCache.get(path);
        Image img = null;
        try (InputStream is = ConfigLoader.class.getResourceAsStream(path)) {
            if (is != null) img = ImageIO.read(is);
        } catch (IOException ignored) {}
        imageCache.put(path, img);
        return img;
    }

    // ── JSON parsing helpers ─────────────────────────────────────────────────

    private Map<String, CityConfig> parseCities(JSONArray arr) {
        Map<String, CityConfig> map = new LinkedHashMap<>();
        for (Object o : arr) {
            JSONObject c   = (JSONObject) o;
            String     id  = c.getString("id");
            JSONObject pos = c.getJSONObject("position");
            map.put(id, new CityConfig(
                id,
                c.getString("display_name"),
                pos.getDouble("x"),
                pos.getDouble("y")
            ));
        }
        return Collections.unmodifiableMap(map);
    }

    private List<RouteConfig> parseRoutes(JSONArray arr) {
        List<RouteConfig> list = new ArrayList<>();
        for (Object o : arr) {
            JSONObject r    = (JSONObject) o;
            String nodeName = r.getString("node_name");
            String from     = r.getString("from_city_id");
            String to       = r.getString("to_city_id");
            int    length   = r.getInt("route_length");
            String cartType = r.getString("cart_type");
            List<SegmentPoint> segs = new ArrayList<>();
            for (Object sp : r.getJSONArray("segment_points")) {
                JSONObject s   = (JSONObject) sp;
                JSONObject pos = s.getJSONObject("position");
                segs.add(new SegmentPoint(
                    pos.getDouble("x"),
                    pos.getDouble("y"),
                    s.getDouble("rotation_radians")
                ));
            }
            list.add(new RouteConfig(nodeName, from, to, length, cartType,
                                     Collections.unmodifiableList(segs)));
        }
        return Collections.unmodifiableList(list);
    }

    private List<TransportCardConfig> parseTransportCards(JSONArray arr) {
        List<TransportCardConfig> list = new ArrayList<>();
        for (Object o : arr) {
            JSONObject tc = (JSONObject) o;
            list.add(new TransportCardConfig(
                tc.getString("cart_type"),
                tc.getInt("card_count"),
                "/" + tc.getString("image_path")
            ));
        }
        return Collections.unmodifiableList(list);
    }

    private Map<String, String> buildCardImagePaths(List<TransportCardConfig> cards) {
        Map<String, String> map = new LinkedHashMap<>();
        for (TransportCardConfig tc : cards) {
            map.put(tc.cartType(), tc.imagePath());
        }
        return Collections.unmodifiableMap(map);
    }

    private List<TicketImageEntry> parseTicketImages(JSONArray arr) {
        List<TicketImageEntry> list = new ArrayList<>();
        for (Object o : arr) {
            JSONObject dt = (JSONObject) o;
            list.add(new TicketImageEntry(
                dt.getString("from_city_id"),
                dt.getString("to_city_id"),
                dt.getInt("points"),
                "/" + dt.getString("image_path")
            ));
        }
        return Collections.unmodifiableList(list);
    }

    private JSONObject loadJson(String resourcePath) {
        try (InputStream is = ConfigLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new RuntimeException("Config not found on classpath: " + resourcePath);
            return new JSONObject(new String(is.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config: " + resourcePath, e);
        }
    }
}
