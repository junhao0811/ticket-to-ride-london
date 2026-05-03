package strategy;

import model.cards.TransportationTypes;
import model.cards.TransportationCard;
import java.util.List;

/**
 * Validates claims on coloured routes.
 * Cards must match the route's required colour; locomotives substitute freely.
 */
public class SpecificColourStrategy implements ClaimStrategy {

    private final TransportationTypes requiredColour;

    /** @param requiredColour the colour this route demands */
    public SpecificColourStrategy(TransportationTypes requiredColour) {
        this.requiredColour = requiredColour;
    }

    @Override
    public boolean canClaim(int requiredLength, List<TransportationCard> cards) {
        if (cards == null || cards.size() != requiredLength) {
            return false;
        }
        for (TransportationCard card : cards) {
            if (!card.matches(requiredColour)) {
                return false;
            }
        }
        return true;
    }
}
