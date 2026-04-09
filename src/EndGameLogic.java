import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndGameLogic {

    private Map<City, List<Route>> mapGraph;
    private List<Player> players;
    private Map<Player, MapGraph> playerGraphs;

    public EndGameLogic(MapGraph map, List<Player> players){
        this.mapGraph = map.getGraph();
        this.players = players;

        Map<Player, MapGraph> playerGraphs = new HashMap<>();
        this.playerGraphs = playerGraphs;

        for (Player player : this.players) {
            playerGraphs.put(player, new MapGraph());
        }
    }

    public void populatePlayerGraphs(){
        for (Map.Entry<City, List<Route>> entry : mapGraph.entrySet()) {
            // key = entry.getKey()
            // value = entry.getValue(

            City city = entry.getKey();
            List<Route> routesList = entry.getValue();

            for (Route route: routesList){
                if (route.getOwner() == null){
                    continue;
                }
                Player owner = route.getOwner();
                City cityA = route.getCityA();
                City cityB = route.getCityB();

                MapGraph playerGraph = this.playerGraphs.get(owner);
                if (!playerGraph.hasConnection(cityA, cityB)){
                    playerGraph.addRoute(route);
                }
            }
        }
    }


    public Map<Player, MapGraph> getPlayerGraphs() {
        return this.playerGraphs;
    }


}
