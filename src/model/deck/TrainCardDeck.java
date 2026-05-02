package model.deck;

import model.cards.TransportationCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Deck of train cards. Maintains a discard pile that is reshuffled
 * back into the draw pile when the deck is exhausted.
 */
public class TrainCardDeck extends Deck<TransportationCard> {

    private final List<TransportationCard> discardPile;

    public TrainCardDeck() {
        this.discardPile = new ArrayList<>();
    }

    /** Adds a card to the discard pile. */
    public void discard(TransportationCard card) {
        if (card != null) {
            discardPile.add(card);
        }
    }

    /** Discards a list of cards (e.g. after a route claim). */
    public void discardAll(List<TransportationCard> cardList) {
        if (cardList != null) {
            discardPile.addAll(cardList);
        }
    }

    /** @return number of cards in the discard pile */
    public int discardSize() {
        return discardPile.size();
    }

    /** @return true if both draw and discard piles are empty */
    public boolean isCompletelyEmpty() {
        return cards.isEmpty() && discardPile.isEmpty();
    }

    /** Reshuffles the discard pile into a new draw pile. */
    public void reshuffleDiscards() {
        addAll(discardPile);
        discardPile.clear();
        shuffleCards();
    }

    @Override
    protected void onExhausted() {
        if (!discardPile.isEmpty()) {
            reshuffleDiscards();
        }
    }

    /**
     * Draws the top card. Returns empty only if both draw and discard piles are empty.
     */
    @Override
    public Optional<TransportationCard> draw() {
        return super.draw();
    }
}
