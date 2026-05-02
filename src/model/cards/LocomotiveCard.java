package model.cards;

/**
 * Locomotive (wild) card — substitutes any colour on any route.
 * Special draw rule: taking a face-up locomotive counts as the entire draw for that turn.
 */
public class LocomotiveCard extends TrainCard {

    public LocomotiveCard() {
        super(CardColour.WILD);
    }

    @Override
    public boolean isWild() {
        return true;
    }

    /** Always matches regardless of the required colour. */
    @Override
    public boolean matches(CardColour required) {
        return true;
    }
}
