package actions;

import model.player.Player;

/**
 * Non-move action — does not mutate GameBoard, Player hand, or Score.
 * Used to pass turn without performing a game action (e.g. a player cannot move).
 */
public class PauseAction extends PlayerAction {

    public PauseAction(Player player) {
        super(player);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public ActionResult execute() {
        return ActionResult.success("Turn passed.");
    }
}
