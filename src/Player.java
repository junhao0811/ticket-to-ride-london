import java.util.List;

public class Player {

    public List<TrainCard> trainCards;
    public List<DestinationTicketCard> ticketCards;
    public String color;
    public int coloredTrainCars;
    public int score;


    public boolean triggeredEndGame(){

        if ( this.coloredTrainCars <=2 ){
            return false;
        }
        return true;
    }


}
