import javax.swing.*;
import java.awt.*;
public class GameUI {
    
    private JFrame mainFrame;
    private JPanel boardPanel;
    private JPanel playerDashboardPanel;
    private JPanel drawingAreaPanel;
    
    public GameUI() {
        initializeUI();
    }
    
    private void initializeUI() {
        // 1. Main Window Setup
        mainFrame = new JFrame("Ticket to Ride - London Prototype");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1024, 768);
        mainFrame.setLayout(new BorderLayout());
        
        // 2. Center: The Game Board (Map & Routes)
        boardPanel = createBoardPanel();
        mainFrame.add(boardPanel, BorderLayout.CENTER);
        
        // 3. South: Player Dashboard (Inventories & Hand)
        playerDashboardPanel = createPlayerDashboardPanel();
        mainFrame.add(playerDashboardPanel, BorderLayout.SOUTH);
        
        // 4. East: Drawing Area (Decks & Face-Up Pool)
        drawingAreaPanel = createDrawingAreaPanel();
        mainFrame.add(drawingAreaPanel, BorderLayout.EAST);
        
        // 5. Display the window
        mainFrame.setLocationRelativeTo(null); // Centers the window on screen
        mainFrame.setVisible(true);
    }
    
    // Creates the area where the map and routes will be drawn
    private JPanel createBoardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(220, 220, 200)); // Placeholder light map color
        
        // Note: For the actual map, you will likely create a custom class 
        // that extends JPanel and overrides the paintComponent(Graphics g) method.
        panel.add(new JLabel("Game Board Map Area"));
        
        return panel;
    }
    
    // Creates the player's control center at the bottom of the screen
    private JPanel createPlayerDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(1024, 150));
        panel.setBorder(BorderFactory.createTitledBorder("Player Dashboard"));
        
        // West Side: Inventories (Trains & Scoring Marker)
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.add(new JLabel("Trains: 45 | Score: 0"));
        panel.add(inventoryPanel, BorderLayout.WEST);
        
        // Center Side: Player Hand (The 4 starting cards)
        JPanel handPanel = new JPanel();
        handPanel.add(new JLabel("Player Hand (4 Cards)"));
        panel.add(handPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Creates the vertical side panel for drawing actions
    private JPanel createDrawingAreaPanel() {
        JPanel panel = new JPanel();
        // BoxLayout aligns components vertically
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 768));
        panel.setBorder(BorderFactory.createTitledBorder("Drawing Area"));
        
        // Top: Train Card Deck (Blind Draw)
        JButton trainDeckBtn = new JButton("Draw Train Card");
        trainDeckBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(trainDeckBtn);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Adds vertical spacing
        
        // Middle: Face-Up Pool (5 Visible Cards)
        JPanel faceUpPool = new JPanel();
        faceUpPool.setLayout(new BoxLayout(faceUpPool, BoxLayout.Y_AXIS));
        faceUpPool.setBorder(BorderFactory.createTitledBorder("Face-Up Pool"));
        
        // Loop to generate the 5 face-up card buttons
        for (int i = 0; i < 5; i++) {
            JButton faceUpCard = new JButton("Face-Up Card " + (i + 1));
            faceUpCard.setAlignmentX(Component.CENTER_ALIGNMENT);
            faceUpPool.add(faceUpCard);
        }
        panel.add(faceUpPool);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Adds vertical spacing
        
        // Bottom: Destination Ticket Deck
        JButton destDeckBtn = new JButton("Draw Dest. Tickets");
        destDeckBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(destDeckBtn);
        
        return panel;
    }
    
    // Standard Swing practice: running the GUI creation on the Event Dispatch Thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameUI());
    }
}