import java.util.List;

public class ClaimRouteAction {
    private String lastMessage;

    public boolean execute(
            GameController gameController,
            Player player,
            Route route,
            List<TrainCard> cardsUsed) {

        if (gameController == null || player == null || route == null || cardsUsed == null) {
            lastMessage = "Missing game data for route claim.";
            return false;
        }

        if (!gameController.canPlayerAct(player)) {
            lastMessage = "This player cannot act right now.";
            return false;
        }

        if (route.isClaimed()) {
            lastMessage = "Route is already claimed.";
            return false;
        }

        if (!player.hasEnoughTrainCars(route.getLength())) {
            lastMessage = "Player does not have enough train pieces.";
            return false;
        }

        if (!player.hasCards(cardsUsed)) {
            lastMessage = "Player does not have the selected train cards.";
            return false;
        }

        if (!route.canBeClaimedWith(cardsUsed)) {
            lastMessage = "Selected train cards do not match the route.";
            return false;
        }

        boolean removedCards = player.removeTrainCards(cardsUsed);
        if (!removedCards) {
            lastMessage = "Failed to remove selected train cards.";
            return false;
        }

        boolean removedTrainCars = player.removeTrainCars(route.getLength());
        if (!removedTrainCars) {
            lastMessage = "Failed to remove train pieces.";
            return false;
        }

        route.claimBy(player);
        player.getScore().addPoints(calculateRoutePoints(route.getLength()));
        gameController.markActionUsed();

        lastMessage = "Route claimed successfully.";
        return true;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    private int calculateRoutePoints(int routeLength) {
        switch (routeLength) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
                return 7;
            case 5:
                return 10;
            case 6:
                return 15;
            default:
                return 0;
        }
    }
}