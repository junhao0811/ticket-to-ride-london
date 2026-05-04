package strategy;

import model.board.GameBoard;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.Player;

/**
 * Strategy interface for all scoring concerns. Swap implementations to support different Ticket to
 * Ride editions.
 */
public interface ScoringStrategy {
    
    /**
     * @return points awarded for claiming {@code route}
     */
    int scoreRoute(Route route);
    
    /**
     * @param ticket the destination ticket to evaluate
     * @param player the player who holds the ticket
     * @param board  the current game board
     * @return positive points if the ticket is completed, negative if not
     */
    int scoreTicket(DestinationTicketCard ticket, Player player, GameBoard board);
    
    /**
     * @param player the player to evaluate
     * @param board  the current game board
     * @return bonus points for the longest continuous path (0 if not applicable)
     */
    int scoreLongestPath(Player player, GameBoard board);

    /**
     * @param player the player to evaluate
     * @param board  the current game board
     * @return the length of the longest continuous path of claimed routes
     */
    default int getLongestPathDistance(Player player, GameBoard board) {
        return 0;
    }
}
