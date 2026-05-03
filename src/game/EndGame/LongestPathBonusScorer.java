package game.EndGame;

import model.player.MapGraph;
import model.player.Player;

import java.util.*;

public class LongestPathBonusScorer {

    private static final int BONUS_POINTS = 10;

    public List<Player> findLongestPathWinners(Map<Player, MapGraph> playerGraphs){
        Set<Player> players = playerGraphs.keySet();

        List<Player> winners = new ArrayList<>();
        int highestDistance = 0;

        for (Player player : players) {

            MapGraph currGraph = playerGraphs.get(player);
            int distance = currGraph.findLongestPathDistance();
            if( distance > highestDistance){
                highestDistance = distance;
                winners.clear(); //if a new max is found, all previous players have less than the max, so clear and start at the new max
                winners.add(player);
            } else if (distance == highestDistance && distance>0){
                // if first player being processed has dist 0, its = to highestDist, so need the check for dist>0 so that first player does not get added if they have no routes and dist 0
                winners.add(player);
            }
        }

        return winners;
    }

    public int getBonusPoints(){
        return BONUS_POINTS;
    }


}
