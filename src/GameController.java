public class GameController {

    private final Game game;
    private final EndGameScorer endGameScorer;

    public GameController(Game game, EndGameScorer endGameScorer) {
        this.game = game;
        this.endGameScorer = endGameScorer;
    }

    /*

    ***JUST A PLACE HOLDER FOR REAL GAME LOOP

     */
    public void runGameLoop() {
        while (!game.detectEnd()) {
            // player takes turn

            if (game.detectEnd()) {
                // handle final round first
                game.startFinalRound();

            }

            if (game.finalRoundComplete()){
                endGameScorer.endScore(game);
                game.markEnded();
            }
        }
    }

}
