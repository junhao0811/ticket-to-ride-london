package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main menu screen shown on application startup.
 *
 * <p>Layout: JFrame → JLayeredPane (background layer 0, content layers 100).
 * Collects player names, then calls {@code GameManager.init()} and opens
 * {@link GameWindow}. All game state flows through GameManager only.
 */
public class GameMenuScreen extends JFrame {

  private static final int STAGE_W = 1920;
  private static final int STAGE_H = 1080;

  private final JTextField p1Field;
  private final JTextField p2Field;

  /** Constructs and wires the menu screen. Call {@code setVisible(true)} to show. */
  public GameMenuScreen() {
    super("Ticket to Ride London");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setExtendedState(MAXIMIZED_BOTH);

    p1Field = nameField("Amelia");
    p2Field = nameField("Otis");

    JPanel bg        = buildBg();
    JPanel center    = buildCenter();
    JPanel bottomBar = buildBottomBar();

    JLayeredPane layered = new JLayeredPane() {
      @Override
      public void doLayout() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        double s   = Math.min((double) w / STAGE_W, (double) h / STAGE_H);
        int sw     = (int) (STAGE_W * s);
        int sh     = (int) (STAGE_H * s);
        int ox     = (w - sw) / 2;
        int oy     = (h - sh) / 2;
        int topH   = (int) (sh * 0.075);
        int botH   = (int) (sh * 0.145);

        bg.setBounds(ox, oy, sw, sh);
        center.setBounds(ox, oy + topH, sw, sh - topH - botH);
        bottomBar.setBounds(ox, oy + sh - botH, sw, botH);
      }
    };

    layered.add(bg,        JLayeredPane.DEFAULT_LAYER);
    layered.add(center,    JLayeredPane.PALETTE_LAYER);
    layered.add(bottomBar, JLayeredPane.PALETTE_LAYER);

    setContentPane(layered);
  }

  // ── Background layer ─────────────────────────────────────────────────────

  private JPanel buildBg() {
    final Image logo = ConfigLoader.get().loadImage("/ticketToRideLondon.png");
    return new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();

        if (logo != null) {
          double imgW = logo.getWidth(null);
          double imgH = logo.getHeight(null);
          double scale = Math.max((double) w / imgW, (double) h / imgH);
          int sw = (int) (imgW * scale);
          int sh = (int) (imgH * scale);
          int ox = (w - sw) / 2;
          int oy = (h - sh) / 2;
          g2.drawImage(logo, ox, oy, sw, sh, null);

          // Subtle dark overlay to ensure readability of UI elements
          g2.setColor(new Color(0, 0, 0, 80));
          g2.fillRect(0, 0, w, h);
        } else {
          // Dark navy-to-near-black vertical gradient
          g2.setPaint(new GradientPaint(0, 0, new Color(0x1A, 0x24, 0x3E),
                                        0, h, new Color(0x0C, 0x10, 0x1E)));
          g2.fillRect(0, 0, w, h);
          // Soft blue radial glow centred above the title area
          if (w > 0 && h > 0) {
            g2.setPaint(new RadialGradientPaint(
                w / 2f, h * 0.38f, w * 0.52f,
                new float[]{0f, 1f},
                new Color[]{new Color(0x28, 0x42, 0x7A, 65), new Color(0, 0, 0, 0)}));
            g2.fillRect(0, 0, w, h);
          }
        }

        // Subtle grid overlay
        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 7));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < w; x += 64) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 64) g2.drawLine(0, y, w, y);
      }
    };
  }
  

  // ── Center content ────────────────────────────────────────────────────────

  private JPanel buildCenter() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.anchor = GridBagConstraints.CENTER;

    Image logo = ConfigLoader.get().loadImage("/ticketToRideLondon.png");
    if (logo == null) {
      c.gridy = 0; c.insets = new Insets(0, 0, 2, 0);
      panel.add(titleLabel("TICKET TO RIDE", Color.WHITE), c);

      c.gridy = 1; c.insets = new Insets(0, 0, 26, 0);
      panel.add(titleLabel("LONDON", UiTheme.GOLD), c);
    } else {
      // Add a spacer to push input fields down if logo is background
      c.gridy = 0;
      c.insets = new Insets(0, 0, 180, 0);
      panel.add(Box.createVerticalStrut(100), c);
    }

    // Gold hairline rule
    JPanel rule = new JPanel() {
      @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        g2.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 0), w / 2f, 0, UiTheme.GOLD));
        g2.fillRect(0, 0, w / 2, 2);
        g2.setPaint(new GradientPaint(w / 2f, 0, UiTheme.GOLD, w, 0, new Color(0, 0, 0, 0)));
        g2.fillRect(w / 2, 0, w / 2, 2);
      }
      @Override public Dimension getPreferredSize() { return new Dimension(500, 2); }
      @Override public boolean isOpaque() { return false; }
    };
    c.gridy = 2; c.insets = new Insets(0, 0, 18, 0);
    panel.add(rule, c);

    c.gridy = 4; c.insets = new Insets(0, 0, 0, 0);
    panel.add(buildInputPanel(), c);

    return panel;
  }

  private JLabel titleLabel(String text, Color colour) {
    JLabel lbl = new JLabel(text, SwingConstants.CENTER);
    lbl.setFont(new Font("Serif", Font.BOLD, 90));
    lbl.setForeground(colour);
    return lbl;
  }

  private JPanel buildInputPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(10, 14, 10, 14);

    c.gridy = 0; c.gridx = 0; panel.add(rowLabel("PLAYER 1"), c);
    c.gridx = 1; panel.add(p1Field, c);

    c.gridy = 1; c.gridx = 0; panel.add(rowLabel("PLAYER 2"), c);
    c.gridx = 1; panel.add(p2Field, c);

    return panel;
  }

  private JLabel rowLabel(String text) {
    JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
    lbl.setFont(UiTheme.mono(14));
    lbl.setForeground(new Color(0xAA, 0xBB, 0xCC));
    lbl.setPreferredSize(new Dimension(160, 50));
    return lbl;
  }

  // ── Bottom button bar ─────────────────────────────────────────────────────

  private JPanel buildBottomBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 32, 0));
    bar.setOpaque(false);
    bar.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
    bar.add(pillButton("SAVED GAMES", false, this::onMyGames));
    bar.add(pillButton("NEW GAME", true,  this::onNewGame));
    return bar;
  }

  /**
   * Creates a pill-shaped button.
   *
   * @param text    button label
   * @param primary {@code true} → red fill; {@code false} → dark navy fill
   * @param action  runnable invoked on click
   */
  private JButton pillButton(String text, boolean primary, Runnable action) {
    Color base   = primary ? UiTheme.ACCENT_RED       : new Color(0x28, 0x38, 0x58);
    Color hov    = primary ? new Color(0xE0, 0x30, 0x36) : new Color(0x34, 0x48, 0x6C);
    Color border = primary ? new Color(0xFF, 0x80, 0x60, 120) : new Color(0x44, 0x5A, 0x80, 120);

    JButton btn = new JButton(text) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int bw = getWidth() - 4;
        int bh = getHeight() - 4;
        boolean pressed = getModel().isPressed();
        boolean rolled  = getModel().isRollover();

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

    btn.setRolloverEnabled(true);
    btn.setFont(UiTheme.display(18));
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setPreferredSize(new Dimension(280, 66));
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.addActionListener(e -> action.run());
    return btn;
  }

  // ── Player name field ─────────────────────────────────────────────────────

  private JTextField nameField(String defaultText) {
    JTextField field = new JTextField(defaultText, 18) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0x18, 0x24, 0x3C));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
      }
    };
    field.setFont(UiTheme.display(15));
    field.setForeground(Color.WHITE);
    field.setCaretColor(UiTheme.GOLD);
    field.setOpaque(false);
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0x3A, 0x52, 0x7A), 1, true),
        BorderFactory.createEmptyBorder(8, 16, 8, 16)));
    field.setPreferredSize(new Dimension(320, 50));
    return field;
  }

  // ── Action handlers ───────────────────────────────────────────────────────

  private void onNewGame() {
    String name1 = p1Field.getText().trim();
    String name2 = p2Field.getText().trim();
    if (name1.isEmpty()) name1 = "Player 1";
    if (name2.isEmpty()) name2 = "Player 2";
    String n1 = name1, n2 = name2;
    new PlayerLobbyScreen(n1, n2).setVisible(true);
    dispose();
  }

  private void onMyGames() {
    JOptionPane.showMessageDialog(
        this,
        "Saved Games will be available in a future update.",
        "Coming Soon",
        JOptionPane.INFORMATION_MESSAGE);
  }
}
