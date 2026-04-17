public final class RegularTrainCard extends TrainCard {
    private final CardColor color;

    public RegularTrainCard(CardColor color) {
        this.color = color;
    }

    @Override
    public CardColor getColor() {
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