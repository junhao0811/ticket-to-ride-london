package game;

/**
 * Observer interface — UI panels implement this to receive state-change notifications.
 * Called by GameManager after every successful action.
 */
public interface GameStateListener {

    /** Invoked whenever game state changes (after any action, after phase transition). */
    void onStateChanged();
}
