public final class RegularTrainCard extends TrainCard {
    private final TrainTypes color;

    public RegularTrainCard(TrainTypes color) {
        this.color = color;
    }

    @Override
    public TrainTypes getColor() {
        return color;
    }

    @Override
    public boolean isLocomotive() {
        return false;
    }

    @Override
    public String toString() {
        return "RegularTrainCard{" + "color=" + color + '}';
    }
}