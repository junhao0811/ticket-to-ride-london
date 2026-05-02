package actions;

import model.cards.DestinationTicketCard;
import model.deck.DestinationTicketDeck;
import model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command: draw up to 3 destination tickets; player keeps ≥ 1, rest return to bottom.
 * The UI sets the kept list via {@link #setKeptTickets(List)} before execute() is called.
 */
public class DrawDestinationTicketAction extends PlayerAction {

    private static final int DRAW_COUNT = 3;

    private final DestinationTicketDeck ticketDeck;
    private final int minKeep;
    private List<DestinationTicketCard> drawnTickets;
    private List<DestinationTicketCard> keptTickets;

    /**
     * @param player     the acting player
     * @param ticketDeck the destination ticket deck
     * @param minKeep    minimum tickets the player must keep (1 mid-game, 2 at setup)
     */
    public DrawDestinationTicketAction(Player player, DestinationTicketDeck ticketDeck,
                                       int minKeep) {
        super(player);
        this.ticketDeck   = ticketDeck;
        this.minKeep      = minKeep;
        this.drawnTickets = new ArrayList<>();
        this.keptTickets  = null;
    }

    /** Draws tickets immediately so the UI can display them for the player to choose from. */
    public List<DestinationTicketCard> peekDrawnTickets() {
        drawnTickets = ticketDeck.drawN(DRAW_COUNT);
        return new ArrayList<>(drawnTickets);
    }

    /**
     * UI calls this after the player selects which tickets to keep.
     *
     * @param kept list of tickets the player chose to keep (must be ≥ minKeep)
     */
    public void setKeptTickets(List<DestinationTicketCard> kept) {
        this.keptTickets = kept;
    }

    @Override
    public boolean validate() {
        if (ticketDeck.isEmpty()) {
            return false;
        }
        if (keptTickets == null) {
            // Tickets haven't been drawn/chosen yet — still valid to initiate
            return true;
        }
        return keptTickets.size() >= minKeep;
    }

    @Override
    public ActionResult execute() {
        if (drawnTickets.isEmpty()) {
            drawnTickets = ticketDeck.drawN(DRAW_COUNT);
        }
        List<DestinationTicketCard> toReturn = new ArrayList<>(drawnTickets);
        List<DestinationTicketCard> kept = keptTickets != null ? keptTickets : drawnTickets;
        toReturn.removeAll(kept);

        player.addTickets(kept);
        ticketDeck.returnToBottom(toReturn);

        return ActionResult.success("Kept " + kept.size() + " ticket(s).");
    }

    /** @return the tickets drawn, for UI display */
    public List<DestinationTicketCard> getDrawnTickets() {
        return new ArrayList<>(drawnTickets);
    }
}
