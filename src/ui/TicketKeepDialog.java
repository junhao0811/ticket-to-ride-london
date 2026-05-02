package ui;

import model.cards.DestinationTicketCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Modal shown after drawing destination tickets.
 * Player sees high-quality card images and clicks to select which to keep.
 */
public class TicketKeepDialog extends JDialog {

    private static final Color OVERLAY_BG = new Color(0, 0, 0, 160);
    private static final Color TEAL_BTN   = new Color(0x1A, 0x8A, 0x9E);
    private static final Color TEAL_HOVER = new Color(0x22, 0xA6, 0xBD);

    private final List<DestinationTicketCard> drawn;
    private final int                         minKeep;
    private final Set<Integer>                selectedIndices = new HashSet<>();
    private final JPanel                      cardsPanel;

    private final boolean                     isInitial;

    public TicketKeepDialog(Frame parent, List<DestinationTicketCard> drawn,
                            int minKeep, boolean isInitial, Consumer<List<DestinationTicketCard>> onConfirm) {
        super(parent, true);
        this.drawn = drawn;
        this.minKeep = minKeep;
        this.isInitial = isInitial;

        // Default all to selected
        for (int i = 0; i < drawn.size(); i++) selectedIndices.add(i);

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(parent.getSize());
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(OVERLAY_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.CENTER;

        // ── Title ──
        String titleText = "PICK " + minKeep + " OR MORE TICKETS";
        JLabel title = new JLabel(titleText);
        title.setFont(UiTheme.display(isInitial ? 32 : 24));
        title.setForeground(isInitial ? UiTheme.GOLD : Color.WHITE);
        c.gridy = 0;
        c.insets = new Insets(0, 0, 10, 0);
        root.add(title, c);

        if (isInitial) {
            JLabel sub = new JLabel("Select your starting routes to begin the game");
            sub.setFont(UiTheme.body(16));
            sub.setForeground(new Color(0xAA, 0xBB, 0xCC));
            c.gridy = 1;
            c.insets = new Insets(0, 0, 40, 0);
            root.add(sub, c);
        }

        // ── Cards list ──
        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        cardsPanel.setOpaque(false);
        rebuildCards();

        c.gridy = 2;
        c.insets = new Insets(0, 0, 40, 0);
        root.add(cardsPanel, c);

        // ── Confirm button ──
        JButton confirm = new JButton(isInitial ? "START JOURNEY" : "CONTINUE PLAYING") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean rolled = getModel().isRollover();
                g2.setColor(rolled ? TEAL_HOVER : TEAL_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        confirm.setPreferredSize(new Dimension(320, 54));
        confirm.setFont(UiTheme.display(16));
        confirm.setContentAreaFilled(false);
        confirm.setBorderPainted(false);
        confirm.setFocusPainted(false);
        confirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirm.addActionListener(e -> {
            if (selectedIndices.size() < minKeep) {
                return; // Interaction feedback handled by card visuals
            }
            List<DestinationTicketCard> kept = new ArrayList<>();
            for (int idx : selectedIndices) kept.add(drawn.get(idx));
            onConfirm.accept(kept);
            dispose();
        });

        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        root.add(confirm, c);

        setContentPane(root);
    }

    private void rebuildCards() {
        cardsPanel.removeAll();
        for (int i = 0; i < drawn.size(); i++) {
            cardsPanel.add(makeTicketCard(drawn.get(i), i));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel makeTicketCard(DestinationTicketCard t, int index) {
        ConfigLoader                  cfg   = ConfigLoader.get();
        ConfigLoader.TicketImageEntry match = cfg.findTicketImage(t.getCityA(), t.getCityB());
        Image img = match != null ? cfg.loadImage(match.imagePath()) : null;

        boolean selected = selectedIndices.contains(index);
        Dimension cardSize = new Dimension(280, 170);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth(), h = getHeight();
                Shape clip = new RoundRectangle2D.Float(6, 6, w - 12, h - 12, 14, 14);

                if (selected) {
                    // Thick glowing border for selection
                    g2.setColor(new Color(0x3A, 0xBD, 0xD0)); // Teal highlight
                    g2.setStroke(new BasicStroke(5f));
                    g2.draw(new RoundRectangle2D.Float(3, 3, w - 6, h - 6, 16, 16));
                }

                if (img != null) {
                    g2.setClip(clip);
                    g2.drawImage(img, 6, 6, w - 12, h - 12, null);
                    g2.setClip(null);
                } else {
                    g2.setColor(new Color(0xF4, 0xEF, 0xDD));
                    g2.fill(clip);
                    g2.setColor(UiTheme.INK);
                    g2.setFont(UiTheme.display(12));
                    g2.drawString(t.getCityA() + " → " + t.getCityB(), 20, 35);
                    g2.setFont(UiTheme.display(24));
                    g2.drawString(String.valueOf(t.getPointValue()), w - 50, h - 25);
                }

                if (!selected) {
                    // Desaturate/darken unselected cards
                    g2.setColor(new Color(0, 0, 0, 110));
                    g2.fill(clip);
                }

                g2.dispose();
            }
        };

        card.setPreferredSize(cardSize);
        card.setMinimumSize(cardSize);
        card.setMaximumSize(cardSize);
        card.setOpaque(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (selectedIndices.contains(index)) {
                    // Only allow deselect if we still meet the minimum
                    if (selectedIndices.size() > minKeep) {
                        selectedIndices.remove(index);
                    }
                } else {
                    selectedIndices.add(index);
                }
                rebuildCards();
            }
        });

        return card;
    }
}
