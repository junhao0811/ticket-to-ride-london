package model.board;

import java.util.ArrayList;
import java.util.List;

/**
 * A named London city node on the game board.
 * Holds references to the routes that connect it to neighbours.
 * Contains no scoring logic — that belongs in ScoringStrategy.
 */
public class City {

    private final String name;
    private final List<Route> routes;


    /** @param name display name of the city */
    public City(String name) {
        this.name = name;
        this.routes = new ArrayList<>();
    }
    /** @return the city's display name */
    public String getName() {
        return name;
    }

    /** Registers a route that touches this city. */
    public void addRoute(Route route) {
        if (route != null) {
            routes.add(route);
        }
    }
}
