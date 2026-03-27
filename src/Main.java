import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Instantiate the components
            GameController controller = new GameController();
            GameWindow window = new GameWindow(controller);
            
            // Link the window back to the controller so it can send UI updates
            controller.setWindow(window);
            
            // Display the UI
            window.setVisible(true);
        });
    }
}