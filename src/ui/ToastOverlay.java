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
        messageLabel.setFont(UiTheme.body(12));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UiTheme.PANEL_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(new Color(0x1A, 0x24, 0x3C, 230));
        add(messageLabel);
        setVisible(false);

        hideTimer = new Timer(2400, e -> setVisible(false));
        hideTimer.setRepeats(false);
    }

    /** Shows the toast message for 2.4 seconds. */
    public void show(String message) {
        messageLabel.setText("  ● " + message);
        setVisible(true);
        hideTimer.restart();
        revalidate();
        repaint();
    }
}
