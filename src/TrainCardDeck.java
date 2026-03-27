import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class TrainCardDeck {

    public List<TrainCard> trainCards;

    public TrainCardDeck(){

        this.trainCards = new ArrayList<>();

        for (int i = 0; i<12;i++){

            TrainCard box = new TrainCard(TrainTypes.BOX);
            TrainCard passenger = new TrainCard(TrainTypes.PASSENGER);
            TrainCard tanker = new TrainCard(TrainTypes.TANKER);
            TrainCard reefer = new TrainCard(TrainTypes.REEFER);
            TrainCard freight = new TrainCard(TrainTypes.FREIGHT);
            TrainCard hopper = new TrainCard(TrainTypes.HOPPER);
            TrainCard caboose = new TrainCard(TrainTypes.CABOOSE);
            TrainCard coal = new TrainCard(TrainTypes.COAL);

            this.trainCards.addAll((Collection<? extends TrainCard>) List.of(box, passenger, tanker,reefer, freight, hopper, caboose, coal));

        }

        for (int i = 0; i<14;i++){

            TrainCard locomotive = new TrainCard(TrainTypes.LOCOMOTIVE);
            this.trainCards.add(locomotive);
        }

    }

    public void shuffleDeck(){

        Collections.shuffle(this.trainCards);
    }

    public List<TrainCard> fiveForPool(){
        int itemsToRemove = 5;
        List<TrainCard> pool = new ArrayList<>();
        for (int i = 0; i<itemsToRemove; i++){
            int lastIndex = this.trainCards.size()-1;
            TrainCard card = this.trainCards.remove(lastIndex);
            pool.add(0, card);
        }
        return pool;

    }


}
