package actions;

import game.GameManager;
import game.ScoreManager;
import model.board.GameBoard;
import model.board.Route;
import model.cards.TransportationCard;
import model.deck.TrainCardDeck;
import model.player.Player;

import java.util.List;

/**
 * Command: claim a route by spending matching train cards.
 * Validation and execution fully encapsulated here — GameManager just calls execute().
 */
public class ClaimRouteAction extends PlayerAction {

    private final Route route;
    private final List<TransportationCard> cardsUsed;
    private final TrainCardDeck deck;
    private final ScoreManager scoreManager;

    /**
     * @param player       the acting player
     * @param route        the route to claim
     * @param cardsUsed    cards from the player's hand to spend
     * @param deck         discard target for the spent cards
     * @param scoreManager awards route points immediately on execute
     */
    public ClaimRouteAction(Player player, Route route, List<TransportationCard> cardsUsed,
                            TrainCardDeck deck, ScoreManager scoreManager) {
        super(player);
        this.route        = route;
        this.cardsUsed    = cardsUsed;
        this.deck         = deck;
        this.scoreManager = scoreManager;
    }

    /**
     * Static helper to find which cards from a player's hand should be used to claim a route,
     * given the player's choice of color/wild.
     * 
     * @param player the acting player
     * @param route  the route to claim
     * @param color  the color chosen by the player (matching route or wild)
     * @return the list of cards to use, or an empty list if insufficient cards
     */
    public static List<TransportationCard> getCardsToClaim(Player player, Route route, model.cards.TransportationTypes color) {
        List<TransportationCard> cardsToUse = new java.util.ArrayList<>();
        List<TransportationCard> hand = new java.util.ArrayList<>(player.getHand());

        // 1. Gather all matching colored cards first (unless WILD was selected)
        if (color != model.cards.TransportationTypes.LOCOMOTIVE) {
            var it = hand.iterator();
            while (it.hasNext() && cardsToUse.size() < route.getLength()) {
                TransportationCard c = it.next();
                if (!c.isLocomotive() && c.getColour() == color) {
                    cardsToUse.add(c);
                    it.remove();
                }
            }
        }

        // 2. Fill the rest with locomotives
        var it = hand.iterator();
        while (it.hasNext() && cardsToUse.size() < route.getLength()) {
            TransportationCard c = it.next();
            if (c.isLocomotive()) {
                cardsToUse.add(c);
                it.remove();
            }
        }

        if (cardsToUse.size() == route.getLength()) {
            return cardsToUse;
        }
        return List.of();
    }

    @Override
    public boolean validate() {
        if (route == null || route.isClaimed()) {
            return false;
        }

        // Double Route Rules
        GameBoard board = GameManager.getInstance().getBoard();
        if (board != null) {
            List<Route> routesBetween = board.getRoutes().stream()
                .filter(r -> (r.getCityA().equals(route.getCityA()) && r.getCityB().equals(route.getCityB())) ||
                             (r.getCityA().equals(route.getCityB()) && r.getCityB().equals(route.getCityA())))
                .toList();

            if (routesBetween.size() > 1) {
                int playerCount = GameManager.getInstance().getPlayers().size();
                for (Route r : routesBetween) {
                    if (r == route) continue;
                    if (r.isClaimed()) {
                        // Rule 1: A single player cannot claim both routes of a double route.
                        if (r.getClaimedBy().equals(player)) {
                            return false;
                        }
                        // Rule 2: In two-player games, once one Route of a Double Route is claimed,
                        // the other one cannot be claimed by the other player.
                        if (playerCount <= 2) {
                            return false;
                        }
                    }
                }
            }
        }

        if (!route.canBeClaimedWith(cardsUsed)) {
            return false;
        }
        if (!player.hasEnoughTrainPieces(route.getLength())) {
            return false;
        }
        if (!player.hasCards(cardsUsed)) {
            return false;
        }
        return true;
    }

    @Override
    public ActionResult execute() {
        route.assignTo(player);
        player.discard(cardsUsed);
        deck.discardAll(cardsUsed);
        player.placeTrainPieces(route.getLength());
        scoreManager.addRouteScore(player, route);

        int pts = route.getLength(); // approximate for message; actual added by scoreManager
        String colorName = route.getRequiredColour() != null ? route.getRequiredColour().displayName() + " " : "";
        String msg = String.format("Claimed %s%s → %s · +%d pts",
            colorName, route.getCityA(), route.getCityB(),
            new strategy.RouteScoreStrategy().scoreRoute(route));
        return ActionResult.success(msg);
    }

    /** @return the route being claimed */
    public Route getRoute() {
        return route;
    }
}
