import java.util.Arrays;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        System.out.println("--- 1. INITIALIZING CITIES ---");
        City ny = new City("New York");
        City boston = new City("Boston");
        City montreal = new City("Montreal");
        City toronto = new City("Toronto");
        City chicago = new City("Chicago");

        System.out.println("--- 2. INITIALIZING PLAYERS ---");
        Player player1 = new Player();
        //player1.ticketCards = new ArrayList<>();

        Player player2 = new Player();
        //player2.ticketCards = new ArrayList<>();

        System.out.println("--- 3. INITIALIZING ROUTES & OWNERSHIP ---");
        // Player 1 claims the East Coast
        Route r1 = new Route(ny, boston, 2, null);
        r1.setOwner(player1);

        Route r2 = new Route(boston, montreal, 2, null);
        r2.setOwner(player1);

        // Player 2 claims the Midwest
        Route r3 = new Route(toronto, chicago, 4, null);
        r3.setOwner(player2);

        // Unclaimed Route connecting the two sides
        Route r4 = new Route(boston, toronto, 2, null);
        // owner remains null

        System.out.println("--- 4. BUILDING MASTER MAP ---");
        MapGraph masterMap = new MapGraph();
        masterMap.addRoute(r1);
        masterMap.addRoute(r2);
        masterMap.addRoute(r3);
        masterMap.addRoute(r4);

        System.out.println("--- 5. ASSIGNING TICKETS ---");
        // Player 1 Ticket A: NY to Montreal (They own this full path)
        DestinationTicketCard p1TicketA = new DestinationTicketCard(ny, montreal, 10);

        // Player 1 Ticket B: NY to Toronto (Requires unclaimed route r4 - should fail)
        DestinationTicketCard p1TicketB = new DestinationTicketCard(ny, toronto, 15);

        // Player 2 Ticket: Toronto to Chicago (They own this)
        DestinationTicketCard p2Ticket = new DestinationTicketCard(toronto, chicago, 7);

        System.out.println("\n--- 6. EXECUTING END GAME LOGIC ---");
        EndGameLogic endGame = new EndGameLogic(masterMap, Arrays.asList(player1, player2));
        endGame.populatePlayerGraphs();

        Map<Player, MapGraph> extractedGraphs = endGame.getPlayerGraphs();
        MapGraph p1Graph = extractedGraphs.get(player1);
        MapGraph p2Graph = extractedGraphs.get(player2);

        System.out.println("\n--- 7. TESTING PLAYER 1'S BFS ---");
        boolean p1TestA = p1Graph.pathExists(p1TicketA.getCityA(), p1TicketA.getCityB());
        System.out.println("Ticket NY -> Montreal: " + p1TestA + " | Expected: true");

        boolean p1TestB = p1Graph.pathExists(p1TicketB.getCityA(), p1TicketB.getCityB());
        System.out.println("Ticket NY -> Toronto:  " + p1TestB + " | Expected: false (r4 is unclaimed)");

        System.out.println("\n--- 8. TESTING PLAYER 2'S BFS ---");
        boolean p2Test = p2Graph.pathExists(p2Ticket.getCityA(), p2Ticket.getCityB());
        System.out.println("Ticket Toronto -> Chicago: " + p2Test + " | Expected: true");
    }
}
