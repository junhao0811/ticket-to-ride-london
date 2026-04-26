import java.util.List;
import java.util.ArrayList;

public class DestinationTicketDeck extends Deck<DestinationTicketCard> {
    public DestinationTicketDeck() {
        super();
    }

    public List<DestinationTicketCard> draw(int count) {
        List<DestinationTicketCard> drawnTickets = new ArrayList<>();
        int drawCount = Math.min(count, cards.size());
        for (int i = 0; i < drawCount; i++) {
            drawnTickets.add(cards.remove(0));
        }
        return drawnTickets;
    }

    public void returnRemainingTickets(List<DestinationTicketCard> returnedCards) {
        if (returnedCards != null && !returnedCards.isEmpty()) {
            cards.addAll(returnedCards);
        }
    }

    public void dealInitialHands(Player player) {
        if (player == null) {
            return;
        }
        List<DestinationTicketCard> drawnTickets = draw(3);
        player.addTickets(drawnTickets);
    }

    public void refillFaceUpDisplay(FaceUpDisplay faceUpDisplay) {
        // Destination tickets are usually hidden; this is a placeholder hook from UML.
    }

    public DestinationTicketCard provideBlindDraw(Player player) {
        List<DestinationTicketCard> drawnTickets = draw(1);
        DestinationTicketCard drawnCard = drawnTickets.isEmpty() ? null : drawnTickets.get(0);
        if (player != null && drawnCard != null) {
            List<DestinationTicketCard> keep = new ArrayList<>();
            keep.add(drawnCard);
            player.addTickets(keep);
        }
        return drawnCard;
    }
}

