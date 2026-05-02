package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A styled modal dialog for displaying game help/tutorials.
 * Matches the aesthetic of the "Objective" and "Turn Actions" screenshots.
 */
public class HelpDialog extends JDialog {

    private static final Color HEADER_BLUE = new Color(0x3B, 0x59, 0x98);
    private static final Color TEAL_BTN    = new Color(0x1A, 0x8A, 0x9E);
    private static final Color TEAL_HOVER  = new Color(0x22, 0xA6, 0xBD);

    public HelpDialog(Frame owner, String title, String contentHtml, Image image) {
        super(owner, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparent for rounded corners

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ── Header ──
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(HEADER_BLUE);
                // Top-only rounded corners for header
                Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 30, 30, 30);
                g2.setClip(clip);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 60));

        JLabel titleLabel = new JLabel("🎓  " + title);
        titleLabel.setFont(UiTheme.display(18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        // ── Content ──
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        if (contentHtml != null && !contentHtml.isEmpty()) {
            JEditorPane text = new JEditorPane("text/html", 
                "<html><body style='font-family: sans-serif; font-size: 13px; color: #333; margin: 20px; line-height: 1.5;'>" 
                + contentHtml + "</body></html>");
            text.setEditable(false);
            text.setOpaque(false);
            text.setFocusable(false);
            content.add(text);
        }

        if (image != null) {
            JLabel imgLabel = new JLabel(new ImageIcon(image.getScaledInstance(500, -1, Image.SCALE_SMOOTH)));
            imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(imgLabel);
            content.add(Box.createVerticalStrut(20));
        }

        // ── Footer with button ──
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        footer.setOpaque(false);
        
        JButton continueBtn = new JButton("CONTINUE PLAYING") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean rolled = getModel().isRollover();
                g2.setColor(rolled ? TEAL_HOVER : TEAL_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText()))/2, (getHeight() - fm.getHeight())/2 + fm.getAscent());
                g2.dispose();
            }
        };
        continueBtn.setPreferredSize(new Dimension(280, 44));
        continueBtn.setFont(UiTheme.display(14));
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setContentAreaFilled(false);
        continueBtn.setBorderPainted(false);
        continueBtn.setFocusPainted(false);
        continueBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        continueBtn.addActionListener(e -> dispose());
        footer.add(continueBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        add(root);
        setSize(600, image != null ? 580 : 480);
        setLocationRelativeTo(owner);
    }

    public HelpDialog(Frame owner, String title, String contentHtml) {
        this(owner, title, contentHtml, null);
    }

    public static void showObjective(Frame owner) {
        String html = "<p>Your goal is to earn the most points by claiming routes and completing Tickets. "
                    + "Connect the 2 cities shown on a Ticket to complete it and earn the points displayed.</p>"
                    + "<div style='text-align: center; margin: 20px;'><span style='font-size: 40px;'>🎫</span></div>"
                    + "<p style='color: #A00; font-weight: bold; text-align: center;'>"
                    + "Be careful - any incomplete Tickets will deduct their point value from your final score!</p>";
        new HelpDialog(owner, "OBJECTIVE AND TICKETS", html).setVisible(true);
    }

    public static void showTurnActions(Frame owner) {
        String html = "<p>On each turn you can take an action. The main actions are:</p>"
                    + "<ul>"
                    + "<li><b>Take 2 Transport cards</b> from the face-up display or the deck (or 1 from each).</li>"
                    + "<li><b>Claim a route</b> using Transport cards. You must use as many cards as there are sections on the route, "
                    + "and they must match the color of the route.</li>"
                    + "<li><b>Draw 3 new Tickets</b>. You must keep at least 1. Remember - incomplete tickets are worth negative points!</li>"
                    + "</ul>";
        new HelpDialog(owner, "TURN ACTIONS", html).setVisible(true);
    }

    public static void showTicketsRule(Frame owner) {
        String html = "<p>As a turn action you can draw 3 new Tickets from the deck. You must keep at least 1.</p>"
                    + "<div style='text-align: center; margin: 20px;'><span style='font-size: 40px;'>🎫 🚂</span></div>"
                    + "<p>Remember, incomplete Tickets are worth negative points!</p>";
        new HelpDialog(owner, "TICKETS", html).setVisible(true);
    }

    public static void showScoringGuide(Frame owner) {
        String html = "<p>Earn points by claiming routes of various lengths. "
                    + "This point scoring guide shows exactly how many points you'll receive for each route you claim:</p>";
        
        Image img = null;
        try {
            img = javax.imageio.ImageIO.read(HelpDialog.class.getResourceAsStream("/pointScoringGuide.png"));
        } catch (Exception e) {
            System.err.println("Could not load scoring guide image: " + e.getMessage());
        }

        new HelpDialog(owner, "SCORING GUIDE", html, img).setVisible(true);
    }
}
