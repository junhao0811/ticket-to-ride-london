package strategy;

import model.cards.TransportationTypes;
import model.cards.TransportationCard;
import java.util.List;

/**
 * Validates claims on gray (any-colour) routes.
 * All non-locomotive cards must share the same colour; locomotives substitute freely.
 * A hand of all locomotives is also valid.
 */
public class AnyColourStrategy implements ClaimStrategy {

    @Override
    public boolean canClaim(int requiredLength, List<TransportationCard> cards) {
        if (cards == null || cards.size() != requiredLength) {
            return false;
        }
        // Determine the dominant colour from the first non-wild card.
        TransportationTypes chosen = null;
        for (TransportationCard card : cards) {
            if (!card.isLocomotive()) {
                chosen = card.getColour();
                break;
            }
        }
        // All non-wild cards must share that colour.
        for (TransportationCard card : cards) {
            if (!card.isLocomotive() && card.getColour() != chosen) {
                return false;
            }
        }
        return true;
    }
}
