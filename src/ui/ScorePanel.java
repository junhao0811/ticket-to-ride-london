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

    private final JButton menuBtn;

    public ScorePanel() {
        setOpaque(false);
        setLayout(null); // Absolute positioning for the button
        GameManager.getInstance().addListener(this);

        menuBtn = createMenuButton();
        add(menuBtn);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int w = getWidth(), h = getHeight();
        if (w > 0 && h > 0) {
            int btnW = 280, btnH = 66;
            menuBtn.setBounds(w - btnW - 60, h - btnH - 60, btnW, btnH);
        }
    }

    private JButton createMenuButton() {
        JButton btn = new JButton("BACK TO MENU") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int bw = getWidth() - 4;
                int bh = getHeight() - 4;
                boolean pressed = getModel().isPressed();
                boolean rolled  = getModel().isRollover();

                Color base   = new Color(0x28, 0x38, 0x58);
                Color hov    = new Color(0x34, 0x48, 0x6C);
                Color border = new Color(0x44, 0x5A, 0x80, 120);

                // Drop shadow
                g2.setColor(new Color(0, 0, 0, 70));
                g2.fillRoundRect(4, 4, bw, bh, bh, bh);

                // Pill fill
                g2.setColor(pressed ? base.darker() : (rolled ? hov : base));
                g2.fillRoundRect(0, 0, bw, bh, bh, bh);

                // Top sheen
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 45),
                                              0, bh / 2f, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(2, 2, bw - 4, bh / 2, bh - 4, bh - 4);

                // Border
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, bw - 2, bh - 2, bh - 2, bh - 2);

                // Label
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (bw - fm.stringWidth(getText())) / 2,
                    (bh - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }

            @Override public boolean isOpaque() { return false; }
        };

        btn.setFont(UiTheme.display(18));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win != null) win.dispose();
            new GameMenuScreen().setVisible(true);
        });
        return btn;
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
        int cardW  = 1100; // Widened to accommodate split columns
        int cardX  = (w - cardW) / 2;

        // Table Headers - More prominent
        g2.setFont(UiTheme.mono(13));
        g2.setColor(new Color(255, 255, 255, 180));
        int col0 = cardX + 40;   // Player
        int col1 = cardX + 260;  // Routes
        int col2 = cardX + 400;  // DONE Tickets
        int col3 = cardX + 580;  // FAIL Tickets
        int col4 = cardX + 760;  // Path Bonus
        int col5 = cardX + 940;  // Total

        g2.drawString("PLAYER",       col0, startY - 20);
        g2.drawString("ROUTE SCORE",       col1, startY - 20);
        g2.drawString("TICKETS COMPLETED", col2, startY - 20);
        g2.drawString("TICKETS INCOMPLETE", col3, startY - 20);
        g2.drawString("LONGEST PATH BONUS",         col4, startY - 20);
        g2.drawString("FINAL SCORE",        col5, startY - 20);

        // Header underline
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(cardX, startY - 10, cardX + cardW, startY - 10);

        int y = startY + 40;
        for (FinalScore fs : scores) {
            boolean isWinner = fs == winner;
            
            // Row background - Less transparent for better visual contrast
            g2.setColor(new Color(255, 255, 255, isWinner ? 120 : 60));
            g2.fillRoundRect(cardX, y - 55, cardW, rowH, 14, 14);
            if (isWinner) {
                g2.setColor(new Color(0xC8, 0x9A, 0x2A, 220)); // UiTheme.GOLD
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

            // DONE Tickets (Math breakdown)
            g2.setFont(UiTheme.mono(18));
            g2.setColor(UiTheme.ROUTE_GREEN);
            g2.drawString("+" + fs.completedPoints(), col2, y);
            g2.setFont(UiTheme.mono(11));
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawString(fs.completedTickets() + " completed", col2, y + 18);

            // FAIL Tickets (Math breakdown)
            g2.setFont(UiTheme.mono(18));
            g2.setColor(fs.incompletePoints() < 0 ? UiTheme.ACCENT_RED : UiTheme.INK_MUTE);
            g2.drawString(String.valueOf(fs.incompletePoints()), col3, y);
            g2.setFont(UiTheme.mono(11));
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawString(fs.incompleteTickets() + " failed", col3, y + 18);

            // Path Bonus (Breakdown)
            g2.setFont(UiTheme.mono(18));
            g2.setColor(fs.pathBonus() > 0 ? UiTheme.GOLD : UiTheme.INK_MUTE);
            g2.drawString("+" + fs.pathBonus(), col4, y);
            g2.setFont(UiTheme.mono(11));
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawString("Len: " + fs.longestPathDistance(), col4, y + 18);

            // Total
            g2.setFont(UiTheme.display(28));
            g2.setColor(isWinner ? UiTheme.GOLD : Color.WHITE);
            g2.drawString(String.valueOf(fs.total()), col5, y + 5);

            y += rowH + 20;
        }
    }
}
