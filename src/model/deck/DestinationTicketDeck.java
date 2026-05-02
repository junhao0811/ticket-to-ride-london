package model.deck;

import model.cards.DestinationTicketCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Deck of destination ticket cards.
 * Does not reshuffle on exhaustion — tickets are finite.
 */
public class DestinationTicketDeck extends Deck<DestinationTicketCard> {

    public DestinationTicketDeck() {
        super();
    }

    /**
     * Draws up to {@code count} tickets from the top.
     * Returns fewer if the deck has fewer than {@code count} cards remaining.
     */
    public List<DestinationTicketCard> drawN(int count) {
        List<DestinationTicketCard> drawn = new ArrayList<>();
        int limit = Math.min(count, cards.size());
        for (int i = 0; i < limit; i++) {
            draw().ifPresent(drawn::add);
        }
        return drawn;
    }

    /** Returns unkept tickets to the bottom of the deck. */
    public void returnToBottom(List<DestinationTicketCard> returned) {
        if (returned != null) {
            returned.forEach(this::returnToBottom);
        }
    }
}
