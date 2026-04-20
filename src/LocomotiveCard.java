public final class LocomotiveCard extends TrainCard {

    @Override
    public TrainTypes getColor() {
        return TrainTypes.LOCOMOTIVE;
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