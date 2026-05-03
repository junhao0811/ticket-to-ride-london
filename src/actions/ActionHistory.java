package actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Ordered log of executed player actions.
 * Populated by TurnManager after each successful execute().
 * Enables undo/replay in Sprint 3.
 */
public class ActionHistory {

    private final List<PlayerAction> history;

    public ActionHistory() {
        this.history = new ArrayList<>();
    }

    /** Records a successfully executed action. */
    public void record(PlayerAction action) {
        if (action != null) {
            history.add(action);
        }
    }

    /** @return the most recently recorded action, or empty if history is empty */
    public Optional<PlayerAction> last() {
        if (history.isEmpty()) return Optional.empty();
        return Optional.of(history.get(history.size() - 1));
    }

    /** @return unmodifiable view of the full action log */
    public List<PlayerAction> getAll() {
        return Collections.unmodifiableList(history);
    }

    /** @return number of recorded actions */
    public int size() {
        return history.size();
    }
}
