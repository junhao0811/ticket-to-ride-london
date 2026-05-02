package model.board;

import model.cards.CardColour;
import model.cards.TrainCard;
import model.player.Player;
import strategy.ClaimStrategy;
import java.util.List;

/**
 * A single route segment between two cities.
 * Claim validation is fully delegated to the injected {@link ClaimStrategy}.
 */
public class Route {

    private final String id;
    private final String cityA;
    private final String cityB;
    private final int length;
    /** Null means gray — any colour may be used. */
    private final CardColour requiredColour;
    private final ClaimStrategy claimStrategy;
    private Player claimedBy;
    private final List<SegmentPoint> segmentPoints;

    /**
     * @param id             unique route identifier (e.g. the config node_name)
     * @param cityA          name of the first endpoint city
     * @param cityB          name of the second endpoint city
     * @param length         number of train pieces required
     * @param requiredColour route colour; null = gray (any colour)
     * @param claimStrategy  validates whether a given set of cards may claim this route
     * @param segmentPoints  pixel positions of each train-car slot, in order
     */
    public Route(String id, String cityA, String cityB, int length,
                 CardColour requiredColour, ClaimStrategy claimStrategy,
                 List<SegmentPoint> segmentPoints) {
        this.id = id;
        this.cityA = cityA;
        this.cityB = cityB;
        this.length = length;
        this.requiredColour = requiredColour;
        this.claimStrategy = claimStrategy;
        this.claimedBy = null;
        this.segmentPoints = List.copyOf(segmentPoints);
    }

    /** @return true if a player has already claimed this route */
    public boolean isClaimed() {
        return claimedBy != null;
    }

    /**
     * Returns true if {@code cards} legally pay for this route.
     * Delegates entirely to the assigned {@link ClaimStrategy}.
     */
    public boolean canBeClaimedWith(List<TrainCard> cards) {
        return claimStrategy.canClaim(length, cards);
    }

    /**
     * Marks this route as owned by {@code player}.
     * Has no effect if the route is already claimed.
     */
    public void assignTo(Player player) {
        if (!isClaimed() && player != null) {
            this.claimedBy = player;
        }
    }

    /** @return the unique route id */
    public String getId() {
        return id;
    }

    /** @return name of the first endpoint city */
    public String getCityA() {
        return cityA;
    }

    /** @return name of the second endpoint city */
    public String getCityB() {
        return cityB;
    }

    /** @return number of train pieces this route costs */
    public int getLength() {
        return length;
    }

    /** @return required colour, or null for gray routes */
    public CardColour getRequiredColour() {
        return requiredColour;
    }

    /** @return the player who owns this route, or null if unclaimed */
    public Player getClaimedBy() {
        return claimedBy;
    }

    /** @return pixel positions of each train-car slot on this route, in order */
    public List<SegmentPoint> getSegmentPoints() {
        return segmentPoints;
    }
}
