package game;

import actions.ActionHistory;
import actions.ActionResult;
import actions.PlayerAction;
import game.EndGame.DestinationTicketScorer;
import game.EndGame.EndGameScorer;
import game.EndGame.LongestPathBonusScorer;
import game.EndGame.PlayerGraphsBuilder;
import model.board.GameBoard;
import model.deck.DestinationTicketCardDeck;
import model.deck.FaceUpTrainCardPool;
import model.deck.TrainCardDeck;
import model.player.Player;
import variant.GameVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the state and logic for a single game match.
 * Discarded when returning to menu or starting a new game.
 */
public class GameController {
    
    private final GameVariant variant;
    private final GameBoard board;
    private final List<Player> players;
    private final TrainCardDeck trainDeck;
    private final FaceUpTrainCardPool faceUpPool;
    private final DestinationTicketCardDeck ticketDeck;
    private final ScoreManager scoreManager;
    private final TurnManager turnManager;
    private final ActionHistory history;
    private List<ScoreManager.FinalScore> finalScores;
    private EndGameScorer endGameScorer;
    
    public GameController(GameVariant variant, List<Player> players) {
        this.variant = variant;
        this.players = new ArrayList<>(players);
        this.board = new GameBoard(variant.getMapConfiguration());
        this.trainDeck = variant.getMapConfiguration().buildTrainDeck();
        this.ticketDeck = variant.getMapConfiguration().buildTicketDeck();
        this.faceUpPool = new FaceUpTrainCardPool();
        this.scoreManager = new ScoreManager(variant.getScoring());
        this.history = new ActionHistory();
        this.turnManager = new TurnManager(this.players, this.history);
        this.finalScores = null;
        
        this.endGameScorer = new EndGameScorer(new PlayerGraphsBuilder(), new DestinationTicketScorer(), new LongestPathBonusScorer());
        
        dealInitialHands();
        faceUpPool.refill(trainDeck);
    }
    
    public ActionResult submitAction(PlayerAction action) {
        ActionResult result = turnManager.playTurn(action);
        if (result.isSuccess() && turnManager.getPhase() == GamePhase.ENDED) {
            this.finalScores = scoreManager.finalizeScores(players, board);
            // We no longer need endGameScorer as scoreManager.finalizeScores handles it all
            // including tie-breaking data preparation.
        }
        return result;
    }
    
    private void dealInitialHands() {
        int handSize = variant.getInitialHandSize();
        for (Player p : players) {
            for (int i = 0; i < handSize; i++) {
                trainDeck.draw().ifPresent(p::addCard);
            }
        }
    }
    
    // Getters
    public GameBoard getBoard() { return board; }
    public List<Player> getPlayers() { return List.copyOf(players); }
    public Player getCurrentPlayer() { return turnManager.currentPlayer(); }
    public GamePhase getPhase() { return turnManager.getPhase(); }
    public TrainCardDeck getTrainDeck() { return trainDeck; }
    public FaceUpTrainCardPool getFaceUpPool() { return faceUpPool; }
    public DestinationTicketCardDeck getTicketDeck() { return ticketDeck; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public List<ScoreManager.FinalScore> getFinalScores() {
        return finalScores != null ? List.copyOf(finalScores) : List.of();
    }
    public GameVariant getVariant() { return variant; }
}