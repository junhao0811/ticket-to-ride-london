package model.cards;

/**
 * A standard coloured train card.
 * Matches routes whose required colour equals this card's colour.
 * Locomotives are handled by {@link LocomotiveCard}.
 */
public class RegularTrainCard extends TrainCard {

    /** @param colour the card colour — must not be WILD */
    public RegularTrainCard(CardColour colour) {
        super(colour);
    }

    @Override
    public boolean isWild() {
        return false;
    }

    /**
     * Matches when {@code required} is null (gray/any route) or equals this card's colour.
     */
    @Override
    public boolean matches(CardColour required) {
        return required == null || required == getColour();
    }
}
