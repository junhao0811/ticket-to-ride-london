import ui.GameMenuScreen;

import javax.swing.*;

/**
 * Entry point. Opens the main menu; GameManager.init() is called from
 * GameMenuScreen once the player enters names and clicks NEW GAME.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameMenuScreen().setVisible(true));
    }
}
