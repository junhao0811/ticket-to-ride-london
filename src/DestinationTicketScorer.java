public class DestinationTicketScorer {


    public int calculateDestinationTickets(Player player, MapGraph playerGraph) {
            int totalScore = 0;
            for (DestinationTicketCard ticket : player.getTicketCards()) {
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


