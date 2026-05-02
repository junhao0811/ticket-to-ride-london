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

        messageLabel = new JLabel();
        messageLabel.setFont(UiTheme.display(18));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UiTheme.GOLD, 2, true),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(new Color(0x0C, 0x12, 0x24, 240));
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
