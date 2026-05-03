package ui;

import game.GameManager;
import game.GameStateListener;
import game.ScoreManager.FinalScore;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Final scoring breakdown shown when the game phase transitions to ENDED.
 * Replaces the map view; shows route pts, ticket +/-, longest-path bonus, total.
 */
public class ScorePanel extends JPanel implements GameStateListener {

    public ScorePanel() {
        setOpaque(false);
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
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        
        // Dark translucent wash to let the board peek through
        g2.setColor(new Color(10, 15, 30, 140));
        g2.fillRect(0, 0, w, h);

        List<FinalScore> scores = GameManager.getInstance().getFinalScores();
        if (scores.isEmpty()) {
            g2.setFont(UiTheme.display(24));
            g2.setColor(Color.WHITE);
            String msg = "Calculating results...";
            g2.drawString(msg, (w - g2.getFontMetrics().stringWidth(msg))/2, h/2);
            return;
        }

        // ── Header Section ──────────────────────────────────────────────────
        g2.setFont(UiTheme.display(48));
        g2.setColor(UiTheme.GOLD);
        String title = "GAME OVER";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, 100);

        FinalScore winner = scores.get(0);
        g2.setFont(UiTheme.display(26));
        g2.setColor(Color.WHITE);
        String sub = winner.getPlayer().getName() + " Wins!";
        g2.drawString(sub, (w - g2.getFontMetrics().stringWidth(sub)) / 2, 150);

        // ── Dashboard Layout ───────────────────────────────────────────────
        int startY = 240;
        int rowH   = 90;
        int cardW  = 960;
        int cardX  = (w - cardW) / 2;

        // Table Headers - More prominent
        g2.setFont(UiTheme.mono(13));
        g2.setColor(new Color(255, 255, 255, 180));
        int col0 = cardX + 40;   // Player
        int col1 = cardX + 260;  // Routes
        int col2 = cardX + 420;  // Tickets
        int col3 = cardX + 620;  // Path Bonus
        int col4 = cardX + 820;  // Total

        g2.drawString("PLAYER",  col0, startY - 20);
        g2.drawString("ROUTES",  col1, startY - 20);
        g2.drawString("TICKETS", col2, startY - 20);
        g2.drawString("PATH",    col3, startY - 20);
        g2.drawString("TOTAL",   col4, startY - 20);

        // Header underline
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(cardX, startY - 10, cardX + cardW, startY - 10);

        int y = startY + 40;
        for (FinalScore fs : scores) {
            boolean isWinner = fs == winner;
            
            // Row background
            g2.setColor(new Color(255, 255, 255, isWinner ? 30 : 15));
            g2.fillRoundRect(cardX, y - 55, cardW, rowH, 14, 14);
            if (isWinner) {
                g2.setColor(new Color(0xC8, 0x9A, 0x2A, 180)); // UiTheme.GOLD
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(cardX, y - 55, cardW, rowH, 14, 14);
            }

            // Player Name & Colour
            Color pCol = UiTheme.playerColour(fs.getPlayer().getColour());
            g2.setColor(pCol);
            g2.fillOval(col0 - 28, y - 18, 20, 20);
            
            g2.setFont(UiTheme.display(20));
            g2.setColor(isWinner ? UiTheme.GOLD : Color.WHITE);
            g2.drawString(fs.getPlayer().getName(), col0, y);

            // Routes
            g2.setFont(UiTheme.mono(18));
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(fs.routePoints()), col1, y);

            // Tickets (Breakdown)
            int tp = fs.ticketPoints();
            g2.setColor(tp >= 0 ? UiTheme.ROUTE_GREEN : UiTheme.ACCENT_RED);
            g2.drawString((tp >= 0 ? "+" : "") + tp, col2, y);
            g2.setFont(UiTheme.mono(11));
            g2.setColor(new Color(255, 255, 255, 140));
            g2.drawString(fs.completedTickets() + " completed", col2, y + 18);

            // Path Bonus (Breakdown)
            g2.setFont(UiTheme.mono(18));
            g2.setColor(fs.pathBonus() > 0 ? UiTheme.GOLD : UiTheme.INK_MUTE);
            g2.drawString("+" + fs.pathBonus(), col3, y);
            g2.setFont(UiTheme.mono(11));
            g2.setColor(new Color(255, 255, 255, 140));
            g2.drawString("Len: " + fs.longestPathDistance(), col3, y + 18);

            // Total
            g2.setFont(UiTheme.display(28));
            g2.setColor(isWinner ? UiTheme.GOLD : Color.WHITE);
            g2.drawString(String.valueOf(fs.total()), col4, y + 5);

            y += rowH + 20;
        }
    }
}
