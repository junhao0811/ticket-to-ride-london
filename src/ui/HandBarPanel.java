package ui;

import game.GameManager;
import game.GameStateListener;
import model.board.Route;
import model.cards.TransportationTypes;
import model.cards.TransportationCard;
import model.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Bottom bar showing the player's hand of train cards with actual card images
 * where available. Supports the "Single Selection" route claim flow.
 */
public class HandBarPanel extends JPanel implements GameStateListener {

    private static final TransportationTypes[] ORDER = {
        TransportationTypes.BLUE, TransportationTypes.GREEN, TransportationTypes.YELLOW,
        TransportationTypes.BLACK, TransportationTypes.ORANGE, TransportationTypes.PURPLE, TransportationTypes.LOCOMOTIVE
    };

    // Maps config cart_type strings to Java TransportationTypes enum constants
    private static final Map<String, TransportationTypes> CONFIG_TO_COLOUR = Map.of(
        "ORANGE",     TransportationTypes.ORANGE,
        "YELLOW",     TransportationTypes.YELLOW,
        "GREEN",      TransportationTypes.GREEN,
        "BLUE",       TransportationTypes.BLUE,
        "PURPLE",     TransportationTypes.PURPLE,
        "BLACK",      TransportationTypes.BLACK,
        "LOCOMOTIVE", TransportationTypes.LOCOMOTIVE
    );

    private final Player              you;
    private final Map<TransportationTypes, Image> cardImages;
    private       TransportationTypes selectedColor;
    private       Runnable            onConfirm;
    private       Runnable            onCancel;
    private       Consumer<TransportationTypes> onSelectionChanged;
    private       JButton             claimBtn;
    private       Runnable            onEndTurn;

    public HandBarPanel(Player you) {
        this.you        = you;
        this.cardImages = loadCardImages();
        setOpaque(false);
        setPreferredSize(new Dimension(0, 100));
        GameManager.getInstance().addListener(this);
        buildLayout();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Subtle dark wash for consistency with rails
        g.setColor(new Color(0, 0, 0, 12));
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    public void setOnConfirm(Runnable callback) { this.onConfirm = callback; }
    public void setOnCancel(Runnable callback)  { this.onCancel = callback; }
    public void setOnSelectionChanged(Consumer<TransportationTypes> callback) { this.onSelectionChanged = callback; }
    public void setOnEndTurn(Runnable callback) { this.onEndTurn = callback; }

    public void setConfirmVisible(boolean visible) {
        claimBtn.setVisible(visible);
        revalidate();
        repaint();
    }

    public void clearSelection() {
        selectedColor = null;
        setConfirmVisible(false);
        onStateChanged();
    }

    public TransportationTypes getSelectedColor() {
        return selectedColor;
    }

    @Override
    public void onStateChanged() {
        rebuildCards();
        revalidate();
        repaint();
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private void buildLayout() {
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, UiTheme.PANEL_BORDER),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JPanel cardsArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        cardsArea.setOpaque(false);
        cardsArea.setName("cardsArea");
        add(cardsArea, BorderLayout.CENTER);

        claimBtn = makeButton("Claim Route", UiTheme.GOLD, UiTheme.INK);
        claimBtn.setVisible(false);
        claimBtn.addActionListener(e -> { if (onConfirm != null) onConfirm.run(); });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        
        JButton endBtn = makeButton("End Turn", UiTheme.PANEL_BORDER, Color.WHITE);
        endBtn.addActionListener(e -> {
            Player current = GameManager.getInstance().getCurrentPlayer();
            GameManager.getInstance().submitAction(new actions.PauseAction(current));
        });

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

        Player current = GameManager.getInstance().getCurrentPlayer();
        Map<TransportationTypes, Integer> counts = new EnumMap<>(TransportationTypes.class);
        for (TransportationCard card : current.getHand()) counts.merge(card.getColour(), 1, Integer::sum);

        if (counts.isEmpty()) {
            JLabel empty = new JLabel("EMPTY HAND  —  DRAW CARDS FROM THE POOL OR DECK");
            empty.setFont(UiTheme.mono(10));
            empty.setForeground(new Color(0xFF, 0xFF, 0xFF, 80));
            panel.add(empty);
        } else {
            for (TransportationTypes c : ORDER) {
                Integer cnt = counts.get(c);
                if (cnt != null) {
                    panel.add(makeHandCard(c, cnt));
                }
            }
        }
    }

    // ── Card tile rendering ───────────────────────────────────────────────────

    private JPanel makeHandCard(TransportationTypes colour, int count) {
        Image  img = cardImages.get(colour);
        Color  bg  = UiTheme.routeColour(colour);
        Color  ink = UiTheme.inkOn(bg);
        boolean selected = (colour == selectedColor);

        JPanel panel = new JPanel() {
            { setPreferredSize(new Dimension(100, 72)); setOpaque(false); }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int w = getWidth(), h = getHeight();

                if (selected) {
                    g2.rotate(Math.toRadians(-5), w / 2.0, h / 2.0);
                    g2.setColor(new Color(0x2C, 0x8C, 0x4D)); // Accent green
                    g2.fillRoundRect(-2, -2, w + 4, h + 4, 10, 10);
                }

                Shape clip = new RoundRectangle2D.Float(0, 0, w, h, 8, 8);

                if (img != null) {
                    g2.setClip(clip);
                    g2.drawImage(img, 0, 0, w, h, null);
                    g2.setClip(null);
                    GradientPaint grad = new GradientPaint(
                        0, h * 0.45f, new Color(0, 0, 0, 0),
                        0, h,         new Color(0, 0, 0, 210));
                    g2.setPaint(grad);
                    g2.fill(clip);
                } else {
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
                g2.setColor(selected ? new Color(0x2C, 0x8C, 0x4D) : new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(selected ? 3f : 1f));
                g2.draw(clip);
                g2.dispose();
            }
        };

        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selectedColor == colour) {
                    selectedColor = null;
                } else {
                    selectedColor = colour;
                }
                setConfirmVisible(false); // Hide tick/cross when selection changes
                if (onSelectionChanged != null) {
                    onSelectionChanged.accept(selectedColor);
                }
                onStateChanged();
            }
        });

        return panel;
    }

    // ── Buttons ───────────────────────────────────────────────────────────────

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

    private JButton makeIconButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(44, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static Map<TransportationTypes, Image> loadCardImages() {
        Map<TransportationTypes, Image> map = new EnumMap<>(TransportationTypes.class);
        ConfigLoader cfg = ConfigLoader.get();
        cfg.getCardImagePaths().forEach((type, path) -> {
            TransportationTypes colour = CONFIG_TO_COLOUR.get(type);
            if (colour == null) return;
            Image img = cfg.loadImage(path);
            if (img != null) map.put(colour, img);
        });
        return map;
    }
}
