import java.util.ArrayList;
import java.util.List;

public final class DrawDestinationTicketAction {
    private static final int DRAW_COUNT = 3;

    private String lastMessage;
    private List<DestinationTicketCard> lastDrawnTickets = new ArrayList<>();

    public boolean execute(
            GameController gameController,
            Player player,
            DestinationTicketCardDeck deck,
            List<Integer> keepIndexes) {

        if (gameController == null || player == null || deck == null || keepIndexes == null) {
            lastMessage = "Missing game data for destination ticket draw.";
            return false;
        }

        if (!gameController.canPlayerAct(player)) {
            lastMessage = "This player cannot act right now.";
            return false;
        }

        lastDrawnTickets = deck.drawUpTo(DRAW_COUNT);
        if (lastDrawnTickets.isEmpty()) {
            lastMessage = "No destination tickets are available.";
            return false;
        }

        List<DestinationTicketCard> keptTickets = selectKeptTickets(lastDrawnTickets, keepIndexes);
        if (keptTickets.isEmpty()) {
            deck.returnToBottom(lastDrawnTickets);
            lastDrawnTickets = new ArrayList<>();
            lastMessage = "Player must keep at least one destination ticket.";
            return false;
        }

        List<DestinationTicketCard> unkeptTickets = new ArrayList<>(lastDrawnTickets);
        unkeptTickets.removeAll(keptTickets);

        player.addDestinationTickets(keptTickets);
        deck.returnToBottom(unkeptTickets);
        gameController.markActionUsed();

        lastMessage = "Destination tickets drawn successfully.";
        return true;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public List<DestinationTicketCard> getLastDrawnTickets() {
        return new ArrayList<>(lastDrawnTickets);
    }

    private List<DestinationTicketCard> selectKeptTickets(
            List<DestinationTicketCard> availableTickets,
            List<Integer> keepIndexes) {

        List<DestinationTicketCard> keptTickets = new ArrayList<>();

        for (Integer index : keepIndexes) {
            if (index == null || index < 0 || index >= availableTickets.size()) {
                return new ArrayList<>();
            }
            keptTickets.add(availableTickets.get(index));
        }

        return keptTickets;
    }
}