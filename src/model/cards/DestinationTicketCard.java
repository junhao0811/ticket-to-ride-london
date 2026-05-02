package model.cards;

/**
 * Immutable destination ticket. Holds two city names and a point value.
 * Scoring (+/- pointValue) is computed by TicketScoreStrategy, not here.
 */
public final class DestinationTicketCard {

    private final String cityA;
    private final String cityB;
    private final int pointValue;

    /**
     * @param cityA      name of the first city
     * @param cityB      name of the second city
     * @param pointValue points awarded (positive) or lost (negative sign applied by scorer)
     */
    public DestinationTicketCard(String cityA, String cityB, int pointValue) {
        this.cityA = cityA;
        this.cityB = cityB;
        this.pointValue = pointValue;
    }

    /** @return name of the origin city */
    public String getCityA() {
        return cityA;
    }

    /** @return name of the destination city */
    public String getCityB() {
        return cityB;
    }

    /** @return point value of this ticket (always positive; scorer applies sign) */
    public int getPointValue() {
        return pointValue;
    }

    @Override
    public String toString() {
        return cityA + " → " + cityB + " (" + pointValue + "pts)";
    }
}
