package ui;

import game.GameManager;
import game.GameStateListener;
import model.board.Route;
import model.cards.CardColour;
import model.cards.TrainCard;
import model.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Bottom bar showing the player's hand of train cards with actual card images
 * where available, plus Claim Route and End Turn action buttons.
 */
public class HandBarPanel extends JPanel implements GameStateListener {

    private static final CardColour[] ORDER = {
        CardColour.BLUE, CardColour.GREEN, CardColour.YELLOW,
        CardColour.BLACK, CardColour.ORANGE, CardColour.PURPLE, CardColour.WILD
    };

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

    private final Player              you;
    private final Map<CardColour, Image> cardImages;
    private       Route               selectedRoute;
    private       Consumer<Route>     onClaim;
    private       Runnable            onEndTurn;
    private       JButton             claimBtn;
    private       JButton             endBtn;

    public HandBarPanel(Player you) {
        this.you        = you;
        this.cardImages = loadCardImages();
        setOpaque(true);
        setBackground(new Color(0x0F, 0x16, 0x28));
        setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UiTheme.PANEL_BORDER));
        setPreferredSize(new Dimension(0, 100));
        GameManager.getInstance().addListener(this);
        buildLayout();
    }

    public void setOnClaim(Consumer<Route> callback)  { this.onClaim = callback; }
    public void setOnEndTurn(Runnable callback)        { this.onEndTurn = callback; }

    public void setSelectedRoute(Route route) {
        this.selectedRoute = route;
        updateButtonState();
    }

    @Override
    public void onStateChanged() {
        rebuildCards();
        updateButtonState();
        revalidate();
        repaint();
    }

    // ── Layout ────────────────────────────────────────────────────────��──────

    private void buildLayout() {
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel cardsArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        cardsArea.setOpaque(false);
        cardsArea.setName("cardsArea");
        add(cardsArea, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        claimBtn = makeButton("Claim Route", UiTheme.GOLD, UiTheme.INK);
        claimBtn.setEnabled(false);
        claimBtn.addActionListener(e -> {
            if (selectedRoute != null && onClaim != null) onClaim.accept(selectedRoute);
        });

        endBtn = makeButton("End Turn", UiTheme.ACCENT_RED, Color.WHITE);
        endBtn.addActionListener(e -> { if (onEndTurn != null) onEndTurn.run(); });

        actions.add(claimBtn);
        actions.add(endBtn);
        add(actions, BorderLayout.EAST);

        rebuildCards();
    }

    private void rebuildCards() {
        Component cardsArea = null;
        for (Component c : getComponents()) {
            if ("cardsArea".equals(c.getName())) { cardsArea = c; break; }
        }
        if (!(cardsArea instanceof JPanel panel)) return;
        panel.removeAll();

        Map<CardColour, Integer> counts = new EnumMap<>(CardColour.class);
        for (TrainCard card : you.getHand()) counts.merge(card.getColour(), 1, Integer::sum);

        if (counts.isEmpty()) {
            JLabel empty = new JLabel("EMPTY HAND  —  DRAW CARDS FROM THE POOL OR DECK");
            empty.setFont(UiTheme.mono(10));
            empty.setForeground(new Color(0xFF, 0xFF, 0xFF, 80));
            panel.add(empty);
        } else {
            for (CardColour c : ORDER) {
                Integer cnt = counts.get(c);
                if (cnt != null) panel.add(makeHandCard(c, cnt));
            }
        }
    }

    // ── Card tile rendering ───────────────────────────────────────────────────

    private JPanel makeHandCard(CardColour colour, int count) {
        Image  img = cardImages.get(colour);
        Color  bg  = UiTheme.routeColour(colour);
        Color  ink = UiTheme.inkOn(bg);

        return new JPanel() {
            { setPreferredSize(new Dimension(100, 72)); setOpaque(false); }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int w = getWidth(), h = getHeight();
                Shape clip = new RoundRectangle2D.Float(0, 0, w, h, 8, 8);

                if (img != null) {
                    // Card image with gradient overlay for count badge readability
                    g2.setClip(clip);
                    g2.drawImage(img, 0, 0, w, h, null);
                    g2.setClip(null);
                    GradientPaint grad = new GradientPaint(
                        0, h * 0.45f, new Color(0, 0, 0, 0),
                        0, h,         new Color(0, 0, 0, 210));
                    g2.setPaint(grad);
                    g2.fill(clip);
                } else {
                    // Colour-block fallback
                    g2.setColor(bg);
                    g2.fill(clip);
                    g2.setFont(UiTheme.mono(9));
                    g2.setColor(new Color(ink.getRed(), ink.getGreen(), ink.getBlue(), 140));
                    FontMetrics fm = g2.getFontMetrics();
                    String badge = colour.badge();
                    g2.drawString(badge, w / 2 - fm.stringWidth(badge) / 2, 14);
                }

                // Count badge
                g2.setFont(UiTheme.display(16));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String s = "×" + count;
                g2.drawString(s, w / 2 - fm.stringWidth(s) / 2, h - 8);

                // Border
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(clip);
            }
        };
    }

    // ── Buttons ───────────────────────────��────────────────────────��─────────

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? bg : bg.darker().darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setFont(UiTheme.display(13));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(140, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateButtonState() {
        boolean isYourTurn = GameManager.getInstance().getCurrentPlayer() == you;
        claimBtn.setEnabled(selectedRoute != null && isYourTurn);
        endBtn.setEnabled(isYourTurn);
    }

    // ── Image loading ──────────────────────────────���─────────────────────��───

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
