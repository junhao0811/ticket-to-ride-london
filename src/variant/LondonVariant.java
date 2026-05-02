package variant;

import strategy.LondonScoringConfig;
import strategy.ScoringStrategy;

/**
 * Concrete variant for the London map.
 * This is the only class that names "London" — GameManager never does.
 */
public class LondonVariant implements GameVariant {

    private final LondonMapConfig mapConfig = new LondonMapConfig();
    private final ScoringStrategy scoring   = new LondonScoringConfig();

    @Override
    public MapConfiguration getMapConfiguration() {
        return mapConfig;
    }

    @Override
    public ScoringStrategy getScoring() {
        return scoring;
    }

    @Override
    public int getInitialTrainPieces() {
        return 17;
    }

    @Override
    public int getInitialHandSize() {
        return 4;
    }

    @Override
    public int getInitialTicketsDealt() {
        return 3;
    }

    @Override
    public int getMinTicketsKeptInitial() {
        return 2;
    }

    @Override
    public int getMinTicketsKeptDraw() {
        return 1;
    }
}
