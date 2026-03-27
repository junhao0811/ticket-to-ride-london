import java.util.List;

public class Player {
    private int trainCarCount = 45;
    private int score = 0;

    public boolean hasRequiredCards(List<TrainCard> cards) {
        // Validate player hand contains the cards
        return true;
    }

    public boolean hasEnoughTrains(int cost) {
        return trainCarCount >= cost;
    }

    public void discardCards(List<TrainCard> cards) {
        // Remove from hand, send to discard pile
    }

    public void removeTrains(int count) {
        this.trainCarCount -= count;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void addTickets(List<DestinationTicketCard> kept) {
        // Add tickets to player's destination tickets list
    }

    public int getTrainCount() {
        return trainCarCount;
    }
}

