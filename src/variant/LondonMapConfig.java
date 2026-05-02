package variant;

import model.board.City;
import model.board.Route;
import model.board.SegmentPoint;
import model.cards.CardColour;
import model.cards.DestinationTicketCard;
import model.cards.LocomotiveCard;
import model.cards.RegularTrainCard;
import model.deck.DestinationTicketDeck;
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
        List<model.cards.TrainCard> cards = new ArrayList<>();
        for (TransportCardConfig tc : ConfigLoader.get().getTransportCards()) {
            if (tc.cartType().equals("LOCOMOTIVE")) {
                for (int i = 0; i < tc.cardCount(); i++) cards.add(new LocomotiveCard());
            } else {
                CardColour colour = parseColour(tc.cartType());
                if (colour != null) {
                    for (int i = 0; i < tc.cardCount(); i++) cards.add(new RegularTrainCard(colour));
                }
            }
        }
        TrainCardDeck deck = new TrainCardDeck();
        deck.addAll(cards);
        deck.shuffleCards();
        return deck;
    }

    @Override
    public DestinationTicketDeck buildTicketDeck() {
        List<DestinationTicketCard> tickets = new ArrayList<>();
        for (TicketImageEntry t : ConfigLoader.get().getTicketImages()) {
            String fromName = ConfigLoader.CITY_ID_TO_JAVA_NAME.getOrDefault(t.fromId(), t.fromId());
            String toName   = ConfigLoader.CITY_ID_TO_JAVA_NAME.getOrDefault(t.toId(), t.toId());
            tickets.add(new DestinationTicketCard(fromName, toName, t.points()));
        }
        DestinationTicketDeck deck = new DestinationTicketDeck();
        deck.addAll(tickets);
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
            CardColour colour = parseColour(rc.cartType());
            Route route;
            if (colour == null) {
                route = new Route(rc.nodeName(), fromName, toName, rc.routeLength(),
                                  null, new AnyColourStrategy(), segs);
            } else {
                route = new Route(rc.nodeName(), fromName, toName, rc.routeLength(),
                                  colour, new SpecificColourStrategy(colour), segs);
            }
            routes.add(route);
            from.addRoute(route);
            to.addRoute(route);
        }
        return routes;
    }

    /** Maps a config {@code cart_type} string to a {@link CardColour}; returns null for "GRAY". */
    private CardColour parseColour(String cartType) {
        return switch (cartType) {
            case "BLUE"   -> CardColour.BLUE;
            case "GREEN"  -> CardColour.GREEN;
            case "YELLOW" -> CardColour.YELLOW;
            case "BLACK"  -> CardColour.BLACK;
            case "ORANGE" -> CardColour.ORANGE;
            case "PURPLE" -> CardColour.PURPLE;
            default       -> null;
        };
    }
}
