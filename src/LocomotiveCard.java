public final class LocomotiveCard extends TrainCard {

    @Override
    public CardColor getColor() {
        return CardColor.LOCOMOTIVE;
    }

    @Override
    public boolean isLocomotive() {
        return true;
    }

    @Override
    public String toString() {
        return "LocomotiveCard";
    }
}