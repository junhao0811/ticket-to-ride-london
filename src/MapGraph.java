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




    //public void removeCity(City city) {
   //     graph.remove(city);
   //     graph.values().forEach(values -> values.remove(city));
  //  }


   // public void addEdge(City cityA, City cityB) {
    //    Vertex<T> vertex2 = new Vertex<>(t2);
    //    Optional.ofNullable(graph.get(vertex1))
     //           .orElseThrow()
     //           .add(vertex2);
   //     Optional.ofNullable(graph.get(vertex2))
    //            .orElseThrow()
    //            .add(vertex1);
   // }

}
