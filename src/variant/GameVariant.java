package variant;

import strategy.ScoringStrategy;

/**
 * Entry point for all map and rule variation.
 * Swap the variant in main() to change the entire game without touching GameManager.
 */
public interface GameVariant {

    /** @return the map configuration for this variant */
    MapConfiguration getMapConfiguration();

    /** @return the scoring strategy bundle for this variant */
    ScoringStrategy getScoring();

    /** @return number of train pieces each player starts with */
    int getInitialTrainPieces();

    /** @return number of train cards dealt to each player at game start */
    int getInitialHandSize();

    /** @return number of destination tickets dealt to each player during setup */
    int getInitialTicketsDealt();

    /** @return minimum tickets a player must keep during initial deal */
    int getMinTicketsKeptInitial();

    /** @return minimum tickets a player must keep when drawing mid-game */
    int getMinTicketsKeptDraw();
}
