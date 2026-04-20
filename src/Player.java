import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Player {
    private static final int STARTING_TRAIN_CARS = 17;

    private final String name;
    private final String color;
    private final List<TrainCard> hand;
    private final List<DestinationTicketCard> destinationTickets;
    private final Score score;
    private int remainingTrainCars;

    public Player(String name, String color) {
        this.name = name;
        this.color = color;
        this.hand = new ArrayList<>();
        this.destinationTickets = new ArrayList<>();
        this.score = new Score();
        this.remainingTrainCars = STARTING_TRAIN_CARS;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getRemainingTrainCars() {
        return remainingTrainCars;
    }

    public Score getScore() {
        return score;
    }

    public List<TrainCard> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public List<DestinationTicketCard> getDestinationTickets() {
        return Collections.unmodifiableList(destinationTickets);
    }

    public void addTrainCard(TrainCard card) {
        hand.add(card);
    }

    public void addTrainCards(List<TrainCard> cards) {
        hand.addAll(cards);
    }

    public boolean hasEnoughTrainCars(int amount) {
        return remainingTrainCars >= amount;
    }

    public boolean hasCards(List<TrainCard> cardsToCheck) {
        if (cardsToCheck == null) {
            return false;
        }

        List<TrainCard> handCopy = new ArrayList<>(hand);
        for (TrainCard card : cardsToCheck) {
            if (!handCopy.remove(card)) {
                return false;
            }
        }
        return true;
    }

    public boolean removeTrainCards(List<TrainCard> cardsToRemove) {
        if (!hasCards(cardsToRemove)) {
            return false;
        }

        for (TrainCard card : cardsToRemove) {
            hand.remove(card);
        }
        return true;
    }

    public boolean removeTrainCars(int amount) {
        if (!hasEnoughTrainCars(amount)) {
            return false;
        }

        remainingTrainCars -= amount;
        return true;
    }

    public void addDestinationTickets(List<DestinationTicketCard> ticketsToAdd) {
        destinationTickets.addAll(ticketsToAdd);
    }
}