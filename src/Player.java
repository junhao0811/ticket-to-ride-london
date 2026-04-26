import java.util.List;
import java.util.ArrayList;

public class Player {
    private List<TrainCard> trainCards;
    private List<DestinationTicketCard> destinationTicketCards;
    private int coloredTrainCars = 45;
    private int score = 0;

    public Player() {
        this.trainCards = new ArrayList<>();
        this.destinationTicketCards = new ArrayList<>();
    }

    public String chooseAction(BoardMap gameBoard) {
        if (gameBoard == null) {
            return "DRAW_CARDS";
        }
        return "CLAIM_ROUTE";
    }

    public void drawCards(TrainCard trainCard) {
        if (trainCard != null) {
            trainCards.add(trainCard);
        }
    }

    public boolean claimRoute(Route route, TrainCard trainCard, int trainCarsToSpend) {
        if (route == null || route.isClaimed()) {
            return false;
        }
        if (!hasEnoughTrains(trainCarsToSpend) || !route.validateTrainCards(trainCard)) {
            return false;
        }
        removeTrains(trainCarsToSpend);
        route.assignOwnership(this);
        addScore(route.getLength());
        return true;
    }

    public void drawTicket(DestinationTicketCard destinationTicketCard) {
        if (destinationTicketCard != null) {
            destinationTicketCards.add(destinationTicketCard);
        }
    }

    public boolean hasRequiredCards(List<TrainCard> cards) {
        return cards != null && trainCards.containsAll(cards);
    }

    public boolean hasEnoughTrains(int cost) {
        return coloredTrainCars >= cost;
    }

    public void discardCards(List<TrainCard> cards) {
        if (cards != null) {
            trainCards.removeAll(cards);
        }
    }

    public void removeTrains(int count) {
        this.coloredTrainCars = Math.max(0, this.coloredTrainCars - count);
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void addTickets(List<DestinationTicketCard> kept) {
        if (kept != null) {
            destinationTicketCards.addAll(kept);
        }
    }

    public int getTrainCount() {
        return coloredTrainCars;
    }

    public int getScore() {
        return score;
    }
}

