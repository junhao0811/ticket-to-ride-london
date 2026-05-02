import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Player> players = new ArrayList<>();
        Player playerTest = new Player();
        Player playerTest2 = new Player();

        players.add(playerTest);
        players.add(playerTest2);

        MapGraph boardGraph = new MapGraph();
        // add routes here

        Game game = new Game(boardGraph, players);

        //endgame helper classes instances
        EndGameScorer endGameScorer = new EndGameScorer(new PlayerGraphsBuilder(), new DestinationTicketScorer(), new LongestPathBonusScorer());
        GameController controller = new GameController(game, endGameScorer);
        controller.runGameLoop();


    }
}
