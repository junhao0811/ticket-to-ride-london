package game.EndGame;

import model.cards.DestinationTicketCard;
import model.player.MapGraph;
import model.player.Player;

public class DestinationTicketScorer {


    public int calculateDestinationTickets(Player player, MapGraph playerGraph) {
            int totalScore = 0;
            for (DestinationTicketCard ticket : player.getTickets()) {
                boolean isCompleted = playerGraph.pathExists(ticket.getCityA(), ticket.getCityB());
                if (isCompleted) {
                    totalScore = totalScore + ticket.getPointValue();
                } else {
                    totalScore = totalScore - ticket.getPointValue();
                }
            }
            return totalScore;
        }
    }


