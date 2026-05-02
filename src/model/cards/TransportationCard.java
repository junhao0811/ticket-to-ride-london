package model.cards;

/**
 * Abstract base for all train cards.
 * Subclasses supply colour-matching behaviour polymorphically — no instanceof needed.
 */
public abstract class TransportationCard {

    private final TransportationTypes colour;

    /** @param colour the card's colour (WILD for locomotives) */
    protected TransportationCard(TransportationTypes colour) {
        this.colour = colour;
    }

    /** @return true if this card acts as a wild substitute for any colour */
    public abstract boolean isLocomotive();

    /**
     * Returns true if this card satisfies a route slot that requires {@code required}.
     * Pass {@code null} for gray (any-colour) routes — concrete types handle that case.
     */
    public abstract boolean matches(TransportationTypes required);

    /** @return the card's nominal colour */
    public TransportationTypes getColour() {
        return colour;
    }
}
