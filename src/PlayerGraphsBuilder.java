import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerGraphsBuilder {


    public Map<Player, MapGraph> buildPlayerGraphs(List<Player> players, MapGraph boardGraph){

        Map<Player, MapGraph> playerGraphs = new HashMap<>();
        //initialise player graphs
        for (Player player : players) {
            playerGraphs.put(player, new MapGraph());
        }

        for (Route route: boardGraph.getAllRoutes()) {
            Player owner = route.getOwner();
            if (owner == null || !playerGraphs.containsKey(owner)) {
                continue;
            }

            MapGraph playerGraph = playerGraphs.get(owner);
            if (!playerGraph.containsRoute(route)) {
                playerGraph.addRoute(route);
            }
        }
        return playerGraphs;
    }
}
