import java.util.List;
import java.util.Map;

public class EndGameScorer {


    private PlayerGraphsBuilder builder;
    private DestinationTicketScorer ticketScorer;
    private LongestPathBonusScorer longestPathBonusScorer;

    public EndGameScorer(PlayerGraphsBuilder builder, DestinationTicketScorer ticketScorer, LongestPathBonusScorer longestPathScorer){

        this.builder = builder;
        this.ticketScorer = ticketScorer;
        this.longestPathBonusScorer = longestPathScorer;
    }


    public void endScore(Game game){
        //build the player graphs
        Map<Player, MapGraph> playerGraphs = builder.buildPlayerGraphs(game.getPlayers(), game.getBoardGraph());

        // add up all the tickets scores and deduct for those uncompleted
        for (Player player : game.getPlayers()){
            int totalScore =  ticketScorer.calculateDestinationTickets(player, playerGraphs.get(player));
            player.addScore(totalScore);
        }

        //get the players who get the bonuses
        List<Player> winners = longestPathBonusScorer.findLongestPathWinners(playerGraphs);

        for (Player player: winners){
            player.addScore(longestPathBonusScorer.getBonusPoints());
        }
    }



}
