public class DestinationTicketCard {

    private City cityA;
    private City cityB;
    private int pointValue;

    public DestinationTicketCard(City cityA, City cityB, int points){
        this.cityA = cityA;
        this.cityB = cityB;
        this.pointValue = points;

    }

    public City getCityA(){
        return this.cityA;
    }

    public City getCityB(){
        return this.cityB;
    }

    public int getPointValue(){
        return this.pointValue;
    }

}
