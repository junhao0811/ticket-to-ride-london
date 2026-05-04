package model.player;

import model.board.Route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerGraphsBuilder {


    public Map<Player, MapGraph> buildPlayerGraphs(List<Player> players, List<Route> allRoutes){

        Map<Player, MapGraph> playerGraphs = new HashMap<>();
        //initialise player graphs
        for (Player player : players) {
            playerGraphs.put(player, new MapGraph());
        }

        for (Route route: allRoutes) {
            Player owner = route.getClaimedBy();
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
