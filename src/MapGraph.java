import java.util.*;

public class MapGraph{

    private Map<City, List<Route>> graph = new HashMap<>();


    public void addCity(City newCity) {
        graph.putIfAbsent(newCity, new ArrayList<>());
    }

    public void addRoute(Route newRoute){
        addCity(newRoute.getCityA());
        addCity(newRoute.getCityB());

        graph.get(newRoute.getCityA()).add(newRoute);
        graph.get(newRoute.getCityB()).add(newRoute);
    }

    public List<Route> getCityRoutes(City city) {
        return graph.get(city);
    }

    public Set<City> getAllCities(){
        Set<City> keys = this.graph.keySet();
        return keys;
    }

    public Set<Route> getAllRoutes() {
        Set<Route> allRoutes = new HashSet<>();
        for (List<Route> cityRoutes : this.graph.values()) {
            allRoutes.addAll(cityRoutes);
        }
        return allRoutes;
    }

    public Map<City, List<Route>> getGraph(){
        return this.graph;
    }


    public boolean hasConnection(City cityA, City cityB) {
        if (!graph.containsKey(cityA)) {
            return false;
        }
        for (Route existingRoute : graph.get(cityA)) {
            if (existingRoute.getCityA().equals(cityB) || existingRoute.getCityB().equals(cityB)) {
                return true;
            }
        }
        return false;
    }


    public boolean pathExists(City source, City target){
        Queue<City> discovered_q = new LinkedList<>();
        Set<City> discoveredSet = new HashSet<>();

        discovered_q.add(source);
        discoveredSet.add(source);

        while ( !discovered_q.isEmpty() ){
            City currCity = discovered_q.poll();
            if (currCity == target){
                return true;
            }

            List<Route> currRoutes= this.getCityRoutes(currCity);

            for (Route route : currRoutes){
                City nextCity = route.getOppositeCity(currCity);

                if (!discoveredSet.contains(nextCity)){
                    discovered_q.add(nextCity);
                    discoveredSet.add(nextCity);
                }
            }
        }
        return false;
    }

    public Set<Map.Entry<City, List<Route>>> getAllEntries(){
        return this.graph.entrySet();
    }

    public DequeResult dfsFindMax( Set<Route> discoveredSet, City source, Deque<City> currentPath, int currentDist) {


        int maxDist = currentDist;
        Deque<City> maxPath = new ArrayDeque<>(currentPath);

        for (Route route : this.getCityRoutes(source)) {
            if (!discoveredSet.contains(route)) {
                discoveredSet.add(route);
                City oppositeCity = route.getOppositeCity(source);
                currentPath.push(oppositeCity);
                int weight = route.getLength();
                int nextDist = currentDist + weight;
                DequeResult childResult = this.dfsFindMax(discoveredSet, oppositeCity, currentPath, nextDist);
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
