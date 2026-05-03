package model.deck;

import java.util.Collections;
import model.cards.DestinationTicketCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Deck of destination ticket cards.
 * Does not reshuffle on exhaustion — tickets are finite.
 */
public final class DestinationTicketCardDeck extends Deck<DestinationTicketCard> {

    public DestinationTicketCardDeck(List<DestinationTicketCard> tickets) {
        super();
        addAll(tickets);
    }
}
