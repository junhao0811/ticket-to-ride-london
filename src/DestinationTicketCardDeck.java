import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DestinationTicketCardDeck {
    private final List<DestinationTicketCard> tickets;

    public DestinationTicketCardDeck(List<DestinationTicketCard> tickets) {
        this.tickets = new ArrayList<>(tickets);
    }

    public void shuffle() {
        Collections.shuffle(tickets);
    }

    public List<DestinationTicketCard> drawUpTo(int count) {
        List<DestinationTicketCard> drawnTickets = new ArrayList<>();
        int actualCount = Math.min(count, tickets.size());

        for (int i = 0; i < actualCount; i++) {
            drawnTickets.add(tickets.remove(0));
        }
        return drawnTickets;
    }

    public void returnToBottom(List<DestinationTicketCard> unkeptTickets) {
        tickets.addAll(unkeptTickets);
    }

    public int size() {
        return tickets.size();
    }
}