public class Main {

    public static void main(String[] args) {

        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();

        City MontReal = new City("Montreal");
        City NewYork = new City("New York");
        City Boston = new City("Boston");

        Route montNew = new Route(MontReal, NewYork, 3, TrainColor.BLUE);
        Route newBost = new Route(NewYork, Boston, 2, TrainColor.YELLOW);

        MapGraph map = new MapGraph();

        


    }

}
