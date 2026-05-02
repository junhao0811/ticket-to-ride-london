 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;

public class Player {

    private List<DestinationTicketCard> ticketCards;
    public String color;
    private int coloredTrainCars;
    private int score;
    private int maxDist;
    private boolean hasLongestPathBonus;

    //dummy constructor
    public Player(){
        this.coloredTrainCars = 45;
        this.ticketCards = new ArrayList<>();
        this.score = 0;
        this.hasLongestPathBonus = false;
    }


    public void addTicketCard(DestinationTicketCard ticketCard){
        this.ticketCards.add(ticketCard);

    }


    public List<DestinationTicketCard> getTicketCards() {
        return Collections.unmodifiableList(ticketCards);
    }


    public boolean triggeredEndGame(){
        if ( this.coloredTrainCars <=2 ){
            return true;
        }
        return false;
    }

    public void setMaxDist(int max){
        this.maxDist = max;
    }

    public int getMaxDist(){
        return this.maxDist;
    }

    public void addScore(int points){
        this.score = this.score + points;
    }

    public int getScore(){
        return this.score;
    }

    public void setTrueHasLongestPathBonus(){
        this.hasLongestPathBonus = true;
    }

    public boolean getHasLongestPathBonus(){
        return this.hasLongestPathBonus;
    }

    public int getColoredTrainCarsCount(){
        return this.coloredTrainCars;
    }




}
