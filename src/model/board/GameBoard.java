package model.board;

import variant.MapConfiguration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Live game state of the London map — cities and routes.
 * Constructed once from a {@link MapConfiguration}; passed by injection everywhere.
 * NOT a singleton: use constructor injection to keep the design testable and extensible.
 */
public class GameBoard {

    private final List<City> cities;
    private final List<Route> routes;

    /** @param config the map configuration that supplies cities and routes */
    public GameBoard(MapConfiguration config) {
        this.cities = config.getCities();
        this.routes = config.getRoutes();
    }

    /** @return unmodifiable list of all cities on the board */
    public List<City> getCities() {
        return Collections.unmodifiableList(cities);
    }

    /** @return unmodifiable list of all routes on the board */
    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    /** @return the route with the given id, or empty if not found */
    public Optional<Route> findRouteById(String id) {
        return routes.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    /** @return the city with the given name, or empty if not found */
    public Optional<City> findCityByName(String name) {
        return cities.stream().filter(c -> c.getName().equals(name)).findFirst();
    }
}
