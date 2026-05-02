package game;

import model.board.GameBoard;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.Player;
import strategy.ScoringStrategy;

import java.util.*;

/**
 * Manages all scoring for the game.
 * Receives its strategy via constructor injection — never instantiates a strategy itself.
 */
public class ScoreManager {

    private final ScoringStrategy strategy;

    /** @param strategy the scoring rule set for the active game variant */
    public ScoreManager(ScoringStrategy strategy) {
        this.strategy = strategy;
    }

    /** Awards route-claim points to {@code player} immediately when a route is claimed. */
    public void addRouteScore(Player player, Route route) {
        int pts = strategy.scoreRoute(route);
        player.getScore().addPoints(pts);
    }

    /**
     * Runs final scoring for all players.
     * Applies ticket +/- and longest-path bonus; returns a per-player breakdown.
     *
     * @param players all players in the game
     * @param board   the completed game board
     * @return list of {@link FinalScore} records in descending score order
     */
    public List<FinalScore> finalizeScores(List<Player> players, GameBoard board) {
        List<FinalScore> results = new ArrayList<>();

        for (Player p : players) {
            int routePts  = p.getScore().getPoints();
            int ticketPts = tallyTickets(p, board);
            int pathBonus = strategy.scoreLongestPath(p, board);

            p.getScore().addPoints(ticketPts);
            p.getScore().addPoints(pathBonus);

            results.add(new FinalScore(p, routePts, ticketPts, pathBonus, p.getScore().getPoints()));
        }

        results.sort(Comparator.comparingInt(FinalScore::total).reversed());
        return results;
    }

    private int tallyTickets(Player player, GameBoard board) {
        int total = 0;
        for (DestinationTicketCard ticket : player.getTickets()) {
            total += strategy.scoreTicket(ticket, player, board);
        }
        return total;
    }

    // ── Inner record for final scoring breakdown ──────────────────────────────

    /** Breakdown of a single player's final score. */
    public static class FinalScore {
        private final Player player;
        private final int routePoints;
        private final int ticketPoints;
        private final int pathBonus;
        private final int total;

        FinalScore(Player player, int routePoints, int ticketPoints, int pathBonus, int total) {
            this.player       = player;
            this.routePoints  = routePoints;
            this.ticketPoints = ticketPoints;
            this.pathBonus    = pathBonus;
            this.total        = total;
        }

        public Player getPlayer()      { return player; }
        public int routePoints()       { return routePoints; }
        public int ticketPoints()      { return ticketPoints; }
        public int pathBonus()         { return pathBonus; }
        public int total()             { return total; }
    }
}
