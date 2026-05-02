package actions;

import model.cards.TransportationCard;
import model.deck.FaceUpTrainCardPool;
import model.deck.TrainCardDeck;
import model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stateful command for the train-card draw turn.
 *
 * <p>The command owns the rulebook state machine. The UI only supplies the next
 * source: first card, optional second card, then completion.</p>
 */
public class DrawTrainCardAction extends PlayerAction {

    /** Describes where a single draw comes from. */
    public enum DrawSource {
        FACE_UP_0, FACE_UP_1, FACE_UP_2, FACE_UP_3, FACE_UP_4, BLIND_DECK
    }

    private enum DrawPhase {
        FIRST_PICK, SECOND_PICK, COMPLETE
    }

    private final DrawSource firstSource;
    private DrawSource secondSource;
    private final TrainCardDeck deck;
    private final FaceUpTrainCardPool pool;

    private DrawPhase phase = DrawPhase.FIRST_PICK;
    private final List<String> drawLog = new ArrayList<>();

    /**
     * @param player      the player drawing cards
     * @param firstSource where the first card comes from
     * @param deck        the train-card draw deck
     * @param pool        the five face-up card pool
     */
    public DrawTrainCardAction(Player player, DrawSource firstSource,
                               TrainCardDeck deck, FaceUpTrainCardPool pool) {
        super(player);
        this.firstSource  = firstSource;
        this.secondSource = null;
        this.deck         = deck;
        this.pool         = pool;
    }

    /**
     * Provides the phase-2 draw source.  Call this before re-submitting the action
     * after phase 1 returns {@link ActionResult#pending}.
     */
    public void setSecondSource(DrawSource source) {
        this.secondSource = source;
    }

    /**
     * @return true when phase 1 is complete and the player still owes a second draw
     */
    public boolean awaitingSecondDraw() {
        return phase == DrawPhase.SECOND_PICK;
    }

    /**
     * Used by the UI to disable illegal choices while preserving this pending action.
     */
    public boolean canUseSourceForCurrentPhase(DrawSource source) {
        if (phase == DrawPhase.COMPLETE) return false;
        return isSourceAvailable(source, phase);
    }

    // -- Validation ------------------------------------------------------------

    @Override
    public boolean validate() {
        if (phase == DrawPhase.COMPLETE) return false;
        if (!hasAnyLegalSourceFor(phase)) return false;

        DrawSource requested = phase == DrawPhase.FIRST_PICK ? firstSource : secondSource;
        return requested != null && isSourceAvailable(requested, phase);
    }

    // -- Execution -------------------------------------------------------------

    @Override
    public ActionResult execute() {
        return switch (phase) {
            case FIRST_PICK -> executeFirstPick();
            case SECOND_PICK -> executeSecondPick();
            case COMPLETE -> ActionResult.failure("Transport-card draw is already complete.");
        };
    }

    private ActionResult executeFirstPick() {
        Optional<TransportationCard> drawn = drawFrom(firstSource, DrawPhase.FIRST_PICK);
        if (drawn.isEmpty()) {
            return ActionResult.failure("No transport card available from that source.");
        }

        TransportationCard card = drawn.get();
        keepCard(firstSource, card);

        if (isFaceUpLocomotive(firstSource, card)) {
            phase = DrawPhase.COMPLETE;
            return ActionResult.success("Drew face-up Wild. Draw complete: " + formatLog());
        }

        if (!hasAnyLegalSourceFor(DrawPhase.SECOND_PICK)) {
            phase = DrawPhase.COMPLETE;
            return ActionResult.success("Drew 1 card. No legal second transport card is available: " + formatLog());
        }

        phase = DrawPhase.SECOND_PICK;
        return ActionResult.pending("Drew " + drawLog.get(0) + ". Select a second transport card.");
    }

    private ActionResult executeSecondPick() {
        Optional<TransportationCard> drawn = drawFrom(secondSource, DrawPhase.SECOND_PICK);
        if (drawn.isEmpty()) {
            return ActionResult.failure("That transport card cannot be taken as the second draw.");
        }

        keepCard(secondSource, drawn.get());
        phase = DrawPhase.COMPLETE;
        return ActionResult.success("Drew transport cards: " + formatLog());
    }

    // -- Helpers ---------------------------------------------------------------

    private Optional<TransportationCard> drawFrom(DrawSource source, DrawPhase requestedPhase) {
        if (!isSourceAvailable(source, requestedPhase)) return Optional.empty();
        if (source == DrawSource.BLIND_DECK) {
            return deck.draw();
        }

        return pool.take(slotIndex(source), deck);
    }

    private boolean isSourceAvailable(DrawSource source, DrawPhase requestedPhase) {
        if (source == null) return false;
        if (source == DrawSource.BLIND_DECK) {
            return !deck.isCompletelyEmpty();
        }

        int idx = slotIndex(source);
        List<TransportationCard> slots = pool.getSlots();
        if (idx >= slots.size()) return false;

        TransportationCard card = slots.get(idx);
        if (card == null) return false;
        return !(requestedPhase == DrawPhase.SECOND_PICK && card.isLocomotive());
    }

    private boolean hasAnyLegalSourceFor(DrawPhase requestedPhase) {
        for (DrawSource source : DrawSource.values()) {
            if (isSourceAvailable(source, requestedPhase)) return true;
        }
        return false;
    }

    private void keepCard(DrawSource source, TransportationCard card) {
        player.addCard(card);
        drawLog.add(describe(source, card));
    }

    private boolean isFaceUpLocomotive(DrawSource source, TransportationCard card) {
        return source != DrawSource.BLIND_DECK && card.isLocomotive();
    }

    private int slotIndex(DrawSource source) {
        return source.ordinal();
    }

    private String describe(DrawSource source, TransportationCard card) {
        String name = card.getColour().displayName();
        return source == DrawSource.BLIND_DECK ? "blind:" + name : name;
    }

    private String formatLog() {
        return String.join(", ", drawLog);
    }
}
