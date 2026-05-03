package strategy;

import model.board.City;
import model.board.GameBoard;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.Player;

import java.util.*;

/**
 * Awards or deducts points for destination tickets.
 * Uses BFS over the player's claimed routes to check city connectivity.
 */
public class TicketScoreStrategy implements ScoringStrategy {

    @Override
    public int scoreRoute(Route route) {
        return 0; // delegated to RouteScoreStrategy
    }

    @Override
    public int scoreTicket(DestinationTicketCard ticket, Player player, GameBoard board) {
        boolean connected = areCitiesConnected(ticket.getCityA(), ticket.getCityB(), player, board);
        return connected ? ticket.getPointValue() : -ticket.getPointValue();
    }

    @Override
    public int scoreLongestPath(Player player, GameBoard board) {
        return 0; // delegated to LongestPathStrategy
    }

    // ── BFS connectivity check ────────────────────────────────────────────────

    private boolean areCitiesConnected(City cityA, City cityB, Player player, GameBoard board) {
        Set<City> visited = new HashSet<>();
        Queue<City> queue = new LinkedList<>();
        queue.add(cityA);
        visited.add(cityA);

        while (!queue.isEmpty()) {
            City current = queue.poll();
            if (current.equals(cityB)) {
                return true;
            }
            for (Route route : board.getRoutes()) {
                if (route.getClaimedBy() != player) continue;
                City neighbour = getNeighbour(route, current);
                if (neighbour != null && !visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }
        return false;
    }

    private City getNeighbour(Route route, City city) {
        if (route.getCityA().equals(city)) return route.getCityB();
        if (route.getCityB().equals(city)) return route.getCityA();
        return null;
    }
}
