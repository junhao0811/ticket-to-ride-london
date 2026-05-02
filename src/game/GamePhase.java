package game;

/** Represents the current phase of a game session. */
public enum GamePhase {
    /** Dealing cards and tickets before first turn. */
    SETUP,
    /** Normal play: players take turns. */
    PLAYING,
    /** Triggered when a player has ≤ 2 trains remaining. Everyone gets one more turn. */
    FINAL_ROUND,
    /** All turns done — proceed to final scoring. */
    ENDED
}
