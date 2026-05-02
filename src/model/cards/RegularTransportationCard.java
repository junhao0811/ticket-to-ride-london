package model.cards;

/**
 * A standard coloured train card.
 * Matches routes whose required colour equals this card's colour.
 * Locomotives are handled by {@link LocomotiveCard}.
 */
public class RegularTransportationCard extends TransportationCard {

    /** @param colour the card colour — must not be WILD */
    public RegularTransportationCard(TransportationTypes colour) {
        super(colour);
    }

    @Override
    public boolean isLocomotive() {
        return false;
    }

    /**
     * Matches when {@code required} is null (gray/any route) or equals this card's colour.
     */
    @Override
    public boolean matches(TransportationTypes required) {
        return required == null || required == getColour();
    }
    
    @Override
    public String toString() {
        return "RegularTransportationCard{" + "colour=" + getColour() + '}';
    }
}
