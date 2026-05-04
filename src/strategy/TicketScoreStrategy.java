package strategy;

import game.EndGame.PlayerGraphsBuilder;
import model.board.GameBoard;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.MapGraph;
import model.player.Player;

import java.util.Map;

/**
 * Awards or deducts points for destination tickets.
 * Uses the teammate's MapGraph to check city connectivity.
 */
public class TicketScoreStrategy implements ScoringStrategy {

    @Override
    public int scoreRoute(Route route) {
        return 0; // delegated to RouteScoreStrategy
    }

    @Override
    public int scoreTicket(DestinationTicketCard ticket, Player player, GameBoard board) {
        PlayerGraphsBuilder builder = new PlayerGraphsBuilder();
        Map<Player, MapGraph> graphs = builder.buildPlayerGraphs(
            game.GameManager.getInstance().getPlayers(), board.getRoutes());
        
        MapGraph playerGraph = graphs.get(player);
        boolean connected = playerGraph != null && playerGraph.pathExists(ticket.getCityA(), ticket.getCityB());
        return connected ? ticket.getPointValue() : -ticket.getPointValue();
    }

    @Override
    public int scoreLongestPath(Player player, GameBoard board) {
        return 0; // delegated to LongestPathStrategy
    }
}
