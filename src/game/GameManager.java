package game;

import actions.ActionHistory;
import actions.ActionResult;
import actions.PlayerAction;
import model.board.GameBoard;
import model.deck.DestinationTicketDeck;
import model.deck.FaceUpTrainCardPool;
import model.deck.TrainCardDeck;
import model.player.Player;
import variant.GameVariant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Singleton mediator for the game session.
 * Receives its GameVariant via init() — never instantiates a variant internally.
 * All UI → game communication goes through submitAction().
 */
public class GameManager {

    private static GameManager instance;

    private GameVariant variant;
    private GameBoard board;
    private List<Player> players;
    private TrainCardDeck trainDeck;
    private FaceUpTrainCardPool faceUpPool;
    private DestinationTicketDeck ticketDeck;
    private ScoreManager scoreManager;
    private TurnManager turnManager;
    private ActionHistory history;
    private final List<GameStateListener> listeners;
    private List<ScoreManager.FinalScore> finalScores;

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
     * Initialises the game session with the given variant and player list.
     * Builds the board, shuffles decks, deals initial hands and tickets,
     * and runs the locomotive-reset rule on the face-up pool.
     *
     * @param variant the game variant (e.g. LondonVariant)
     * @param players the ordered list of players (first player goes first)
     */
    public void init(GameVariant variant, List<Player> players) {
        this.variant      = variant;
        this.players      = new ArrayList<>(players);
        this.board        = new GameBoard(variant.getMapConfiguration());
        this.trainDeck    = variant.getMapConfiguration().buildTrainDeck();
        this.ticketDeck   = variant.getMapConfiguration().buildTicketDeck();
        this.faceUpPool   = new FaceUpTrainCardPool();
        this.scoreManager = new ScoreManager(variant.getScoring());
        this.history      = new ActionHistory();
        this.turnManager  = new TurnManager(this.players, this.history);
        this.finalScores  = null;

        dealInitialHands();
        faceUpPool.refill(trainDeck);
    }

    /**
     * Main entry point for UI actions.
     * Validates and executes the action; notifies listeners on success.
     *
     * @param action the action the current player wishes to take
     * @return result carrying success flag and a toast-ready message
     */
    public ActionResult submitAction(PlayerAction action) {
        ActionResult result = turnManager.playTurn(action);
        if (result.isSuccess() && turnManager.getPhase() == GamePhase.ENDED) {
            finalScores = scoreManager.finalizeScores(players, board);
        }
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

    // ── Getters (read-only, for UI panels) ───────────────────────────────────

    /** @return the active game board */
    public GameBoard getBoard() { return board; }

    /** @return all players in turn order */
    public List<Player> getPlayers() { return List.copyOf(players); }

    /** @return the player whose turn it currently is */
    public Player getCurrentPlayer() { return turnManager.currentPlayer(); }

    /** @return the current game phase */
    public GamePhase getPhase() { return turnManager.getPhase(); }

    /** @return the train card deck */
    public TrainCardDeck getTrainDeck() { return trainDeck; }

    /** @return the face-up card pool */
    public FaceUpTrainCardPool getFaceUpPool() { return faceUpPool; }

    /** @return the destination ticket deck */
    public DestinationTicketDeck getTicketDeck() { return ticketDeck; }

    /** @return the score manager */
    public ScoreManager getScoreManager() { return scoreManager; }

    /** @return final score breakdown, or empty list if game has not ended */
    public List<ScoreManager.FinalScore> getFinalScores() {
        return finalScores != null ? List.copyOf(finalScores) : List.of();
    }

    /** @return the active game variant */
    public GameVariant getVariant() { return variant; }

    // ── Setup helpers ────────────────────────────────────────────────────────

    private void dealInitialHands() {
        int handSize = variant.getInitialHandSize();
        // Initial pieces already handled in Player constructor (passed from Lobby)
        for (Player p : players) {
            for (int i = 0; i < handSize; i++) {
                trainDeck.draw().ifPresent(p::addCard);
            }
        }
    }

    private void dealInitialTickets() {
        int dealt   = variant.getInitialTicketsDealt();
        int minKeep = variant.getMinTicketsKeptInitial();
        for (Player p : players) {
            List<model.cards.DestinationTicketCard> tickets = ticketDeck.drawN(dealt);
            // Keep all for now — UI will show TicketKeepDialog on first interaction.
            // Player must discard down to minKeep; stub: keep all dealt.
            p.addTickets(tickets);
        }
    }
}
