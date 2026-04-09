 import java.util.ArrayList;
 import java.util.List;

public class Player {

    public List<TrainCard> trainCards;
    public List<DestinationTicketCard> ticketCardsCompleted;
    public List<DestinationTicketCard> ticketCardsUncompleted;
    public List<DestinationTicketCard> ticketCards;
    public String color;
    public int coloredTrainCars;
    public int score;

    //dummy constructor
    public Player(){
        this.coloredTrainCars = 45;
        this.ticketCards = new ArrayList<>();
    }


    public void addTicketCard(DestinationTicketCard ticketCard){

        this.ticketCards.add(ticketCard);

    }


    public boolean triggeredEndGame(){

        if ( this.coloredTrainCars <=2 ){
            return false;
        }
        return true;
    }


}
