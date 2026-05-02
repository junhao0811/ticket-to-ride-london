package model.player;

import model.cards.DestinationTicketCard;
import model.cards.TransportationCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player in the game.
 * Passive data holder — does not call Route, Deck, or BoardMap directly.
 * All cross-component logic flows through GameManager and the Command layer.
 */
public class Player {

    private final String name;
    private final PlayerColour colour;
    private final List<TransportationCard> hand;
    private final List<DestinationTicketCard> tickets;
    private int trainPieces;
    private final Score score;

    /**
     * @param name               player display name
     * @param colour             player token colour
     * @param initialTrainPieces number of train/bus pieces to start with
     */
    public Player(String name, PlayerColour colour, int initialTrainPieces) {
        this.name = name;
        this.colour = colour;
        this.hand = new ArrayList<>();
        this.tickets = new ArrayList<>();
        this.trainPieces = initialTrainPieces;
        this.score = new Score();
    }

    /** Adds a single card to the player's hand. */
    public void addCard(TransportationCard card) {
        if (card != null) {
            hand.add(card);
        }
    }

    /** Removes the specified cards from the player's hand (e.g. after claiming a route). */
    public void discard(List<TransportationCard> cards) {
        if (cards != null) {
            hand.removeAll(cards);
        }
    }

    /** Adds destination tickets to the player's ticket collection. */
    public void addTickets(List<DestinationTicketCard> kept) {
        if (kept != null) {
            tickets.addAll(kept);
        }
    }

    /** Deducts {@code count} train pieces when a route is claimed. */
    public void placeTrainPieces(int count) {
        trainPieces = Math.max(0, trainPieces - count);
    }

    /** @return true if the player has at least {@code cost} train pieces remaining */
    public boolean hasEnoughTrainPieces(int cost) {
        return trainPieces >= cost;
    }

    /** @return true if the player's hand contains all the given cards */
    public boolean hasCards(List<TransportationCard> cards) {
        if (cards == null) return false;
        List<TransportationCard> copy = new ArrayList<>(hand);
        for (TransportationCard c : cards) {
            if (!copy.remove(c)) return false;
        }
        return true;
    }

    /** @return unmodifiable view of the player's hand */
    public List<TransportationCard> getHand() {
        return Collections.unmodifiableList(hand);
    }

    /** @return unmodifiable view of the player's destination tickets */
    public List<DestinationTicketCard> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    /** @return the player's mutable Score object */
    public Score getScore() {
        return score;
    }

    /** @return number of train pieces remaining */
    public int getTrainPieces() {
        return trainPieces;
    }

    /** @return player display name */
    public String getName() {
        return name;
    }

    /** @return player token colour */
    public PlayerColour getColour() {
        return colour;
    }
}
