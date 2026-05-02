package game.EndGame;

import model.board.Route;
import model.player.MapGraph;
import model.player.Player;

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
        List<Player> players = null;
        List<Route> routes = null;
        Map<Player, MapGraph> playerGraphs = builder.buildPlayerGraphs(players, routes);

        // add up all the tickets scores and deduct for those uncompleted
        for (Player player : players)){
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
