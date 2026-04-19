import java.util.List;

public final class Route {
    private final City firstCity;
    private final City secondCity;
    private final int length;
    private final CardColor requiredColor;
    private Player owner;

    public Route(City firstCity, City secondCity, int length, CardColor requiredColor) {
        this.firstCity = firstCity;
        this.secondCity = secondCity;
        this.length = length;
        this.requiredColor = requiredColor;
    }

    public City getFirstCity() {
        return firstCity;
    }

    public City getSecondCity() {
        return secondCity;
    }

    public int getLength() {
        return length;
    }

    public CardColor getRequiredColor() {
        return requiredColor;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isClaimed() {
        return owner != null;
    }

    public void claimBy(Player player) {
        owner = player;
    }

    public boolean canBeClaimedWith(List<TrainCard> cards) {
        if (cards == null || cards.size() != length) {
            return false;
        }

        if (requiredColor != CardColor.GREY) {
            return matchesColoredRoute(cards);
        }

        return matchesGreyRoute(cards);
    }

    private boolean matchesColoredRoute(List<TrainCard> cards) {
        for (TrainCard card : cards) {
            if (!card.isLocomotive() && card.getColor() != requiredColor) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesGreyRoute(List<TrainCard> cards) {
        CardColor chosenColor = null;

        for (TrainCard card : cards) {
            if (card.isLocomotive()) {
                continue;
            }

            if (chosenColor == null) {
                chosenColor = card.getColor();
            } else if (card.getColor() != chosenColor) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Route{"
                + "firstCity="
                + firstCity
                + ", secondCity="
                + secondCity
                + ", length="
                + length
                + ", requiredColor="
                + requiredColor
                + '}';
    }
}