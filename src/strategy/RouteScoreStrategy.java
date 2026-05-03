package strategy;

import model.board.GameBoard;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.Player;

/**
 * Awards points for claimed routes using the official scoring table.
 * Matches the point scoring guide: 1→1, 2→2, 3→4, 4→7.
 */
public class RouteScoreStrategy implements ScoringStrategy {

    private static final int[] SCORE_TABLE = {0, 1, 2, 4, 7};

    @Override
    public int scoreRoute(Route route) {
        int len = route.getLength();
        if (len < 1 || len >= SCORE_TABLE.length) {
            return 0;
        }
        return SCORE_TABLE[len];
    }

    @Override
    public int scoreTicket(DestinationTicketCard ticket, Player player, GameBoard board) {
        return 0; // delegated to TicketScoreStrategy
    }

    @Override
    public int scoreLongestPath(Player player, GameBoard board) {
        return 0; // delegated to LongestPathStrategy
    }
}
