import model.player.MapGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private final MapGraph boardGraph;
    private final List<Player> players;
    //private game.EndGame.EndGameScorer endGameScorer;


    public Game(MapGraph boardGraph, List<Player> players) {
        this.boardGraph = boardGraph;
        this.players = new ArrayList<>(players);
    }

    public MapGraph getBoardGraph() {
        return boardGraph;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players); // a safe to way to just see the player instances and access them
    }

    public boolean detectEnd(){
        for (Player player : this.getPlayers()){
            if (player.triggeredEndGame()){
                return true;
            }
        }
        return false;
    }

    public boolean finalRoundComplete(){
        return true;
    }

    public void startFinalRound(){
        //add the turn mechanics here
    }


    public void markEnded(){
        // later to be filled with something
    }

}