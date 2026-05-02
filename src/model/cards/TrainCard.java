package model.cards;

/**
 * Abstract base for all train cards.
 * Subclasses supply colour-matching behaviour polymorphically — no instanceof needed.
 */
public abstract class TrainCard {

    private final CardColour colour;

    /** @param colour the card's colour (WILD for locomotives) */
    protected TrainCard(CardColour colour) {
        this.colour = colour;
    }

    /** @return true if this card acts as a wild substitute for any colour */
    public abstract boolean isWild();

    /**
     * Returns true if this card satisfies a route slot that requires {@code required}.
     * Pass {@code null} for gray (any-colour) routes — concrete types handle that case.
     */
    public abstract boolean matches(CardColour required);

    /** @return the card's nominal colour */
    public CardColour getColour() {
        return colour;
    }
}
