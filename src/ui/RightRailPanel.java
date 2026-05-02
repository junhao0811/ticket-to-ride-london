package ui;

import actions.DrawTrainCardAction;
import actions.DrawTrainCardAction.DrawSource;
import game.GameManager;
import game.GameStateListener;
import model.cards.CardColour;
import model.cards.TrainCard;
import model.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Right rail: ticket deck button, 5 face-up train card slots, and the blind draw pile.
 * Face-up cards show their actual card images from the resources/img folder where available.
 */
public class RightRailPanel extends JPanel implements GameStateListener {

    // Maps config cart_type strings to Java CardColour enum constants
    private static final Map<String, CardColour> CONFIG_TO_COLOUR = Map.of(
        "ORANGE",     CardColour.ORANGE,
        "YELLOW",     CardColour.YELLOW,
        "GREEN",      CardColour.GREEN,
        "BLUE",       CardColour.BLUE,
        "PURPLE",     CardColour.PURPLE,
        "BLACK",      CardColour.BLACK,
        "LOCOMOTIVE", CardColour.WILD
    );

    private final Runnable              onDrawTickets;
    private       Runnable              onEndTurn;
    private final Map<CardColour, Image> cardImages;

    public RightRailPanel(Runnable onDrawTickets) {
        this.onDrawTickets = onDrawTickets;
        this.cardImages    = loadCardImages();
        setOpaque(false);
        setPreferredSize(new Dimension(110, 0));
        GameManager.getInstance().addListener(this);
        rebuild();
    }

    public void setOnEndTurn(Runnable r) { this.onEndTurn = r; }

    @Override
    public void onStateChanged() { rebuild(); revalidate(); repaint(); }

    // ── Panel assembly ────────────────────────────────────────────────────────

    private void rebuild() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(16, 4, 16, 12));

        add(makeTicketDeck());
        add(Box.createVerticalStrut(6));

        List<TrainCard> slots = GameManager.getInstance().getFaceUpPool().getSlots();
        for (int i = 0; i < slots.size(); i++) {
            add(makeFaceUpCard(slots.get(i), i));
            add(Box.createVerticalStrut(4));
        }

        add(makeDeckCard());
    }

    // ── Card panels ───────────────────────────────────────────────────────────

    private JPanel makeTicketDeck() {
        JPanel card = makeCardShell(UiTheme.MAP_CREAM);
        card.setLayout(new BorderLayout());
        JLabel lbl = new JLabel("<html><center>🎫<br>Tickets</center></html>", SwingConstants.CENTER);
        lbl.setFont(UiTheme.body(10));
        lbl.setForeground(UiTheme.INK);
        card.add(lbl, BorderLayout.CENTER);
        card.add(badge(GameManager.getInstance().getTicketDeck().size(), UiTheme.ACCENT_RED),
            BorderLayout.SOUTH);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onDrawTickets != null) onDrawTickets.run();
            }
        });
        return card;
    }

    private JPanel makeFaceUpCard(TrainCard card, int index) {
        Color fill   = card != null ? UiTheme.routeColour(card.getColour()) : UiTheme.PANEL_BG;
        Image img    = card != null ? cardImages.get(card.getColour()) : null;
        String label = card != null ? (card.isWild() ? "WILD" : card.getColour().badge()) : "";

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int w = getWidth(), h = getHeight();
                Shape clip = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 6, 6);

                if (img != null) {
                    g2.setClip(clip);
                    g2.drawImage(img, 0, 0, w, h, null);
                    g2.setClip(null);
                    // Top-left badge overlay
                    g2.setColor(new Color(0, 0, 0, 140));
                    g2.fillRoundRect(3, 3, 32, 14, 4, 4);
                    g2.setFont(UiTheme.mono(8));
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, 6, 13);
                } else {
                    g2.setColor(fill);
                    g2.fill(clip);
                    if (card != null) {
                        g2.setFont(UiTheme.mono(10));
                        g2.setColor(UiTheme.inkOn(fill));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(label, w / 2 - fm.stringWidth(label) / 2, h / 2 + fm.getAscent() / 2 - 2);
                    }
                }
                // Border
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(clip);
            }
        };
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(96, 56));
        panel.setPreferredSize(new Dimension(96, 56));
        panel.setCursor(card != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                     : Cursor.getDefaultCursor());

        final int idx = index;
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (card == null) return;
                Player you = GameManager.getInstance().getCurrentPlayer();
                DrawTrainCardAction action = new DrawTrainCardAction(
                    you, List.of(DrawSource.values()[idx]),
                    GameManager.getInstance().getTrainDeck(),
                    GameManager.getInstance().getFaceUpPool());
                GameManager.getInstance().submitAction(action);
            }
        });
        return panel;
    }

    private JPanel makeDeckCard() {
        JPanel card = makeCardShell(UiTheme.PANEL_BG);
        card.setLayout(new BorderLayout());
        JLabel lbl = new JLabel("DECK", SwingConstants.CENTER);
        lbl.setFont(UiTheme.mono(10));
        lbl.setForeground(new Color(0xFF, 0xFF, 0xFF, 150));
        card.add(lbl, BorderLayout.CENTER);
        card.add(badge(GameManager.getInstance().getTrainDeck().size(), UiTheme.PANEL_BORDER),
            BorderLayout.SOUTH);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                Player you = GameManager.getInstance().getCurrentPlayer();
                DrawTrainCardAction action = new DrawTrainCardAction(
                    you, List.of(DrawSource.BLIND_DECK, DrawSource.BLIND_DECK),
                    GameManager.getInstance().getTrainDeck(),
                    GameManager.getInstance().getFaceUpPool());
                GameManager.getInstance().submitAction(action);
            }
        });
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPanel makeCardShell(Color bg) {
        JPanel p = new JPanel();
        p.setBackground(bg);
        p.setOpaque(true);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xFF, 0xFF, 0xFF, 40), 1, true),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        p.setMaximumSize(new Dimension(96, 56));
        p.setPreferredSize(new Dimension(96, 56));
        return p;
    }

    private JLabel badge(int count, Color bg) {
        JLabel l = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        l.setFont(UiTheme.mono(9));
        l.setForeground(Color.WHITE);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        return l;
    }

    private static Map<CardColour, Image> loadCardImages() {
        Map<CardColour, Image> map = new EnumMap<>(CardColour.class);
        ConfigLoader cfg = ConfigLoader.get();
        cfg.getCardImagePaths().forEach((type, path) -> {
            CardColour colour = CONFIG_TO_COLOUR.get(type);
            if (colour == null) return;
            Image img = cfg.loadImage(path);
            if (img != null) map.put(colour, img);
        });
        return map;
    }
}
