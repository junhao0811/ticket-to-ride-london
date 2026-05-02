package model.deck;

import model.cards.TransportationCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The 5 face-up train cards visible to all players.
 * Uses composition — does NOT extend Deck.
 * Enforces the locomotive-reset rule: if 3+ slots are locomotives, discard all and redraw.
 */
public class FaceUpTrainCardPool {

    private static final int POOL_SIZE = 5;
    private static final int LOCO_RESET_LIMIT = 3;

    private final List<TransportationCard> slots;
    private boolean lastTakenWasLocomotive;

    public FaceUpTrainCardPool() {
        this.slots = new ArrayList<>(Collections.nCopies(POOL_SIZE, null));
        this.lastTakenWasLocomotive = false;
    }

    /**
     * Fills all empty slots from the deck, then applies the locomotive-reset rule.
     * Should be called after setup and after each card is taken.
     */
    public void refill(TrainCardDeck deck) {
        fillEmpties(deck);
        enforceLocomotiveReset(deck);
    }

    /**
     * Takes the card at {@code index} and immediately refills from the deck.
     *
     * @return the taken card, or empty if the slot was null
     */
    public Optional<TransportationCard> take(int index, TrainCardDeck deck) {
        if (index < 0 || index >= POOL_SIZE) {
            return Optional.empty();
        }
        TransportationCard taken = slots.get(index);
        if (taken == null) {
            return Optional.empty();
        }
        lastTakenWasLocomotive = taken.isLocomotive();
        slots.set(index, null);
        refill(deck);
        return Optional.of(taken);
    }

    /** @return true if the last card taken from the pool was a locomotive */
    public boolean lastTakenWasLocomotive() {
        return lastTakenWasLocomotive;
    }

    /** @return unmodifiable snapshot of the 5 slots (null = empty) */
    public List<TransportationCard> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private void fillEmpties(TrainCardDeck deck) {
        for (int i = 0; i < POOL_SIZE; i++) {
            if (slots.get(i) == null) {
                deck.draw().ifPresent(c -> {
                    int firstNull = slots.indexOf(null);
                    if (firstNull >= 0) slots.set(firstNull, c);
                });
            }
        }
    }

    private void enforceLocomotiveReset(TrainCardDeck deck) {
        int attempts = 0;
        while (locomotiveCount() >= LOCO_RESET_LIMIT && attempts < 10) {
            for (TransportationCard card : slots) {
                if (card != null) {
                    deck.discard(card);
                }
            }
            Collections.fill(slots, null);
            fillEmpties(deck);
            attempts++;
        }
    }

    private int locomotiveCount() {
        int count = 0;
        for (TransportationCard c : slots) {
            if (c != null && c.isLocomotive()) count++;
        }
        return count;
    }
}
