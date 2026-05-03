package strategy;

import model.board.City;
import model.board.GameBoard;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.Player;

import java.util.*;

/**
 * Finds the longest continuous path for a player using DFS + backtracking.
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
     * otherwise 0.  The comparison is performed across all players found via the
     * board's claimed routes — callers must invoke this for each player separately.
     */
    @Override
    public int scoreLongestPath(Player player, GameBoard board) {
        // Collect all unique players who have claimed at least one route.
        Set<Player> allPlayers = new HashSet<>();
        for (Route r : board.getRoutes()) {
            if (r.getClaimedBy() != null) allPlayers.add(r.getClaimedBy());
        }
        int myPath = longestPath(player, board);
        int maxOther = 0;
        for (Player other : allPlayers) {
            if (other != player) {
                maxOther = Math.max(maxOther, longestPath(other, board));
            }
        }
        return myPath >= maxOther && myPath > 0 ? LONGEST_PATH_BONUS : 0;
    }

    // ── DFS longest-path calculation ─────────────────────────────────────────

    private int longestPath(Player player, GameBoard board) {
        List<Route> owned = new ArrayList<>();
        for (Route r : board.getRoutes()) {
            if (r.getClaimedBy() == player) owned.add(r);
        }
        if (owned.isEmpty()) return 0;

        int[] best = {0};
        Set<City> allCities = collectCities(owned);
        Set<Route> visited = new HashSet<>();

        for (City start : allCities) {
            dfs(start, owned, visited, 0, best);
        }
        return best[0];
    }

    private void dfs(City city, List<Route> owned, Set<Route> visited, int length, int[] best) {
        best[0] = Math.max(best[0], length);
        for (Route r : owned) {
            if (visited.contains(r)) continue;
            City neighbour = getNeighbour(r, city);
            if (neighbour == null) continue;
            visited.add(r);
            dfs(neighbour, owned, visited, length + r.getLength(), best);
            visited.remove(r);
        }
    }

    private Set<City> collectCities(List<Route> routes) {
        Set<City> cities = new HashSet<>();
        for (Route r : routes) {
            cities.add(r.getCityA());
            cities.add(r.getCityB());
        }
        return cities;
    }

    private City getNeighbour(Route route, City city) {
        if (route.getCityA().equals(city)) return route.getCityB();
        if (route.getCityB().equals(city)) return route.getCityA();
        return null;
    }
}
