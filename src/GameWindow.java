import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    
    private GameController controller;
    private JTextArea displayArea;
    
    public GameWindow(GameController controller) {
        this.controller = controller;
        
        setTitle("Ticket to Ride - Sprint 1 Prototype");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());
        
        // Create the top panel with the 3 mandatory action buttons
        JPanel actionPanel = new JPanel();
        JButton drawCardsBtn = new JButton("Draw Train Car Cards");
        JButton claimRouteBtn = new JButton("Claim a Route");
        JButton drawTicketsBtn = new JButton("Draw Destination Tickets");
        
        // Map buttons to the controller logic
        drawCardsBtn.addActionListener(e -> controller.takeTurn("DRAW_CARDS"));
        claimRouteBtn.addActionListener(e -> controller.takeTurn("CLAIM_ROUTE"));
        drawTicketsBtn.addActionListener(e -> controller.takeTurn("DRAW_TICKETS"));
        
        actionPanel.add(drawCardsBtn);
        actionPanel.add(claimRouteBtn);
        actionPanel.add(drawTicketsBtn);
        add(actionPanel, BorderLayout.NORTH);
        
        // Create the center display area for game state and validation messages
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        logMessage("System initialized. Awaiting player action...");
    }
    
    // Method for the controller to update the UI
    public void logMessage(String message) {
        displayArea.append(message + "\n");
        // Auto-scroll to the bottom
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }
}