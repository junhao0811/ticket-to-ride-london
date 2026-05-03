package game;

import actions.ActionHistory;
import actions.ActionResult;
import actions.PlayerAction;
import model.player.Player;

import java.util.List;

/**
 * Enforces the turn skeleton via Template Method:
 * start → validate → execute → record → check end → pass turn.
 *
 * Move validity lives inside PlayerAction.validate() — never here.
 */
public class TurnManager {

    private static final int FINAL_ROUND_TRAIN_THRESHOLD = 2;

    private final List<Player> players;
    private final ActionHistory history;
    private int currentIdx;
    private GamePhase phase;
    private int finalRoundTurnsLeft;

    /**
     * @param players ordered list of players
     * @param history shared action log
     */
    public TurnManager(List<Player> players, ActionHistory history) {
        this.players             = players;
        this.history             = history;
        this.currentIdx          = 0;
        this.phase               = GamePhase.PLAYING;
        this.finalRoundTurnsLeft = 0;
    }

    /**
     * Processes one player action through the full turn skeleton.
     *
     * @param action the action the current player wishes to execute
     * @return the ActionResult from execute(), or a failure if validation failed
     */
    public ActionResult playTurn(PlayerAction action) {
        if (phase == GamePhase.ENDED) {
            return ActionResult.failure("Game is over.");
        }
        if (!action.validate()) {
            return ActionResult.failure("Action is invalid!");
        }
        ActionResult result = action.execute();
        if (result.isSuccess()) {
            history.record(action);
            checkEndCondition(action.getPlayer());
            if (phase != GamePhase.ENDED) {
                advanceTurn();
            }
        }
        return result;
    }

    /** @return the player whose turn it currently is */
    public Player currentPlayer() {
        return players.get(currentIdx);
    }

    /** @return the current game phase */
    public GamePhase getPhase() {
        return phase;
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private void checkEndCondition(Player justPlayed) {
        if (phase == GamePhase.PLAYING) {
            // Only evaluate end-of-game at the end of a full round
            if (currentIdx == players.size() - 1) {
                boolean thresholdMet = false;
                for (Player p : players) {
                    if (p.getTrainPieces() <= FINAL_ROUND_TRAIN_THRESHOLD) {
                        thresholdMet = true;
                        break;
                    }
                }
                
                if (thresholdMet) {
                    phase = GamePhase.FINAL_ROUND;
                    finalRoundTurnsLeft = players.size();
                }
            }
        } else if (phase == GamePhase.FINAL_ROUND) {
            finalRoundTurnsLeft--;
            if (finalRoundTurnsLeft <= 0) {
                phase = GamePhase.ENDED;
            }
        }
    }

    private void advanceTurn() {
        currentIdx = (currentIdx + 1) % players.size();
    }
}
