package ui;

import game.GameManager;
import game.GameStateListener;
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

    public LeftRailPanel(Player you) {
        this.you = you;
        setOpaque(false);
        setPreferredSize(new Dimension(210, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        GameManager.getInstance().addListener(this);
        rebuild();
    }

    @Override
    public void onStateChanged() { rebuild(); revalidate(); repaint(); }

    // ── Assembly ──────────────────────────────────────────────────────────────

    private void rebuild() {
        removeAll();
        setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 8));

        // Settings icon placeholder
        JLabel gear = new JLabel("⚙");
        gear.setFont(UiTheme.display(18));
        gear.setForeground(new Color(0xFF, 0xFF, 0xFF, 120));
        gear.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(gear);

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

        // Your chip
        int youIdx    = players.indexOf(you);
        Color youColour = UiTheme.PLAYER_COLOURS[youIdx % UiTheme.PLAYER_COLOURS.length];
        add(makeYouChip(you, you == current, youColour));

        // Ticket thumbnail strip
        if (!you.getTickets().isEmpty()) {
            add(Box.createVerticalStrut(6));
            add(makeTicketStrip(you.getTickets()));
        }
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

    private JPanel makeYouChip(Player p, boolean active, Color colour) {
        JPanel chip = new JPanel(new BorderLayout(6, 0));
        chip.setOpaque(true);
        chip.setBackground(new Color(0x18, 0x20, 0x38));
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? colour : UiTheme.PANEL_BORDER, active ? 2 : 1, true),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        chip.setMaximumSize(new Dimension(200, 100));

        JLabel avatar = makeAvatar(p, colour, 44, 20);
        chip.add(avatar, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(p.getName() + (active ? "  ◀ your turn" : ""));
        name.setFont(UiTheme.display(12));
        name.setForeground(active ? colour : Color.WHITE);

        JLabel tickInfo = new JLabel("🎫 " + p.getTickets().size() + "  ·  🚌 " + p.getTrainPieces());
        tickInfo.setFont(UiTheme.mono(10));
        tickInfo.setForeground(new Color(0xAA, 0xBB, 0xCC));

        JLabel score = new JLabel(p.getScore().get() + " pts");
        score.setFont(UiTheme.display(16));
        score.setForeground(colour);

        info.add(name);
        info.add(tickInfo);
        info.add(score);
        chip.add(info, BorderLayout.CENTER);
        return chip;
    }

    // ── Ticket strip ──────────────────────────────────────────────────────────

    /**
     * Horizontal scrollable strip of destination ticket thumbnails.
     * Tries to match each ticket to a config image; shows a styled text card if unmatched.
     */
    private JScrollPane makeTicketStrip(List<DestinationTicketCard> tickets) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row.setOpaque(false);

        for (DestinationTicketCard t : tickets) {
            row.add(makeTicketThumbnail(t));
        }

        JScrollPane scroll = new JScrollPane(row,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setMaximumSize(new Dimension(200, 62));
        scroll.setPreferredSize(new Dimension(200, 62));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 4));
        scroll.getHorizontalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private JPanel makeTicketThumbnail(DestinationTicketCard t) {
        ConfigLoader            cfg   = ConfigLoader.get();
        ConfigLoader.TicketImageEntry match = cfg.findTicketImage(t.getCityA(), t.getCityB());
        Image img = match != null ? cfg.loadImage(match.imagePath()) : null;

        String fromAbbr = abbrev(t.getCityA());
        String toAbbr   = abbrev(t.getCityB());
        String pts      = "+" + t.getPointValue();

        return new JPanel() {
            { setPreferredSize(new Dimension(60, 50)); setOpaque(false); }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int w = getWidth(), h = getHeight();
                Shape clip = new RoundRectangle2D.Float(0, 0, w, h, 7, 7);

                if (img != null) {
                    g2.setClip(clip);
                    g2.drawImage(img, 0, 0, w, h, null);
                    g2.setClip(null);
                    // Darken bottom edge for pts badge
                    g2.setColor(new Color(0, 0, 0, 160));
                    g2.fillRect(0, h - 14, w, 14);
                } else {
                    // Manila-card fallback
                    g2.setColor(new Color(0xF4, 0xEF, 0xDD, 230));
                    g2.fill(clip);
                    // From city
                    g2.setFont(UiTheme.mono(8));
                    g2.setColor(UiTheme.INK);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(fromAbbr, w / 2 - fm.stringWidth(fromAbbr) / 2, 13);
                    // Arrow
                    g2.setFont(UiTheme.body(9));
                    g2.setColor(UiTheme.INK_DIM);
                    fm = g2.getFontMetrics();
                    g2.drawString("→", w / 2 - fm.stringWidth("→") / 2, 26);
                    // To city
                    g2.setFont(UiTheme.mono(8));
                    g2.setColor(UiTheme.INK);
                    fm = g2.getFontMetrics();
                    g2.drawString(toAbbr, w / 2 - fm.stringWidth(toAbbr) / 2, 39);
                }

                // Point value
                g2.setFont(UiTheme.display(9));
                g2.setColor(UiTheme.GOLD);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(pts, w / 2 - fm.stringWidth(pts) / 2, h - 3);

                // Border
                g2.setColor(new Color(0, 0, 0, 80));
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(clip);
            }
        };
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
