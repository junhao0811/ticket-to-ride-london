package strategy;

import model.player.PlayerGraphsBuilder;
import model.board.GameBoard;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.MapGraph;
import model.player.Player;

import java.util.Map;

/**
 * Finds the longest continuous path for a player using the teammate's MapGraph.
 * Awards +10 bonus. Ties → both players receive +10.
 */
public class LongestPathStrategy implements ScoringStrategy {

    private static final int LONGEST_PATH_BONUS = 10;

    @Override
    public int scoreRoute(Route route) {
        return 0;
    }

    @Override
    public int scoreTicket(DestinationTicketCard ticket, Player player, GameBoard board) {
        return 0;
    }

    /**
     * Returns +10 if this player has the longest continuous path (or ties for it),
     * otherwise 0.
     */
    @Override
    public int scoreLongestPath(Player player, GameBoard board) {
        PlayerGraphsBuilder builder = new PlayerGraphsBuilder();
        Map<Player, MapGraph> graphs = builder.buildPlayerGraphs(
            game.GameManager.getInstance().getPlayers(), board.getRoutes());

        int myPath = getLongestPathDistance(player, board);
        int maxOther = 0;

        for (Player other : graphs.keySet()) {
            if (!other.equals(player)) {
                maxOther = Math.max(maxOther, graphs.get(other).findLongestPathDistance());
            }
        }
        return myPath >= maxOther && myPath > 0 ? LONGEST_PATH_BONUS : 0;
    }

    @Override
    public int getLongestPathDistance(Player player, GameBoard board) {
        PlayerGraphsBuilder builder = new PlayerGraphsBuilder();
        Map<Player, MapGraph> graphs = builder.buildPlayerGraphs(
            game.GameManager.getInstance().getPlayers(), board.getRoutes());
        
        MapGraph myGraph = graphs.get(player);
        return myGraph != null ? myGraph.findLongestPathDistance() : 0;
    }
}
