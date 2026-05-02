package model.player;

import model.board.City;
import model.board.Route;

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
        if (!graph.containsKey(source) || !graph.containsKey(target)) {
            return false; //safety check for when player made no routes towards these 2 cities so can never be connected and the cities wont exist in player's graph
        }

        Queue<City> discovered_q = new LinkedList<>();
        Set<City> discoveredSet = new HashSet<>();

        discovered_q.add(source);
        discoveredSet.add(source);

        while ( !discovered_q.isEmpty() ){
            City currCity = discovered_q.poll();
            //if (currCity == target){
                //return true;
            //}

            if (currCity.equals(target)) {
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

    public boolean containsRoute(Route route) {
        return getAllRoutes().contains(route);
    }

    public int findLongestPathDistance(){
        int maxDist = 0;

        for (City city : this.getAllCities()){

            Deque<City> currentPath = new ArrayDeque<>();
            currentPath.push(city);
            Set<Route> discoveredSet = new HashSet<>();
            int currentDist = 0;

            MapGraph.DequeResult result = dfsFindMax(discoveredSet, city, currentPath, currentDist);
            maxDist = Math.max(maxDist, result.value());
        }
        return maxDist;
    }

    private DequeResult dfsFindMax(Set<Route> discoveredSet, City source, Deque<City> currentPath, int currentDist) {


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
