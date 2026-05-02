package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Transient status message shown at the bottom-centre of the stage.
 * Fades out after ~2.4 seconds — mirrors the toast in app.jsx.
 */
public class ToastOverlay extends JPanel {

    private JLabel messageLabel;
    private Timer hideTimer;

    public ToastOverlay() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        messageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Semi-transparent white background
                g2.setColor(new Color(255, 255, 255, 200)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        messageLabel.setFont(UiTheme.display(18));
        messageLabel.setForeground(new Color(0x3E, 0x27, 0x23)); // Dark Brown
        messageLabel.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        messageLabel.setOpaque(false);
        add(messageLabel);
        setVisible(false);

        hideTimer = new Timer(4000, e -> setVisible(false));
        hideTimer.setRepeats(false);
    }

    /** Shows the toast message for 4 seconds. */
    public void show(String message) {
        messageLabel.setText("  ◈  " + message.toUpperCase());
        setVisible(true);
        hideTimer.restart();
        revalidate();
        repaint();
    }
}
