package strategy;

import model.board.GameBoard;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.Player;

/**
 * Wires the three scoring strategies together for the London variant.
 * Delegates each concern to the appropriate specialist.
 */
public class LondonScoringStrategy implements ScoringStrategy {
    
    private final RouteScoreStrategy    routeScorer   = new RouteScoreStrategy();
    private final TicketScoreStrategy   ticketScorer  = new TicketScoreStrategy();
    private final LongestPathStrategy   pathScorer    = new LongestPathStrategy();
    
    @Override
    public int scoreRoute(Route route) {
        return routeScorer.scoreRoute(route);
    }
    
    @Override
    public int scoreTicket(DestinationTicketCard ticket, Player player, GameBoard board) {
        return ticketScorer.scoreTicket(ticket, player, board);
    }
    
    @Override
    public int scoreLongestPath(Player player, GameBoard board) {
        return pathScorer.scoreLongestPath(player, board);
    }
}