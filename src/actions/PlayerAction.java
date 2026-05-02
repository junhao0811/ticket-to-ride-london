package actions;

import model.player.Player;

/**
 * Abstract Command base for all player turn actions.
 * Subclasses own their own validation logic — Template Method is NOT used here.
 * GameManager calls validate() then execute() exactly once per turn.
 */
public abstract class PlayerAction {

    protected final Player player;

    /** @param player the player executing this action */
    protected PlayerAction(Player player) {
        this.player = player;
    }

    /**
     * Returns true if this action is legal given the current game state.
     * Must not mutate any state.
     */
    public abstract boolean validate();

    /**
     * Executes the action and returns a result with a status message.
     * Called only after {@link #validate()} returns true.
     */
    public abstract ActionResult execute();

    /** @return the player who initiated this action */
    public Player getPlayer() {
        return player;
    }
}
