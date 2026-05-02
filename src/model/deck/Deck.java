package model.deck;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base deck using the Template Method pattern.
 * Subclasses override {@link #onExhausted()} to supply reshuffle behaviour.
 */
public abstract class Deck<T> {

    protected final Deque<T> cards;

    protected Deck() {
        this.cards = new ArrayDeque<>();
    }

    /** Adds all items to the top of the deck (used by factory methods). */
    public void addAll(List<T> items) {
        if (items != null) {
            cards.addAll(items);
        }
    }

    /** Shuffles the deck in place. */
    public void shuffleCards() {
        List<T> list = new ArrayList<>(cards);
        Collections.shuffle(list);
        cards.clear();
        cards.addAll(list);
    }

    /**
     * Draws the top card, calling {@link #onExhausted()} if the deck is empty before drawing.
     *
     * @return the drawn card, or empty if no cards remain after reshuffle
     */
    public Optional<T> draw() {
        if (cards.isEmpty()) {
            onExhausted();
        }
        return cards.isEmpty() ? Optional.empty() : Optional.of(cards.poll());
    }

    /** Returns a card to the bottom of the deck. */
    public void returnToBottom(T card) {
        if (card != null) {
            cards.addLast(card);
        }
    }

    /** @return current number of cards in this deck */
    public int size() {
        return cards.size();
    }

    /** @return true if this deck has no cards */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Called by {@link #draw()} when the deck is exhausted.
     * Override in subclasses to reshuffle a discard pile.
     * Default: no-op (deck stays empty).
     */
    protected void onExhausted() {
        // no-op — subclasses override for reshuffle
    }
}
