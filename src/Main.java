import java.util.ArrayList;
import java.util.List;

public class Main   {

    public static void main(String[] args) {

        System.out.println("WELCOME TO TICKET TO RIDE!");
        TrainCardDeck trainCardDeck = new TrainCardDeck();
        trainCardDeck.shuffleDeck();
        List<TrainCard> pool = trainCardDeck.fiveForPool();
        FaceUpCardPool faceUpCardPool = new FaceUpCardPool(pool);

        System.out.println("The deck of train cards has been set");
        System.out.println("The 5 train cards face up pool has been created");
        System.out.println("The cards are: ");
        for (TrainCard card : faceUpCardPool.trainCards){
            System.out.println(card.type + " Train"+ ", Proof instance exists: " + card);
        }





    }
}
