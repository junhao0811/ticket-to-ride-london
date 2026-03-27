public class GameController {
    
    private GameWindow window;
    
    // Simulating player state [3, 4]
    private int playerTrains = 10; // Started at 10 instead of 45 to quickly test the end-game trigger
    private int playerScore = 0;
    
    public void setWindow(GameWindow window) {
        this.window = window;
    }
    
    // Main entry point for the user story interaction
    public void takeTurn(String actionType) {
        window.logMessage("\n--- Player initiates action: " + actionType + " ---");
        boolean validationSuccess = false;
        
        // 1. Validate the chosen action
        switch (actionType) {
            case "DRAW_CARDS":
                validationSuccess = validateDrawCards();
                if (validationSuccess) {
                    window.logMessage("Success: 2 Train Car Cards drawn.");
                }
                break;
            case "CLAIM_ROUTE":
                validationSuccess = validateClaimRoute();
                if (validationSuccess) {
                    playerTrains -= 3; // Simulating claiming a 3-space route
                    playerScore += 4;
                    window.logMessage("Success: Route claimed! Trains deducted.");
                }
                break;
            case "DRAW_TICKETS":
                validationSuccess = validateDrawTickets();
                if (validationSuccess) {
                    window.logMessage("Success: 3 Destination Tickets drawn, kept at least 1.");
                }
                break;
        }
        
        // 2. Handle failure or success paths
        if (!validationSuccess) {
            window.logMessage(
                "ERROR: Action validation failed. Please retry or select an alternative action.");
            return; // Player must try again (turn is not passed)
        }
        
        // 3. Action succeeded: display updated board/score and check end-game condition
        window.logMessage(
            "State Updated -> Score: " + playerScore + " | Trains Remaining: " + playerTrains);
        
        if (playerTrains <= 2) {
            window.logMessage(
                "*** GAME END CONDITION MET: Player has 2 or fewer trains remaining! ***");
        }
        
        // 4. Pass control
        window.logMessage("Action logged. Passing control to the next player...");
    }
    
    // --- Dummy Validation Methods ---
    private boolean validateDrawCards() {
        return true;
    }
    
    private boolean validateClaimRoute() {
        // Randomly simulate a failure (30% chance) so you can test the error/retry path
        if (Math.random() < 0.3) {
            window.logMessage(
                "Validation Failed: You do not have the matching card set for this route.");
            return false;
        }
        return true;
    }
    
    private boolean validateDrawTickets() {
        return true;
    }
}