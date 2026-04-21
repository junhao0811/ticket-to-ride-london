import java.util.*;

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


    public void findPlayerLongestPath(){

        Set<Player> players = this.playerGraphs.keySet();
        for (Player player : players){

            MapGraph currGraph = this.playerGraphs.get(player);
            Set<City> keys = currGraph.getAllCities(); //get all the cities
            int maxDist = 0;
            Deque<City> maxPath = new ArrayDeque<>();

            for (City city : keys){

                Deque<City> currentPath = new ArrayDeque<>();
                currentPath.push(city);
                //Set<City> discoveredSet = new HashSet<>();
                Set<Route> discoveredSet = new HashSet<>();
                int currentDist = 0;

                DequeResult result = dfsFindMax(currGraph, discoveredSet, city, currentPath, currentDist);
                if (result.value() > maxDist){
                    maxDist = result.value();
                    maxPath = result.deque();
                }
            }
            player.setMaxDist(maxDist);
        }
    }

    public void awardBonus(){
        int absoluteMaximum = 0;
        List<Player> wonBonus = new ArrayList<>();
        Set<Player> players = this.playerGraphs.keySet();

        for (Player player : players){
            if (player.getMaxDist() > absoluteMaximum){
                absoluteMaximum = player.getMaxDist();
                wonBonus.clear();
                wonBonus.add(player);
            } else if (player.getMaxDist() == absoluteMaximum && player.getMaxDist() > 0){
                wonBonus.add(player);
            }
        }
        for (Player player : wonBonus){
            player.addScore(10);
            player.setTrueHasLongestPathBonus();
        }
    }


    public DequeResult dfsFindMax(MapGraph graph, Set<Route> discoveredSet, City source, Deque<City> currentPath, int currentDist) {


        int maxDist = currentDist;
        Deque<City> maxPath = new ArrayDeque<>(currentPath);

        for (Route route : graph.getCityRoutes(source)) {
            if (!discoveredSet.contains(route)) {
                discoveredSet.add(route);
                City oppositeCity = route.getOppositeCity(source);
                currentPath.push(oppositeCity);
                int weight = route.getLength();
                int nextDist = currentDist + weight;
                DequeResult childResult = dfsFindMax(graph, discoveredSet, oppositeCity, currentPath, nextDist);
                if (childResult.value() > maxDist) {
                    maxDist = childResult.value();
                    maxPath = childResult.deque();
                }
                currentPath.pop();
                discoveredSet.remove(route);
            }
        }
        return new DequeResult(maxPath, maxDist);
    }



    public record DequeResult(Deque<City> deque, int value) {}



}
