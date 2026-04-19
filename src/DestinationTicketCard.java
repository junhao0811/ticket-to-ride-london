public class DestinationTicketCard {
    private City firstCity;
    private City secondCity;
    private int pointValue;

    public DestinationTicketCard(City firstCity, City secondCity, int pointValue) {
        this.firstCity = firstCity;
        this.secondCity = secondCity;
        this.pointValue = pointValue;
    }

    public City getFirstCity() {
        return firstCity;
    }

    public City getSecondCity() {
        return secondCity;
    }

    public int getPointValue() {
        return pointValue;
    }

    @Override
    public String toString() {
        return "DestinationTicketCard{"
                + "firstCity="
                + firstCity
                + ", secondCity="
                + secondCity
                + ", pointValue="
                + pointValue
                + '}';
    }
}