package strategy;

import model.cards.TransportationCard;
import java.util.List;

/**
 * Strategy interface for route-claim validation.
 * Two concrete implementations: SpecificColourStrategy (coloured routes)
 * and AnyColourStrategy (gray routes).
 */
public interface ClaimStrategy {

    /**
     * Returns true if {@code cards} legally satisfy this route's cost.
     *
     * @param requiredLength number of train pieces the route costs
     * @param cards          cards the player proposes to spend
     * @return true if the claim is valid
     */
    boolean canClaim(int requiredLength, List<TransportationCard> cards);
}
