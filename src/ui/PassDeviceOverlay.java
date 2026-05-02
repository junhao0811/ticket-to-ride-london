package ui;

import model.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * Full-screen overlay shown over the game board after each turn advances.
 * Displays the next player's identity and blocks input to panels beneath until
 * that player clicks READY.
 *
 * <p>Added last to the stage JPanel so it sits above all other children
 * (null-layout z-order = add order).
 */
public class PassDeviceOverlay extends JPanel {

  private Player nextPlayer;
  private final JButton readyBtn;

  /**
   * @param onReady called when the READY button is clicked; caller should hide this overlay
   */
  public PassDeviceOverlay(Runnable onReady) {
    setLayout(null);
    setOpaque(false);

    readyBtn = buildReadyButton(onReady);
    add(readyBtn);

    // Block all mouse events from reaching panels below
    addMouseListener(new MouseAdapter() {});
    addMouseMotionListener(new MouseAdapter() {});
  }

  /** Updates the player whose name and colour are shown, then repaints. */
  public void setNextPlayer(Player p) {
    this.nextPlayer = p;
    repaint();
  }

  @Override
  public void doLayout() {
    int w = getWidth(), h = getHeight();
    if (w <= 0 || h <= 0) return;
    int btnW = 340, btnH = 70;
    readyBtn.setBounds((w - btnW) / 2, (int) (h * 0.75), btnW, btnH);
  }

  @Override
  protected void paintComponent(Graphics g) {
    int w = getWidth(), h = getHeight();
    if (w <= 0 || h <= 0) return;

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    int band1Y = (int) (h * 0.28);
    int band2Y = (int) (h * 0.62);

    // Three horizontal translucent bands
    g2.setColor(new Color(0, 0, 0, 100));
    g2.fillRect(0, 0, w, band1Y);
    g2.setColor(new Color(0, 0, 0, 200));
    g2.fillRect(0, band1Y, w, band2Y - band1Y);
    g2.setColor(new Color(0, 0, 0, 100));
    g2.fillRect(0, band2Y, w, h - band2Y);

    // Gold separator lines
    g2.setColor(UiTheme.GOLD);
    g2.setStroke(new BasicStroke(2.5f));
    g2.drawLine(0, band1Y, w, band1Y);
    g2.drawLine(0, band2Y, w, band2Y);

    if (nextPlayer != null) {
      paintPlayerSection(g2, w, h, band1Y, band2Y);
    }

    g2.dispose();
  }

  private void paintPlayerSection(Graphics2D g2, int w, int h, int band1Y, int band2Y) {
    Color playerColour = UiTheme.playerColour(nextPlayer.getColour());
    String name = nextPlayer.getName();
    String initial = name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase();

    int centreY = (band1Y + band2Y) / 2;

    // Coloured avatar rect
    int rectW = 180, rectH = 180;
    int rx = (w - rectW) / 2;
    int ry = centreY - rectH / 2 - 40;
    g2.setColor(playerColour);
    g2.fillRoundRect(rx, ry, rectW, rectH, 28, 28);

    // Initial letter centred in rect
    g2.setFont(new Font("SansSerif", Font.BOLD, 64));
    g2.setColor(UiTheme.inkOn(playerColour));
    FontMetrics ifm = g2.getFontMetrics();
    g2.drawString(initial,
        rx + (rectW - ifm.stringWidth(initial)) / 2,
        ry + (rectH - ifm.getHeight()) / 2 + ifm.getAscent());

    // Name pill below avatar
    g2.setFont(UiTheme.display(20));
    FontMetrics nfm = g2.getFontMetrics();
    int pillW = Math.max(220, nfm.stringWidth(name) + 48);
    int pillH = 48;
    int px = (w - pillW) / 2;
    int py = ry + rectH + 16;
    g2.setColor(playerColour);
    g2.fillRoundRect(px, py, pillW, pillH, pillH, pillH);
    g2.setColor(UiTheme.inkOn(playerColour));
    g2.drawString(name,
        px + (pillW - nfm.stringWidth(name)) / 2,
        py + (pillH - nfm.getHeight()) / 2 + nfm.getAscent());

    // Instruction text below name pill
    g2.setFont(UiTheme.display(22));
    g2.setColor(Color.WHITE);
    String instruction = "Pass the device to " + name;
    FontMetrics fm = g2.getFontMetrics();
    g2.drawString(instruction, (w - fm.stringWidth(instruction)) / 2, py + pillH + 48);
  }

  private JButton buildReadyButton(Runnable onReady) {
    Color base  = new Color(0x2E, 0x7D, 0x5E);
    Color hover = new Color(0x3A, 0x96, 0x72);

    JButton btn = new JButton("READY") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int bw = getWidth() - 4, bh = getHeight() - 4;
        boolean pressed = getModel().isPressed();
        boolean rolled  = getModel().isRollover();

        // Shadow
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(4, 4, bw, bh, bh, bh);

        // Fill
        g2.setColor(pressed ? base.darker() : (rolled ? hover : base));
        g2.fillRoundRect(0, 0, bw, bh, bh, bh);

        // Sheen
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 45),
                                      0, bh / 2f, new Color(255, 255, 255, 0)));
        g2.fillRoundRect(2, 2, bw - 4, bh / 2, bh - 4, bh - 4);

        // Gold border
        g2.setColor(new Color(UiTheme.GOLD.getRed(), UiTheme.GOLD.getGreen(),
                               UiTheme.GOLD.getBlue(), 180));
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

    btn.setFont(UiTheme.display(22));
    btn.setRolloverEnabled(true);
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.addActionListener(e -> onReady.run());
    return btn;
  }
}
