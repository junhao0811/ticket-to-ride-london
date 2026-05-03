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
            
            // Tally tickets and count completed ones
            int ticketPts = 0;
            int completedCount = 0;
            for (DestinationTicketCard ticket : p.getTickets()) {
                int score = strategy.scoreTicket(ticket, p, board);
                ticketPts += score;
                if (score > 0) completedCount++;
            }
            
            int pathBonus = strategy.scoreLongestPath(p, board);
            int pathDist = calculateLongestPathDistance(p, board);

            // Update player's actual score object for consistency
            p.getScore().addPoints(ticketPts);
            p.getScore().addPoints(pathBonus);

            results.add(new FinalScore(p, routePts, ticketPts, pathBonus, p.getScore().getPoints(), completedCount, pathDist));
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

    private int calculateLongestPathDistance(Player player, GameBoard board) {
        // We reuse the logic from LongestPathStrategy here or ideally would call it.
        // For now, let's keep it simple and robust by calculating it.
        List<Route> owned = new ArrayList<>();
        for (Route r : board.getRoutes()) {
            if (r.getClaimedBy() == player) owned.add(r);
        }
        if (owned.isEmpty()) return 0;

        int[] best = {0};
        Set<model.board.City> allCities = new HashSet<>();
        for (Route r : owned) {
            allCities.add(r.getCityA());
            allCities.add(r.getCityB());
        }
        
        Set<Route> visited = new HashSet<>();
        for (model.board.City start : allCities) {
            dfs(start, owned, visited, 0, best);
        }
        return best[0];
    }

    private void dfs(model.board.City city, List<Route> owned, Set<Route> visited, int length, int[] best) {
        best[0] = Math.max(best[0], length);
        for (Route r : owned) {
            if (visited.contains(r)) continue;
            model.board.City neighbour = null;
            if (r.getCityA().equals(city)) neighbour = r.getCityB();
            else if (r.getCityB().equals(city)) neighbour = r.getCityA();
            
            if (neighbour != null) {
                visited.add(r);
                dfs(neighbour, owned, visited, length + r.getLength(), best);
                visited.remove(r);
            }
        }
    }

    // ── Inner record for final scoring breakdown ──────────────────────────────

    /** Breakdown of a single player's final score. */
    public static class FinalScore {
        private final Player player;
        private final int routePoints;
        private final int ticketPoints;
        private final int pathBonus;
        private final int total;
        private final int completedTickets;
        private final int longestPathDistance;

        FinalScore(Player player, int routePoints, int ticketPoints, int pathBonus, int total, int completedTickets, int longestPathDistance) {
            this.player       = player;
            this.routePoints  = routePoints;
            this.ticketPoints = ticketPoints;
            this.pathBonus    = pathBonus;
            this.total        = total;
            this.completedTickets = completedTickets;
            this.longestPathDistance = longestPathDistance;
        }

        public Player getPlayer()             { return player; }
        public int routePoints()              { return routePoints; }
        public int ticketPoints()             { return ticketPoints; }
        public int pathBonus()                { return pathBonus; }
        public int total()                    { return total; }
        public int completedTickets()         { return completedTickets; }
        public int longestPathDistance()      { return longestPathDistance; }
    }
}
