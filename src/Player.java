 import java.util.List;

public class Player {

    public List<TrainCard> trainCards;
    public List<DestinationTicketCard> ticketCardsCompleted;
    public List<DestinationTicketCard> ticketCardsUncompleted;
    public String color;
    public int coloredTrainCars;
    public int score;

    //dummy constructor
    public Player(){
        this.coloredTrainCars = 45;
    }


    public boolean triggeredEndGame(){

        if ( this.coloredTrainCars <=2 ){
            return false;
        }
        return true;
    }


}
