package ui;

import game.GameManager;
import game.GameStateListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Final scoring breakdown shown when the game phase transitions to ENDED.
 * Replaces the map view; shows route pts, ticket +/-, longest-path bonus, total.
 */
public class ScorePanel extends JPanel implements GameStateListener {

    public ScorePanel() {
        setOpaque(true);
        setBackground(UiTheme.BG_DEEP);
        GameManager.getInstance().addListener(this);
    }

    @Override
    public void onStateChanged() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<FinalScore> scores = GameManager.getInstance().getFinalScores();
        if (scores.isEmpty()) return;

        int w = getWidth(), h = getHeight();
        int rowH = 60, startY = h / 4;
        int col0 = 60, col1 = 220, col2 = 360, col3 = 500, col4 = 640;

        // Title
        g2.setFont(UiTheme.display(28));
        g2.setColor(UiTheme.GOLD);
        String title = "FINAL SCORES";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, startY - 30);

        // Header
        g2.setFont(UiTheme.mono(11));
        g2.setColor(UiTheme.INK_MUTE);
        g2.drawString("PLAYER",  col0, startY);
        g2.drawString("ROUTES",  col1, startY);
        g2.drawString("TICKETS", col2, startY);
        g2.drawString("PATH",    col3, startY);
        g2.drawString("TOTAL",   col4, startY);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(UiTheme.PANEL_BORDER);
        g2.drawLine(col0, startY + 6, col4 + 60, startY + 6);

        // Rows
        int y = startY + rowH;
        boolean first = true;
        for (FinalScore fs : scores) {
            Color nameColour = first ? UiTheme.GOLD : Color.WHITE;
            first = false;

            g2.setFont(UiTheme.display(15));
            g2.setColor(nameColour);
            g2.drawString(fs.getPlayer().getName(), col0, y);

            g2.setFont(UiTheme.mono(13));
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(fs.routePoints()),  col1, y);

            int tp = fs.ticketPoints();
            g2.setColor(tp >= 0 ? UiTheme.ROUTE_GREEN : UiTheme.ACCENT_RED);
            g2.drawString((tp >= 0 ? "+" : "") + tp, col2, y);

            g2.setColor(fs.pathBonus() > 0 ? UiTheme.GOLD : UiTheme.INK_MUTE);
            g2.drawString("+" + fs.pathBonus(), col3, y);

            g2.setFont(UiTheme.display(18));
            g2.setColor(nameColour);
            g2.drawString(String.valueOf(fs.total()), col4, y);

            y += rowH;
        }
    }
}
