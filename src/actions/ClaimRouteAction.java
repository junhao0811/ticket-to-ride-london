package actions;

import game.ScoreManager;
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

    @Override
    public boolean validate() {
        if (route == null || route.isClaimed()) {
            return false;
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
