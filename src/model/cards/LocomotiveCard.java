package model.cards;

/**
 * Locomotive (wild) card — substitutes any colour on any route.
 * Special draw rule: taking a face-up locomotive counts as the entire draw for that turn.
 */
public final class LocomotiveCard extends TransportationCard {

    public LocomotiveCard() {
        super(TransportationTypes.LOCOMOTIVE);
    }

    @Override
    public boolean isLocomotive() {
        return true;
    }

    /** Always matches regardless of the required colour. */
    @Override
    public boolean matches(TransportationTypes required) {
        return true;
    }
    
    @Override
    public String toString() {
        return "LocomotiveCard";
    }
}
