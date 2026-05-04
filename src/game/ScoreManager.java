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
     * @param player the player whose tickets to check
     * @param board  the current game board
     * @return number of completed destination tickets held by the player
     */
    public int getCompletedTicketCount(Player player, GameBoard board) {
        int count = 0;
        for (DestinationTicketCard t : player.getTickets()) {
            if (strategy.scoreTicket(t, player, board) > 0) {
                count++;
            }
        }
        return count;
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
            
            // Tally tickets and count completed/incomplete ones separately for breakdown
            int ticketPts = 0;
            int completedCount = 0;
            int incompleteCount = 0;
            int completedPts = 0;
            int incompletePts = 0;

            for (DestinationTicketCard ticket : p.getTickets()) {
                int score = strategy.scoreTicket(ticket, p, board);
                ticketPts += score;
                if (score > 0) {
                    completedCount++;
                    completedPts += score;
                } else {
                    incompleteCount++;
                    incompletePts += score;
                }
            }
            
            int pathBonus = strategy.scoreLongestPath(p, board);
            int pathDist = strategy.getLongestPathDistance(p, board);

            // Update player's actual score object for consistency
            p.getScore().addPoints(ticketPts);
            p.getScore().addPoints(pathBonus);

            results.add(new FinalScore(
                p, routePts, ticketPts, completedPts, incompletePts,
                pathBonus, p.getScore().getPoints(), completedCount, incompleteCount, pathDist));
        }

        // Tie-breaking rules:
        // 1. Most points
        // 2. Most completed tickets
        // 3. Longest continuous path distance
        results.sort((a, b) -> {
            if (a.total() != b.total()) return b.total() - a.total();
            if (a.completedTickets() != b.completedTickets()) return b.completedTickets() - a.completedTickets();
            return b.longestPathDistance() - a.longestPathDistance();
        });
        
        return results;
    }

    // ── Inner record for final scoring breakdown ──────────────────────────────

    /** Breakdown of a single player's final score. */
    public static class FinalScore {
        private final Player player;
        private final int routePoints;
        private final int ticketPoints;
        private final int completedPoints;
        private final int incompletePoints;
        private final int pathBonus;
        private final int total;
        private final int completedTickets;
        private final int incompleteTickets;
        private final int longestPathDistance;

        FinalScore(Player player, int routePoints, int ticketPoints, int completedPoints, int incompletePoints,
                   int pathBonus, int total, int completedTickets, int incompleteTickets, int longestPathDistance) {
            this.player       = player;
            this.routePoints  = routePoints;
            this.ticketPoints = ticketPoints;
            this.completedPoints = completedPoints;
            this.incompletePoints = incompletePoints;
            this.pathBonus    = pathBonus;
            this.total        = total;
            this.completedTickets = completedTickets;
            this.incompleteTickets = incompleteTickets;
            this.longestPathDistance = longestPathDistance;
        }

        public Player getPlayer()             { return player; }
        public int routePoints()              { return routePoints; }
        public int ticketPoints()             { return ticketPoints; }
        public int completedPoints()          { return completedPoints; }
        public int incompletePoints()         { return incompletePoints; }
        public int pathBonus()                { return pathBonus; }
        public int total()                    { return total; }
        public int completedTickets()         { return completedTickets; }
        public int incompleteTickets()        { return incompleteTickets; }
        public int longestPathDistance()      { return longestPathDistance; }
    }
}
