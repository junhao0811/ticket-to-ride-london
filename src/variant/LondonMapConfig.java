package variant;

import model.board.City;
import model.board.Route;
import model.board.SegmentPoint;
import model.cards.TransportationTypes;
import model.cards.DestinationTicketCard;
import model.cards.LocomotiveCard;
import model.cards.RegularTransportationCard;
import model.cards.TransportationCard;
import model.deck.DestinationTicketCardDeck;
import model.deck.TrainCardDeck;
import strategy.AnyColourStrategy;
import strategy.SpecificColourStrategy;
import ui.ConfigLoader;
import ui.ConfigLoader.RouteConfig;
import ui.ConfigLoader.TransportCardConfig;
import ui.ConfigLoader.TicketImageEntry;

import java.util.*;

/**
 * Builds the London map by reading all game data from config.json via {@link ConfigLoader}.
 * Cities, routes (with visual segment positions), the train-card deck, and the
 * destination-ticket deck are all driven by the single config file.
 */
public class LondonMapConfig implements MapConfiguration {

    @Override
    public List<City> getCities() {
        return new ArrayList<>(buildCityMap().values());
    }

    @Override
    public List<Route> getRoutes() {
        return buildRoutes(buildCityMap());
    }

    @Override
    public TrainCardDeck buildTrainDeck() {
        List<TransportationCard> cards = new ArrayList<>();
        for (TransportCardConfig tc : ConfigLoader.get().getTransportCards()) {
            if (tc.cartType().equals("LOCOMOTIVE")) {
                for (int i = 0; i < tc.cardCount(); i++) cards.add(new LocomotiveCard());
            } else {
                TransportationTypes colour = parseColour(tc.cartType());
                if (colour != null) {
                    for (int i = 0; i < tc.cardCount(); i++) cards.add(new RegularTransportationCard(colour));
                }
            }
        }
        TrainCardDeck deck = new TrainCardDeck();
        deck.addAll(cards);
        deck.shuffleCards();
        return deck;
    }

    @Override
    public DestinationTicketCardDeck buildTicketDeck() {
        List<DestinationTicketCard> tickets = new ArrayList<>();
        Map<String, City> cityMap = buildCityMap();
        for (TicketImageEntry t : ConfigLoader.get().getTicketImages()) {
            String fromName = ConfigLoader.CITY_ID_TO_JAVA_NAME.getOrDefault(t.fromId(), t.fromId());
            String toName   = ConfigLoader.CITY_ID_TO_JAVA_NAME.getOrDefault(t.toId(), t.toId());
            City fromCity = cityMap.get(fromName);
            City toCity = cityMap.get(toName);
            if (fromCity != null && toCity != null) {
                tickets.add(new DestinationTicketCard(fromCity, toCity, t.points()));
            }
        }
        DestinationTicketCardDeck deck = new DestinationTicketCardDeck(tickets);
        deck.shuffleCards();
        return deck;
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private Map<String, City> buildCityMap() {
        Map<String, City> map = new LinkedHashMap<>();
        for (ConfigLoader.CityConfig cc : ConfigLoader.get().getCitiesById().values()) {
            String javaName = ConfigLoader.CITY_ID_TO_JAVA_NAME.get(cc.id());
            if (javaName != null) {
                map.put(javaName, new City(javaName));
            }
        }
        return map;
    }

    private List<Route> buildRoutes(Map<String, City> cityMap) {
        List<Route> routes = new ArrayList<>();
        for (RouteConfig rc : ConfigLoader.get().getRoutes()) {
            String fromName = ConfigLoader.CITY_ID_TO_JAVA_NAME.get(rc.fromId());
            String toName   = ConfigLoader.CITY_ID_TO_JAVA_NAME.get(rc.toId());
            if (fromName == null || toName == null) continue;

            City from = cityMap.get(fromName);
            City to   = cityMap.get(toName);
            if (from == null || to == null) continue;

            List<SegmentPoint> segs = rc.segments();
            TransportationTypes colour = parseColour(rc.cartType());
            Route route;
            if (colour == null) {
                route = new Route(rc.nodeName(), from, to, rc.routeLength(),
                                  null, new AnyColourStrategy(), segs);
            } else {
                route = new Route(rc.nodeName(), from, to, rc.routeLength(),
                                  colour, new SpecificColourStrategy(colour), segs);
            }
            routes.add(route);
            from.addRoute(route);
            to.addRoute(route);
        }
        return routes;
    }

    /** Maps a config {@code cart_type} string to a {@link TransportationTypes}; returns null for "GRAY". */
    private TransportationTypes parseColour(String cartType) {
        return switch (cartType) {
            case "BLUE"   -> TransportationTypes.BLUE;
            case "GREEN"  -> TransportationTypes.GREEN;
            case "YELLOW" -> TransportationTypes.YELLOW;
            case "BLACK"  -> TransportationTypes.BLACK;
            case "ORANGE" -> TransportationTypes.ORANGE;
            case "PURPLE" -> TransportationTypes.PURPLE;
            default       -> null;
        };
    }
}
