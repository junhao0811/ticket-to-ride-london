package game;

import actions.ActionResult;
import actions.PlayerAction;
import model.board.GameBoard;
import model.deck.DestinationTicketCardDeck;
import model.deck.FaceUpTrainCardPool;
import model.deck.TrainCardDeck;
import model.player.Player;
import variant.GameVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton mediator for the application.
 * Manages the lifecycle of the GameSession and persists across match restarts.
 */
public class GameManager {

    private static GameManager instance;
    private GameController currentController;
    private final List<GameStateListener> listeners;

    private GameManager() {
        this.listeners = new ArrayList<>();
    }

    /** @return the single instance of GameManager */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Initialises a new game session.
     * Any existing session is discarded.
     */
    public void init(GameVariant variant, List<Player> players) {
        this.currentController = new GameController(variant, players);
        notifyListeners();
    }

    /**
     * Main entry point for UI actions.
     * Delegates to the current session.
     */
    public ActionResult submitAction(PlayerAction action) {
        if (currentController == null) {
            return ActionResult.failure("No active game session.");
        }
        ActionResult result = currentController.submitAction(action);
        notifyListeners();
        return result;
    }

    // ── Observers ────────────────────────────────────────────────────────────

    /** Registers a UI panel to receive state-change notifications. */
    public void addListener(GameStateListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    private void notifyListeners() {
        for (GameStateListener l : listeners) {
            l.onStateChanged();
        }
    }

    // ── Session Accessors (Delegated) ────────────────────────────────────────

    public GameBoard getBoard() {
        return currentController != null ? currentController.getBoard() : null;
    }

    public List<Player> getPlayers() {
        return currentController != null ? currentController.getPlayers() : List.of();
    }

    public Player getCurrentPlayer() {
        return currentController != null ? currentController.getCurrentPlayer() : null;
    }

    public GamePhase getPhase() {
        return currentController != null ? currentController.getPhase() : null;
    }

    public TrainCardDeck getTrainDeck() {
        return currentController != null ? currentController.getTrainDeck() : null;
    }

    public FaceUpTrainCardPool getFaceUpPool() {
        return currentController != null ? currentController.getFaceUpPool() : null;
    }

    public DestinationTicketCardDeck getTicketDeck() {
        return currentController != null ? currentController.getTicketDeck() : null;
    }

    public ScoreManager getScoreManager() {
        return currentController != null ? currentController.getScoreManager() : null;
    }

    public List<ScoreManager.FinalScore> getFinalScores() {
        return currentController != null ? currentController.getFinalScores() : List.of();
    }

    public GameVariant getVariant() {
        return currentController != null ? currentController.getVariant() : null;
    }
}
