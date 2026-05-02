package ui;

import model.cards.CardColour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Modal full-screen dialog for choosing a player colour.
 * Shows a large avatar preview on the left and 7 colour swatches on the right.
 * Taken colours (already chosen by other players) are shown locked and unclickable.
 */
public class PlayerColorScreen extends JDialog {

  private static final CardColour[] COLOURS = {
      CardColour.BLUE, CardColour.GREEN, CardColour.YELLOW,
      CardColour.BLACK, CardColour.ORANGE, CardColour.PURPLE
  };

  private CardColour selectedColour;
  private final String playerName;
  private final Set<CardColour> takenColours;
  private final Consumer<CardColour> onDone;

  private JPanel avatarPanel;
  private JPanel swatchPanel;

  /**
   * @param owner        parent frame (lobby screen)
   * @param playerName   name shown in the avatar preview
   * @param current      initially selected colour
   * @param takenColours colours locked by other players
   * @param onDone       called with the chosen colour when DONE is clicked
   */
  public PlayerColorScreen(Frame owner, String playerName, CardColour current,
                            Set<CardColour> takenColours, Consumer<CardColour> onDone) {
    super(owner, true);
    this.playerName   = playerName;
    this.selectedColour = current;
    this.takenColours = takenColours;
    this.onDone       = onDone;

    setUndecorated(true);
    setSize(owner.getSize());
    setLocationRelativeTo(owner);

    JLayeredPane layered = buildLayered();
    setContentPane(layered);
  }

  // ── Layout ────────────────────────────────────────────────────────────────

  private JLayeredPane buildLayered() {
    JPanel bg      = buildBg();
    JPanel content = buildContent();

    JLayeredPane layered = new JLayeredPane() {
      @Override public void doLayout() {
        int w = getWidth(), h = getHeight();
        bg.setBounds(0, 0, w, h);
        content.setBounds(0, 0, w, h);
      }
    };
    layered.add(bg,      JLayeredPane.DEFAULT_LAYER);
    layered.add(content, JLayeredPane.PALETTE_LAYER);
    return layered;
  }

  private JPanel buildBg() {
    final Image street = ConfigLoader.get().loadImage("/randomLondonStreet.png");
    return new JPanel() {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();

        if (street != null) {
          double imgW = street.getWidth(null);
          double imgH = street.getHeight(null);
          double scale = Math.max((double) w / imgW, (double) h / imgH);
          int sw = (int) (imgW * scale);
          int sh = (int) (imgH * scale);
          int ox = (w - sw) / 2;
          int oy = (h - sh) / 2;
          g2.drawImage(street, ox, oy, sw, sh, null);

          // Dark overlay for readability
          g2.setColor(new Color(0, 0, 0, 110));
          g2.fillRect(0, 0, w, h);
        } else {
          g2.setPaint(new GradientPaint(0, 0, new Color(0x1A, 0x24, 0x3E),
                                        0, h, new Color(0x0C, 0x10, 0x1E)));
          g2.fillRect(0, 0, w, h);
          if (w > 0 && h > 0) {
            g2.setPaint(new RadialGradientPaint(
                w / 2f, h * 0.4f, w * 0.5f,
                new float[]{0f, 1f},
                new Color[]{new Color(0x28, 0x42, 0x7A, 55), new Color(0, 0, 0, 0)}));
            g2.fillRect(0, 0, w, h);
          }
        }
      }
    };
  }

  private JPanel buildContent() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);

    GridBagConstraints c = new GridBagConstraints();

    // Title
    JLabel title = new JLabel("CHOOSE YOUR COLOUR");
    title.setFont(UiTheme.mono(22));
    title.setForeground(Color.WHITE);
    c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
    c.insets = new Insets(0, 0, 40, 0);
    c.anchor = GridBagConstraints.CENTER;
    panel.add(title, c);

    // Avatar preview (left)
    avatarPanel = buildAvatarPanel();
    c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
    c.insets = new Insets(0, 0, 0, 48);
    c.anchor = GridBagConstraints.CENTER;
    panel.add(avatarPanel, c);

    // Swatch grid (right)
    swatchPanel = buildSwatchPanel();
    c.gridx = 1; c.gridy = 1; c.gridwidth = 1;
    c.insets = new Insets(0, 0, 0, 0);
    panel.add(swatchPanel, c);

    // Buttons row
    c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
    c.insets = new Insets(44, 0, 0, 0);
    panel.add(buildButtonRow(), c);

    return panel;
  }

  // ── Avatar preview ────────────────────────────────────────────────────────

  private JPanel buildAvatarPanel() {
    JPanel p = new JPanel() {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        Color col = UiTheme.routeColour(selectedColour);
        int r = 70;
        int cx = w / 2, cy = h / 2 - 16;

        // Avatar circle
        g2.setColor(col);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Initial letter
        String initial = playerName.isEmpty() ? "?" :
            String.valueOf(playerName.charAt(0)).toUpperCase();
        g2.setFont(new Font("SansSerif", Font.BOLD, 54));
        g2.setColor(UiTheme.inkOn(col));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(initial,
            cx - fm.stringWidth(initial) / 2,
            cy - fm.getHeight() / 2 + fm.getAscent());

        // Player name below
        g2.setFont(UiTheme.display(16));
        g2.setColor(col);
        FontMetrics nfm = g2.getFontMetrics();
        g2.drawString(playerName,
            cx - nfm.stringWidth(playerName) / 2,
            cy + r + 28);

        g2.dispose();
      }

      @Override public Dimension getPreferredSize() { return new Dimension(220, 240); }
    };
    p.setOpaque(false);
    return p;
  }

  // ── Swatch grid ───────────────────────────────────────────────────────────

  private JPanel buildSwatchPanel() {
    // 4 columns, 2 rows; last cell is intentionally blank
    JPanel grid = new JPanel(new GridLayout(2, 4, 16, 16));
    grid.setOpaque(false);

    for (int i = 0; i < 8; i++) {
      if (i < COLOURS.length) {
        grid.add(makeSwatch(COLOURS[i]));
      } else {
        JPanel blank = new JPanel();
        blank.setOpaque(false);
        grid.add(blank);
      }
    }
    return grid;
  }

  private JPanel makeSwatch(CardColour colour) {
    JPanel swatch = new JPanel() {
      private boolean hovered = false;

      {
        addMouseListener(new MouseAdapter() {
          @Override public void mouseEntered(MouseEvent e) {
            if (!takenColours.contains(colour)) { hovered = true; repaint(); }
          }
          @Override public void mouseExited(MouseEvent e)  {
            hovered = false; repaint();
          }
          @Override public void mouseClicked(MouseEvent e) {
            if (!takenColours.contains(colour)) {
              selectedColour = colour;
              avatarPanel.repaint();
              swatchPanel.repaint();
            }
          }
        });
      }

      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int r = Math.min(w, h) - 8;
        int ox = (w - r) / 2, oy = (h - r) / 2;

        Color base = UiTheme.routeColour(colour);
        boolean taken    = takenColours.contains(colour);
        boolean selected = colour == selectedColour;

        // Dim taken colours
        Color fill = taken ? new Color(base.getRed(), base.getGreen(), base.getBlue(), 90) : base;
        g2.setColor(fill);
        g2.fillOval(ox, oy, r, r);

        if (selected && !taken) {
          // Gold selection ring
          g2.setColor(UiTheme.GOLD);
          g2.setStroke(new BasicStroke(4f));
          g2.drawOval(ox - 4, oy - 4, r + 8, r + 8);
          // White checkmark badge
          int bR = 12;
          int bx = ox + r - bR, by = oy - bR;
          g2.setColor(Color.WHITE);
          g2.fillOval(bx, by, bR * 2, bR * 2);
          g2.setColor(new Color(0x2C, 0x8C, 0x4D));
          g2.setFont(new Font("SansSerif", Font.BOLD, 14));
          FontMetrics fm = g2.getFontMetrics();
          String check = "✓";
          g2.drawString(check, bx + bR - fm.stringWidth(check) / 2, by + bR + fm.getAscent() / 2);
        } else if (hovered && !taken) {
          // Hover ring
          g2.setColor(new Color(255, 255, 255, 160));
          g2.setStroke(new BasicStroke(2f));
          g2.drawOval(ox, oy, r, r);
        } else if (taken) {
          // Lock icon centred
          g2.setFont(new Font("Dialog", Font.PLAIN, 22));
          FontMetrics fm = g2.getFontMetrics();
          String lock = "🔒";
          g2.setColor(new Color(255, 255, 255, 180));
          g2.drawString(lock,
              ox + (r - fm.stringWidth(lock)) / 2,
              oy + (r - fm.getHeight()) / 2 + fm.getAscent());
        }
        g2.dispose();
      }

      @Override public Dimension getPreferredSize() { return new Dimension(90, 90); }
    };
    swatch.setOpaque(false);
    if (!takenColours.contains(colour)) {
      swatch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    return swatch;
  }

  // ── Button row ────────────────────────────────────────────────────────────

  private JPanel buildButtonRow() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
    bar.setOpaque(false);
    bar.add(pillButton("BACK", false, this::dispose));
    bar.add(pillButton("DONE", true,  () -> {
      onDone.accept(selectedColour);
      dispose();
    }));
    return bar;
  }

  private JButton pillButton(String text, boolean primary, Runnable action) {
    Color base   = primary ? UiTheme.ACCENT_RED       : new Color(0x28, 0x38, 0x58);
    Color hov    = primary ? new Color(0xE0, 0x30, 0x36) : new Color(0x34, 0x48, 0x6C);
    Color border = primary ? new Color(0xFF, 0x80, 0x60, 120) : new Color(0x44, 0x5A, 0x80, 120);

    JButton btn = new JButton(text) {
      @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int bw = getWidth() - 4, bh = getHeight() - 4;
        boolean pressed = getModel().isPressed();
        boolean rolled  = getModel().isRollover();

        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(4, 4, bw, bh, bh, bh);

        g2.setColor(pressed ? base.darker() : (rolled ? hov : base));
        g2.fillRoundRect(0, 0, bw, bh, bh, bh);

        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 45),
                                      0, bh / 2f, new Color(255, 255, 255, 0)));
        g2.fillRoundRect(2, 2, bw - 4, bh / 2, bh - 4, bh - 4);

        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, bw - 2, bh - 2, bh - 2, bh - 2);

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

    btn.setRolloverEnabled(true);
    btn.setFont(UiTheme.display(17));
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setPreferredSize(new Dimension(220, 60));
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.addActionListener(e -> action.run());
    return btn;
  }
}
