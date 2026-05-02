package variant;

import model.board.City;
import model.board.Route;
import model.deck.DestinationTicketCardDeck;
import model.deck.TrainCardDeck;
import java.util.List;

/**
 * Factory interface for board construction.
 * Each map variant supplies its own cities, routes, and initial decks.
 */
public interface MapConfiguration {

    /** @return all cities for this map */
    List<City> getCities();

    /** @return all routes for this map */
    List<Route> getRoutes();

    /** @return a freshly shuffled train-card deck for this map */
    TrainCardDeck buildTrainDeck();

    /** @return a freshly shuffled destination-ticket deck for this map */
    DestinationTicketCardDeck buildTicketDeck();
}
