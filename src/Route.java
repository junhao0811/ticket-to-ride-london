public class Route {

    private City cityA;
    private City cityB;
    private int length;
    private TrainColor requiredColor;
    private Player owner;


    public Route(City cityA, City cityB, int length, TrainColor requiredColor){
        this.cityA = cityA;
        this.cityB = cityB;
        this.length = length;
        this.requiredColor = requiredColor;
    }

    public City getOppositeCity(City currentCity) {
        if (this.cityA.equals(currentCity)) {
            return this.cityB;
        } else {
            return this.cityA;
        }
    }

    public City getCityA(){
        return this.cityA;
    }

    public City getCityB(){
        return this.cityB;
    }

    public Player getOwner(){
        return this.owner;
    }

    public void setOwner(Player owner){
        this.owner = owner;
    }

    public int getLength(){
        return this.length;
    }

    public TrainColor getRequiredColor(){
        return this.requiredColor;
    }



}
