import java.util.ArrayList;
import java.util.List;

public class City {
    private String name;
    private List<Route> routes;

    public City(String name) {
        this.name = name;
        this.routes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addRoute(Route route) {
        if (route != null) {
            routes.add(route);
        }
    }

    public void addBonusPoints(Player player) {
        if (player != null) {
            player.addScore(routes.size());
        }
    }
}

