package ui;

import actions.ActionResult;
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

    private final Runnable               onDrawTickets;
    private       Runnable               onEndTurn;
    private final Map<CardColour, Image> cardImages;
    private final Image                  trainCardBack;
    private final Image                  ticketCardBack;

    /** Non-null while a draw action is mid-way (phase 1 done, phase 2 pending). */
    private DrawTrainCardAction inProgressDraw = null;

    public RightRailPanel(Runnable onDrawTickets) {
        this.onDrawTickets  = onDrawTickets;
        this.cardImages     = loadCardImages();
        this.trainCardBack  = ConfigLoader.get().loadImage("/trainCarCardRear.png");
        this.ticketCardBack = ConfigLoader.get().loadImage("/ticketCardFaceDown.png");
        setOpaque(false);
        setPreferredSize(new Dimension(110, 0));
        GameManager.getInstance().addListener(this);
        rebuild();
    }

    public void setOnEndTurn(Runnable r) { this.onEndTurn = r; }

    @Override
    public void onStateChanged() {
        // Clear stale pending draw if the turn has moved on
        if (inProgressDraw != null
                && GameManager.getInstance().getCurrentPlayer() != inProgressDraw.getPlayer()) {
            inProgressDraw = null;
        }
        rebuild();
        revalidate();
        repaint();
    }

    // ── Panel assembly ────────────────────────────────────────────────────────

    private void rebuild() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 4, 20, 12));

        // Top: Ticket Deck
        add(makeTicketDeck());
        
        add(Box.createVerticalGlue());

        // Middle: Face-up cards
        List<TrainCard> slots = GameManager.getInstance().getFaceUpPool().getSlots();
        for (int i = 0; i < slots.size(); i++) {
            add(makeFaceUpCard(slots.get(i), i));
            if (i < slots.size() - 1) add(Box.createVerticalStrut(10));
        }

        add(Box.createVerticalGlue());

        // Bottom: Train Deck
        add(makeDeckCard());
    }

    // ── Card panels ───────────────────────────────────────────────────────────

    private JPanel makeTicketDeck() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int w = getWidth(), h = getHeight();
                Shape clip = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 8, 8);

                if (ticketCardBack != null) {
                    g2.setClip(clip);
                    g2.drawImage(ticketCardBack, 0, 0, w, h, null);
                    g2.setClip(null);
                } else {
                    g2.setColor(UiTheme.MAP_CREAM);
                    g2.fill(clip);
                }

                // Border
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(clip);
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(96, 62));
        panel.setPreferredSize(new Dimension(96, 62));
        panel.add(badge(GameManager.getInstance().getTicketDeck().size(), UiTheme.ACCENT_RED),
            BorderLayout.SOUTH);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onDrawTickets != null) onDrawTickets.run();
            }
        });
        return panel;
    }

    private JPanel makeFaceUpCard(TrainCard card, int index) {
        Color fill   = card != null ? UiTheme.routeColour(card.getColour()) : UiTheme.PANEL_BG;
        Image img    = card != null ? cardImages.get(card.getColour()) : null;
        String label = card != null ? (card.isWild() ? "WILD" : card.getColour().badge()) : "";
        DrawSource source = DrawSource.values()[index];
        boolean selectable = card != null && canSelect(source);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int w = getWidth(), h = getHeight();
                Shape clip = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 8, 8);

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
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(clip);
            }
        };
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(96, 62));
        panel.setPreferredSize(new Dimension(96, 62));
        panel.setCursor(selectable ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                   : Cursor.getDefaultCursor());

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!selectable) return;
                handleCardClick(source);
            }
        });
        return panel;
    }

    private JPanel makeDeckCard() {
        boolean selectable = canSelect(DrawSource.BLIND_DECK);
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int w = getWidth(), h = getHeight();
                Shape clip = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 8, 8);

                if (trainCardBack != null) {
                    g2.setClip(clip);
                    g2.drawImage(trainCardBack, 0, 0, w, h, null);
                    g2.setClip(null);
                } else {
                    g2.setColor(UiTheme.PANEL_BG);
                    g2.fill(clip);
                }

                // Border
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(clip);
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(96, 56));
        panel.setPreferredSize(new Dimension(96, 56));
        panel.add(badge(GameManager.getInstance().getTrainDeck().size(), UiTheme.PANEL_BORDER),
            BorderLayout.SOUTH);
        panel.setCursor(selectable ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                   : Cursor.getDefaultCursor());
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!selectable) return;
                handleCardClick(DrawSource.BLIND_DECK);
            }
        });
        return panel;
    }

    /**
     * Handles a card-draw click for either a face-up slot or the blind deck.
     * Manages the two-phase draw flow: submits phase 1 on first click and
     * phase 2 on the subsequent click (using the same action instance).
     */
    private void handleCardClick(DrawSource source) {
        GameManager gm  = GameManager.getInstance();
        Player      you = gm.getCurrentPlayer();

        if (inProgressDraw != null && inProgressDraw.awaitingSecondDraw()) {
            if (!inProgressDraw.canUseSourceForCurrentPhase(source)) {
                return;
            }

            // Phase 2: feed second source into the existing action and re-submit
            inProgressDraw.setSecondSource(source);
            ActionResult result = gm.submitAction(inProgressDraw);
            if (result.isSuccess()) {
                inProgressDraw = null;
            }
        } else {
            if (!canSelect(source)) {
                return;
            }

            // Phase 1: start a fresh draw action
            DrawTrainCardAction action = new DrawTrainCardAction(
                    you, source, gm.getTrainDeck(), gm.getFaceUpPool());
            ActionResult result = gm.submitAction(action);
            inProgressDraw = result.isPending() ? action : null;
        }
        refreshCards();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean canSelect(DrawSource source) {
        if (inProgressDraw != null && inProgressDraw.awaitingSecondDraw()) {
            return inProgressDraw.canUseSourceForCurrentPhase(source);
        }

        GameManager gm = GameManager.getInstance();
        if (source == DrawSource.BLIND_DECK) {
            return !gm.getTrainDeck().isCompletelyEmpty();
        }

        int index = source.ordinal();
        List<TrainCard> slots = gm.getFaceUpPool().getSlots();
        return index < slots.size() && slots.get(index) != null;
    }

    private void refreshCards() {
        rebuild();
        revalidate();
        repaint();
    }

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
