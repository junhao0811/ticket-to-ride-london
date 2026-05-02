package ui;

import game.GameManager;
import model.cards.CardColour;
import model.player.Player;
import variant.LondonVariant;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Player-lobby screen shown between the main menu and the game board.
 * Displays 2 filled player slots and 3 empty placeholder slots.
 * Players can edit their name's colour here before starting the game.
 */
public class PlayerLobbyScreen extends JFrame {

  private static final int STAGE_W = 1920;
  private static final int STAGE_H = 1080;

  /** Lightweight config holder — actual Player objects are created only on START GAME. */
  private static class PlayerConfig {
    String name;
    CardColour colour;

    PlayerConfig(String name, CardColour colour) {
      this.name   = name;
      this.colour = colour;
    }
  }

  private final PlayerConfig[] configs;
  private final JPanel cardsRow;

  /**
   * @param name1 display name for player 1 (HOST)
   * @param name2 display name for player 2 (GUEST)
   */
  public PlayerLobbyScreen(String name1, String name2) {
    super("Ticket to Ride — London Lines");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setExtendedState(MAXIMIZED_BOTH);

    configs = new PlayerConfig[]{
        new PlayerConfig(name1, CardColour.ORANGE),
        new PlayerConfig(name2, CardColour.BLUE)
    };

    cardsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
    cardsRow.setOpaque(false);
    populateCardsRow();

    JPanel bg     = buildBg();
    JPanel topBar = buildTopBar();
    JPanel center = buildCenter();
    JPanel botBar = buildBottomBar();

    JLayeredPane layered = new JLayeredPane() {
      @Override public void doLayout() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        double s  = Math.min((double) w / STAGE_W, (double) h / STAGE_H);
        int sw = (int) (STAGE_W * s), sh = (int) (STAGE_H * s);
        int ox = (w - sw) / 2,        oy = (h - sh) / 2;
        int topH = (int) (sh * 0.075);
        int botH = (int) (sh * 0.145);

        bg.setBounds(ox, oy, sw, sh);
        topBar.setBounds(ox, oy, sw, topH);
        center.setBounds(ox, oy + topH, sw, sh - topH - botH);
        botBar.setBounds(ox, oy + sh - botH, sw, botH);
      }
    };
    layered.add(bg,     JLayeredPane.DEFAULT_LAYER);
    layered.add(topBar, JLayeredPane.PALETTE_LAYER);
    layered.add(center, JLayeredPane.PALETTE_LAYER);
    layered.add(botBar, JLayeredPane.PALETTE_LAYER);

    setContentPane(layered);
  }

  // ── Cards row ─────────────────────────────────────────────────────────────

  private void populateCardsRow() {
    cardsRow.removeAll();
    for (int i = 0; i < 2; i++) {
      cardsRow.add(makeFilledSlot(i));
    }
    for (int i = 0; i < 3; i++) {
      cardsRow.add(makeEmptySlot());
    }
  }

  private void rebuildCards() {
    populateCardsRow();
    cardsRow.revalidate();
    cardsRow.repaint();
  }

  // ── Filled player slot ────────────────────────────────────────────────────

  private JPanel makeFilledSlot(int idx) {
    PlayerConfig cfg = configs[idx];
    Color avatarCol  = UiTheme.routeColour(cfg.colour);

    JPanel card = new JPanel(null) {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        // White-ish card background
        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 18));
        g2.fillRoundRect(0, 0, w, h, 20, 20);
        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);
        g2.dispose();
      }
    };
    card.setOpaque(false);
    card.setPreferredSize(new Dimension(220, 360));

    // ── Name + pencil row ──
    JLabel nameLabel = new JLabel(cfg.name);
    nameLabel.setFont(UiTheme.display(15));
    nameLabel.setForeground(Color.WHITE);
    nameLabel.setBounds(16, 16, 148, 30);

    JButton pencilBtn = new JButton("✏") {
      @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getModel().isRollover()) {
          g2.setColor(new Color(0xFF, 0xFF, 0xFF, 40));
          g2.fillOval(0, 0, getWidth(), getHeight());
        }
        g2.setFont(getFont());
        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 180));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(getText(),
            (getWidth()  - fm.stringWidth(getText())) / 2,
            (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
      }
      @Override public boolean isOpaque() { return false; }
    };
    pencilBtn.setFont(UiTheme.display(14));
    pencilBtn.setContentAreaFilled(false);
    pencilBtn.setBorderPainted(false);
    pencilBtn.setFocusPainted(false);
    pencilBtn.setRolloverEnabled(true);
    pencilBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    pencilBtn.setBounds(172, 14, 34, 34);
    pencilBtn.addActionListener(e -> openColorScreen(idx));

    // ── Avatar circle ──
    JPanel avatar = new JPanel() {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        int r = 54;
        int cx = w / 2, cy = h / 2;
        g2.setColor(avatarCol);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        String initial = cfg.name.isEmpty() ? "?"
            : String.valueOf(cfg.name.charAt(0)).toUpperCase();
        g2.setFont(new Font("SansSerif", Font.BOLD, 44));
        g2.setColor(UiTheme.inkOn(avatarCol));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(initial, cx - fm.stringWidth(initial) / 2,
                      cy - fm.getHeight() / 2 + fm.getAscent());
        g2.dispose();
      }
      @Override public Dimension getPreferredSize() { return new Dimension(220, 160); }
    };
    avatar.setOpaque(false);
    avatar.setBounds(0, 56, 220, 230);

    // ── HOST / GUEST label ──
    String roleText = idx == 0 ? "HOST" : "GUEST";
    JLabel roleLabel = new JLabel(roleText, SwingConstants.CENTER);
    roleLabel.setFont(UiTheme.mono(13));
    roleLabel.setForeground(new Color(0xAA, 0xBB, 0xCC));
    roleLabel.setBounds(0, 310, 220, 36);

    card.add(nameLabel);
    card.add(pencilBtn);
    card.add(avatar);
    card.add(roleLabel);
    return card;
  }

  // ── Empty placeholder slot ────────────────────────────────────────────────

  private JPanel makeEmptySlot() {
    JPanel card = new JPanel(null) {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 8));
        g2.fillRoundRect(0, 0, w, h, 20, 20);
        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 28));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                                     1f, new float[]{6f, 4f}, 0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);
        g2.dispose();
      }
    };
    card.setOpaque(false);
    card.setPreferredSize(new Dimension(220, 360));

    JButton addPerson = tealButton("＋  ADD PERSON");
    JButton addAi     = tealButton("＋  ADD AI");

    addPerson.setBounds(22, 120, 176, 48);
    addAi.setBounds(22, 184, 176, 48);

    card.add(addPerson);
    card.add(addAi);
    return card;
  }

  private JButton tealButton(String text) {
    Color base = new Color(0x1A, 0x7A, 0x6E);
    Color hov  = new Color(0x22, 0x96, 0x86);

    JButton btn = new JButton(text) {
      @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int bw = getWidth(), bh = getHeight();
        Color fill = getModel().isRollover() ? hov : base;
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, bw, bh, bh, bh);
        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 60));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, bw - 1, bh - 1, bh - 1, bh - 1);
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
    btn.setFont(UiTheme.mono(12));
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setRolloverEnabled(true);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return btn;
  }

  // ── Action handlers ───────────────────────────────────────────────────────

  private void openColorScreen(int idx) {
    Set<CardColour> taken = new HashSet<>();
    for (int i = 0; i < configs.length; i++) {
      if (i != idx) taken.add(configs[i].colour);
    }
    new PlayerColorScreen(
        this, configs[idx].name, configs[idx].colour, taken,
        chosen -> {
          configs[idx].colour = chosen;
          rebuildCards();
        }
    ).setVisible(true);
  }

  private void onStartGame() {
    Player p1 = new Player(configs[0].name, configs[0].colour);
    Player p2 = new Player(configs[1].name, configs[1].colour);
    GameManager.getInstance().init(new LondonVariant(), List.of(p1, p2));
    dispose();
    SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true));
  }

  private void onBack() {
    dispose();
    SwingUtilities.invokeLater(() -> new GameMenuScreen().setVisible(true));
  }

  // ── Background ────────────────────────────────────────────────────────────

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
                w / 2f, h * 0.38f, w * 0.52f,
                new float[]{0f, 1f},
                new Color[]{new Color(0x28, 0x42, 0x7A, 55), new Color(0, 0, 0, 0)}));
            g2.fillRect(0, 0, w, h);
          }
        }

        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 7));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < w; x += 64) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 64) g2.drawLine(0, y, w, y);
      }
    };
  }

  // ── Top bar ───────────────────────────────────────────────────────────────

  private JPanel buildTopBar() {
    JPanel bar = new JPanel(new BorderLayout());
    bar.setOpaque(false);
    bar.setBorder(BorderFactory.createEmptyBorder(0, 36, 0, 36));

    JLabel brand = new JLabel("🎫  LONDON LINES");
    brand.setFont(UiTheme.mono(13));
    brand.setForeground(new Color(0xFF, 0xFF, 0xFF, 130));
    bar.add(brand, BorderLayout.WEST);

    JLabel edition = new JLabel("EDITION I  ·  FIT3077 SPRINT 2");
    edition.setFont(UiTheme.mono(11));
    edition.setForeground(new Color(0xFF, 0xFF, 0xFF, 60));
    bar.add(edition, BorderLayout.EAST);

    return bar;
  }

  // ── Center ────────────────────────────────────────────────────────────────

  private JPanel buildCenter() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.anchor = GridBagConstraints.CENTER;

    JLabel heading = new JLabel("CHOOSE YOUR PLAYERS");
    heading.setFont(UiTheme.mono(26));
    heading.setForeground(Color.WHITE);
    c.gridy = 0; c.insets = new Insets(0, 0, 32, 0);
    panel.add(heading, c);

    c.gridy = 1; c.insets = new Insets(0, 0, 0, 0);
    panel.add(cardsRow, c);

    return panel;
  }

  // ── Bottom bar ────────────────────────────────────────────────────────────

  private JPanel buildBottomBar() {
    JPanel bar = new JPanel(new BorderLayout());
    bar.setOpaque(false);
    bar.setBorder(BorderFactory.createEmptyBorder(24, 36, 0, 36));

    JLabel rules = new JLabel("RULES");
    rules.setFont(UiTheme.mono(13));
    rules.setForeground(new Color(0xFF, 0xFF, 0xFF, 100));
    rules.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    bar.add(rules, BorderLayout.WEST);

    JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
    btnRow.setOpaque(false);
    btnRow.add(pillButton("BACK",       false, this::onBack));
    btnRow.add(pillButton("START GAME", true,  this::onStartGame));
    bar.add(btnRow, BorderLayout.EAST);

    return bar;
  }

  private JButton pillButton(String text, boolean primary, Runnable action) {
    Color base   = primary ? UiTheme.ACCENT_RED         : new Color(0x28, 0x38, 0x58);
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
    btn.setPreferredSize(new Dimension(220, 62));
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.addActionListener(e -> action.run());
    return btn;
  }
}
