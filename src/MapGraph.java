import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapGraph{

    private Map<City, List<Route>> graph = new HashMap<>();



    public void addCity(City newCity) {

        graph.putIfAbsent(newCity, new ArrayList<>());
    }


    public void addRoute(Route newRoute){
        graph.get(newRoute.getCityA()).add(newRoute);
        graph.get(newRoute.getCityB()).add(newRoute);
    }

    public List<Route> getCityRoutes(City city) {
        return graph.get(city);
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
