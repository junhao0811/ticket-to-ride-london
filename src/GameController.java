import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean actionUsed;

    public GameController(List<Player> players) {
        this.players = new ArrayList<>(players);
        this.currentPlayerIndex = 0;
        this.actionUsed = false;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public void startTurn() {
        actionUsed = false;
    }

    public boolean canPlayerAct(Player player) {
        return !actionUsed && getCurrentPlayer() == player;
    }

    public void markActionUsed() {
        actionUsed = true;
    }

    public boolean hasActionBeenUsed() {
        return actionUsed;
    }

    public void endTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        actionUsed = false;
    }
}