package ui;

import game.GameManager;
import game.GameStateListener;
import model.board.GameBoard;
import model.cards.DestinationTicketCard;
import model.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Left rail: opponent player chips at top, current-player chip at bottom with
 * a scrollable strip of destination ticket thumbnails.
 */
public class LeftRailPanel extends JPanel implements GameStateListener {

    private final Player you;
    private boolean      isExpanded = false;

    public LeftRailPanel(Player you) {
        this.you = you;
        setOpaque(false);
        setPreferredSize(new Dimension(220, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        GameManager.getInstance().addListener(this);
        rebuild();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Subtle dark wash to ensure transparency/scrolling behaves correctly in Swing
        g.setColor(new Color(0, 0, 0, 12)); 
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    @Override
    public void onStateChanged() { rebuild(); revalidate(); repaint(); }

    // ── Assembly ──────────────────────────────────────────────────────────────

    private void rebuild() {
        removeAll();
        setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        // ── Help Button Bar ──
        JPanel helpBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        helpBar.setOpaque(false);
        helpBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helpObj = helpIcon("❓", "Game Objective");
        helpObj.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(LeftRailPanel.this);
                HelpDialog.showObjective(owner);
            }
        });

        JLabel helpActions = helpIcon("📖", "Turn Actions");
        helpActions.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(LeftRailPanel.this);
                HelpDialog.showTurnActions(owner);
            }
        });

        JLabel helpTickets = helpIcon("🎫", "Tickets Rule");
        helpTickets.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(LeftRailPanel.this);
                HelpDialog.showTicketsRule(owner);
            }
        });

        helpBar.add(helpObj);
        helpBar.add(helpActions);
        helpBar.add(helpTickets);
        add(helpBar);
        add(Box.createVerticalStrut(14));

        // Opponent chips
        List<Player> players = GameManager.getInstance().getPlayers();
        Player       current = GameManager.getInstance().getCurrentPlayer();
        int idx = 0;
        for (Player p : players) {
            if (p == you) { idx++; continue; }
            add(makeOpponentChip(p, p == current,
                UiTheme.PLAYER_COLOURS[idx % UiTheme.PLAYER_COLOURS.length]));
            add(Box.createVerticalStrut(8));
            idx++;
        }

        add(Box.createVerticalGlue());

        // ── Your Interactive Panel (Drop-up) ──
        if (isExpanded) {
            JPanel tickets = makeExpandedTickets();
            JScrollPane scroll = new JScrollPane(tickets,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
            scroll.setBorder(null);
            scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
            scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
            scroll.getVerticalScrollBar().setUnitIncrement(24);

            // Cap the height strictly to prevent it from reaching the player panel
            scroll.setMinimumSize(new Dimension(200, 100));
            scroll.setMaximumSize(new Dimension(200, 280));
            add(scroll);
            add(Box.createVerticalStrut(40));
        }

        int youIdx    = players.indexOf(you);
        Color youColour = UiTheme.PLAYER_COLOURS[youIdx % UiTheme.PLAYER_COLOURS.length];
        add(makeYouPanel(you, you == current, youColour));
    }

    // ── Opponent chip ─────────────────────────────────────────────────────────

    private JPanel makeOpponentChip(Player p, boolean active, Color colour) {
        JPanel chip = new JPanel(new BorderLayout(6, 0));
        chip.setOpaque(true);
        chip.setBackground(new Color(0x1E, 0x28, 0x42));
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? colour : UiTheme.PANEL_BORDER, active ? 2 : 1, true),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        chip.setMaximumSize(new Dimension(200, 56));
        chip.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel avatar = makeAvatar(p, colour, 36, 16);
        chip.add(avatar, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name  = new JLabel(p.getName());
        name.setFont(UiTheme.display(12));
        name.setForeground(Color.WHITE);
        JLabel stats = new JLabel(p.getScore().get() + " pts  ·  🚌 " + p.getTrainPieces());
        stats.setFont(UiTheme.mono(10));
        stats.setForeground(new Color(0xAA, 0xBB, 0xCC));
        info.add(name);
        info.add(stats);
        chip.add(info, BorderLayout.CENTER);

        if (active) {
            JLabel dot = new JLabel("●");
            dot.setFont(UiTheme.body(10));
            dot.setForeground(colour);
            chip.add(dot, BorderLayout.EAST);
        }
        return chip;
    }

    // ── Your chip ─────────────────────────────────────────────────────────────

    private JPanel makeYouPanel(Player p, boolean active, Color colour) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(200, 110));
        panel.setMinimumSize(new Dimension(200, 110));
        panel.setMaximumSize(new Dimension(200, 110));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Background main area
        JPanel bg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x18, 0x20, 0x38));
                g2.fillRoundRect(0, 30, getWidth(), getHeight() - 30, 12, 12);
                g2.setColor(active ? colour : UiTheme.PANEL_BORDER);
                g2.setStroke(new BasicStroke(active ? 2f : 1f));
                g2.drawRoundRect(0, 30, getWidth() - 1, getHeight() - 31, 12, 12);
                g2.dispose();
            }
        };
        bg.setOpaque(false);
        bg.setBounds(0, 0, 200, 110);
        panel.add(bg);

        // Ticket Stats Overlay
        int completed = 0;
        GameBoard board = GameManager.getInstance().getBoard();
        strategy.ScoringStrategy scorer = GameManager.getInstance().getVariant().getScoring();
        for (DestinationTicketCard t : p.getTickets()) {
            if (scorer.scoreTicket(t, p, board) > 0) completed++;
        }
        int incomplete = p.getTickets().size() - completed;

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        stats.setOpaque(true);
        stats.setBackground(Color.WHITE);
        stats.setBorder(BorderFactory.createLineBorder(new Color(0xD0, 0xD0, 0xD0), 1, true));
        stats.setBounds(15, 10, 100, 32);

        JLabel incLbl = new JLabel("✕ " + incomplete);
        incLbl.setFont(UiTheme.display(12));
        incLbl.setForeground(new Color(0xC8, 0x24, 0x2A)); // Red
        JLabel comLbl = new JLabel("✓ " + completed);
        comLbl.setFont(UiTheme.display(12));
        comLbl.setForeground(new Color(0x2C, 0x8C, 0x4D)); // Green

        stats.add(incLbl);
        stats.add(comLbl);
        panel.add(stats);
        panel.setComponentZOrder(stats, 0);

        // Expand/Collapse Button
        JLabel toggle = new JLabel(isExpanded ? "▼" : "▲");
        toggle.setFont(UiTheme.display(14));
        toggle.setForeground(Color.WHITE);
        toggle.setOpaque(true);
        toggle.setBackground(new Color(0x30, 0x3A, 0x55));
        toggle.setHorizontalAlignment(SwingConstants.CENTER);
        toggle.setBounds(120, 18, 26, 26);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        toggle.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                isExpanded = !isExpanded;
                rebuild();
                revalidate();
                repaint();
            }
        });
        panel.add(toggle);
        panel.setComponentZOrder(toggle, 0);

        // Name and Score
        JLabel name = new JLabel(p.getName());
        name.setFont(UiTheme.display(14));
        name.setForeground(Color.WHITE);
        name.setBounds(55, 50, 130, 20);
        panel.add(name);
        panel.setComponentZOrder(name, 0);

        // Score Badge
        JLabel scoreBadge = new JLabel(String.valueOf(p.getScore().get()));
        scoreBadge.setFont(UiTheme.display(16));
        scoreBadge.setForeground(Color.WHITE);
        scoreBadge.setHorizontalAlignment(SwingConstants.CENTER);
        scoreBadge.setOpaque(true);
        scoreBadge.setBackground(new Color(0xC8, 0x24, 0x2A));
        scoreBadge.setBounds(10, 70, 36, 36);
        scoreBadge.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        panel.add(scoreBadge);
        panel.setComponentZOrder(scoreBadge, 0);

        JLabel pieceInfo = new JLabel("🚌 " + p.getTrainPieces() + " buses left");
        pieceInfo.setFont(UiTheme.mono(10));
        pieceInfo.setForeground(new Color(0xAA, 0xBB, 0xCC));
        pieceInfo.setBounds(55, 70, 130, 15);
        panel.add(pieceInfo);
        panel.setComponentZOrder(pieceInfo, 0);

        return panel;
    }

    // ── Expanded Tickets View ────────────────────────────────────────────────

    private JPanel makeExpandedTickets() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(0, 0, 8, 0);

        List<DestinationTicketCard> tickets = you.getTickets();
        GameBoard board = GameManager.getInstance().getBoard();
        strategy.ScoringStrategy scorer = GameManager.getInstance().getVariant().getScoring();

        int row = 0;
        for (DestinationTicketCard t : tickets) {
            boolean done = scorer.scoreTicket(t, you, board) > 0;
            c.gridy = row++;
            container.add(makeTicketDetailCard(t, done), c);
        }
        
        // Add a shim at the bottom to push everything up
        c.gridy = row;
        c.weighty = 1.0;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        container.add(filler, c);

        return container;
    }

    private JPanel makeTicketDetailCard(DestinationTicketCard t, boolean completed) {
        ConfigLoader            cfg   = ConfigLoader.get();
        ConfigLoader.TicketImageEntry match = cfg.findTicketImage(t.getCityA(), t.getCityB());
        Image img = match != null ? cfg.loadImage(match.imagePath()) : null;

        Dimension cardSize = new Dimension(180, 100);
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth(), h = getHeight();
                Shape clip = new RoundRectangle2D.Float(0, 0, w, h, 10, 10);

                if (img != null) {
                    g2.setClip(clip);
                    g2.drawImage(img, 0, 0, w, h, null);
                    g2.setClip(null);
                } else {
                    g2.setColor(new Color(0xF4, 0xEF, 0xDD));
                    g2.fill(clip);
                    g2.setColor(UiTheme.INK);
                    g2.setFont(UiTheme.display(9));
                    g2.drawString(t.getCityA() + " → " + t.getCityB(), 8, 18);
                }

                if (completed) {
                    // Success overlay
                    g2.setColor(new Color(0x2C, 0x8C, 0x4D, 180));
                    g2.fill(new RoundRectangle2D.Float(w - 28, 4, 24, 24, 6, 6));
                    g2.setColor(Color.WHITE);
                    g2.setFont(UiTheme.display(12));
                    g2.drawString("✓", w - 21, 21);
                } else {
                    // Incomplete marker
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fill(clip);
                    g2.setColor(Color.WHITE);
                    g2.setFont(UiTheme.display(12));
                    g2.drawString("✕", w - 21, 21);
                }

                // Point value badge
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(8, h - 26, 36, 18, 8, 8);
                g2.setColor(UiTheme.GOLD);
                g2.setFont(UiTheme.display(11));
                String pts = String.valueOf(t.getPointValue());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(pts, 8 + (36 - fm.stringWidth(pts)) / 2, h - 13);

                g2.setColor(completed ? new Color(0x2C, 0x8C, 0x4D) : UiTheme.PANEL_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(clip);

                g2.dispose();
            }
        };

        card.setPreferredSize(cardSize);
        card.setMinimumSize(cardSize);
        card.setMaximumSize(cardSize);
        card.setOpaque(false);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel helpIcon(String icon, String tooltip) {
        JLabel lbl = new JLabel(icon);
        lbl.setFont(UiTheme.display(18));
        lbl.setForeground(new Color(0xFF, 0xFF, 0xFF, 120));
        lbl.setToolTipText(tooltip);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { lbl.setForeground(Color.WHITE); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { lbl.setForeground(new Color(0xFF, 0xFF, 0xFF, 120)); }
        });
        return lbl;
    }

    private JLabel makeAvatar(Player p, Color colour, int size, int fontSize) {
        JLabel avatar = new JLabel(String.valueOf(p.getName().charAt(0)));
        avatar.setFont(UiTheme.display(fontSize));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(colour);
        avatar.setPreferredSize(new Dimension(size, size));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setBorder(BorderFactory.createLineBorder(colour.darker(), 1, true));
        return avatar;
    }

    private String abbrev(String city) {
        String[] w = city.split("[ '&]+");
        StringBuilder sb = new StringBuilder();
        for (String part : w) if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0)));
        return sb.length() > 3 ? sb.substring(0, 3) : sb.toString();
    }
}
