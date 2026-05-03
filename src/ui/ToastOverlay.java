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
    private final java.util.Queue<String> messageQueue = new java.util.LinkedList<>();
    private boolean isDisplaying = false;

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

        hideTimer = new Timer(2500, e -> {
            setVisible(false);
            isDisplaying = false;
            processNext();
        });
        hideTimer.setRepeats(false);
    }

    /** Shows the toast message for 2.5 seconds, or queues it if one is already showing. */
    public void show(String message) {
        if (message == null || message.isEmpty()) return;
        messageQueue.add(message);
        if (!isDisplaying) {
            processNext();
        }
    }

    private void processNext() {
        String msg = messageQueue.poll();
        if (msg != null) {
            isDisplaying = true;
            messageLabel.setText("  ◈  " + msg.toUpperCase());
            setVisible(true);
            hideTimer.restart();
            revalidate();
            repaint();
        }
    }
}
