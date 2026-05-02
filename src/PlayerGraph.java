import model.board.City;
import model.board.Route;

import java.util.*;

public class PlayerGraph {

    // represents player graph
    private List<City> cities;

    public void addCity(City newCity){
        this.cities.add(newCity);
    }

    public List<Route> getCityRoutes(City city){
        return city.getRoutes();
    }

    public List<City> getAllCities(){
        return this.cities;
    }

    public boolean pathExists(City source, City target){
        if (!cities.contains(source) || !cities.contains(target)) {
            return false; //safety check for when player made no routes towards these 2 cities so can never be connected and the cities wont exist in player's graph
        }

        Queue<City> discovered_q = new LinkedList<>();
        Set<City> discoveredSet = new HashSet<>();

        discovered_q.add(source);
        discoveredSet.add(source);

        while ( !discovered_q.isEmpty() ){
            City currCity = discovered_q.poll();

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

}
