package ui;

import actions.ClaimRouteAction;
import actions.DrawDestinationTicketAction;
import actions.PauseAction;
import game.GameManager;
import game.GamePhase;
import game.GameStateListener;
import model.board.Route;
import model.cards.DestinationTicketCard;
import model.player.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Top-level application frame.
 * 1920×1080 logical stage scaled to fit any display.
 * Layout: left rail | board (centre) | right rail, with hand bar at the bottom.
 * All event handlers create PlayerAction commands and submit to GameManager.
 */
public class GameWindow extends JFrame implements GameStateListener {

    private static final int STAGE_W = 1920;
    private static final int STAGE_H = 1080;

    private final JPanel stage;
    private final BoardPanel boardPanel;
    private final LeftRailPanel leftRail;
    private final RightRailPanel rightRail;
    private final HandBarPanel handBar;
    private final ToastOverlay toast;
    private final ScorePanel scorePanel;
    private PassDeviceOverlay passOverlay;
    private Player lastKnownPlayer;
    private Route selectedRoute;

    public GameWindow() {
        super("London Lines");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Player you = GameManager.getInstance().getPlayers().get(0);

        // ── Build panels ───────────────────────────────────────────────────
        boardPanel  = new BoardPanel();
        leftRail    = new LeftRailPanel(you);
        rightRail   = new RightRailPanel(this::drawTickets);
        handBar     = new HandBarPanel(you);
        toast       = new ToastOverlay();
        scorePanel  = new ScorePanel();

        // ── Wire up action callbacks ────────────────────────────────────────
        boardPanel.setOnRouteClick(route -> {
            selectedRoute = route;
            handBar.setSelectedRoute(route);
            if (route.isClaimed()) {
                toast.show("Already claimed by "
                    + route.getClaimedBy().getName() + ".");
                selectedRoute = null;
                handBar.setSelectedRoute(null);
            }
        });

        handBar.setOnClaim(route -> {
            if (route == null) return;
            Player current = GameManager.getInstance().getCurrentPlayer();
            new ClaimDialog(this, route, current, cards -> {
                ClaimRouteAction action = new ClaimRouteAction(
                    current, route, cards,
                    GameManager.getInstance().getTrainDeck(),
                    GameManager.getInstance().getScoreManager());
                var result = GameManager.getInstance().submitAction(action);
                toast.show(result.getMessage());
                selectedRoute = null;
                boardPanel.clearSelection();
                handBar.setSelectedRoute(null);
            }).setVisible(true);
        });

        handBar.setOnEndTurn(() -> {
            Player current = GameManager.getInstance().getCurrentPlayer();
            var result = GameManager.getInstance().submitAction(new PauseAction(current));
            toast.show(result.getMessage());
        });

        // ── Assemble stage (JLayeredPane for proper overlay z-ordering) ────
        final Image street = ConfigLoader.get().loadImage("/randomLondonStreet.png");
        JLayeredPane layeredStage = new JLayeredPane() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                if (w <= 0 || h <= 0) return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (street != null) {
                    double imgW = street.getWidth(null);
                    double imgH = street.getHeight(null);
                    double scale = Math.max((double) w / imgW, (double) h / imgH);
                    int sw = (int) (imgW * scale);
                    int sh = (int) (imgH * scale);
                    int ox = (w - sw) / 2;
                    int oy = (h - sh) / 2;
                    g2.drawImage(street, ox, oy, sw, sh, null);

                    // Dark overlay to ensure UI elements (rails, cards) stand out
                    g2.setColor(new Color(0, 0, 0, 140));
                    g2.fillRect(0, 0, w, h);
                } else {
                    // Fallback: Dark navy-to-near-black vertical gradient
                    g2.setPaint(new GradientPaint(0, 0, new Color(0x1A, 0x24, 0x3E),
                                                  0, h, new Color(0x0C, 0x10, 0x1E)));
                    g2.fillRect(0, 0, w, h);

                    // Soft blue radial glow centred on the board
                    g2.setPaint(new RadialGradientPaint(
                        w / 2f, h * 0.45f, w * 0.6f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0x28, 0x42, 0x7A, 50), new Color(0, 0, 0, 0)}));
                    g2.fillRect(0, 0, w, h);
                }

                // Subtle grid overlay to match menu aesthetic
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 5));
                g2.setStroke(new BasicStroke(0.5f));
                for (int x = 0; x < w; x += 64) g2.drawLine(x, 0, x, h);
                for (int y = 0; y < h; y += 64) g2.drawLine(0, y, w, y);
            }
        };
        layeredStage.setLayout(null);
        layeredStage.setOpaque(true);
        layeredStage.setPreferredSize(new Dimension(STAGE_W, STAGE_H));
        this.stage = new JPanel(new BorderLayout()); // Wrapper for legacy references if any
        this.stage.setOpaque(false);
        this.stage.add(layeredStage);

        // Game components -> DEFAULT_LAYER
        layeredStage.add(leftRail,    JLayeredPane.DEFAULT_LAYER);
        layeredStage.add(boardPanel,  JLayeredPane.DEFAULT_LAYER);
        layeredStage.add(rightRail,   JLayeredPane.DEFAULT_LAYER);
        layeredStage.add(handBar,     JLayeredPane.DEFAULT_LAYER);
        layeredStage.add(scorePanel,  JLayeredPane.DEFAULT_LAYER);
        scorePanel.setVisible(false);

        // Toast -> PALETTE_LAYER (above board)
        layeredStage.add(toast,       JLayeredPane.PALETTE_LAYER);

        // Pass-device overlay -> POPUP_LAYER (highest)
        passOverlay = new PassDeviceOverlay(() -> {
            passOverlay.setVisible(false);
            onStateChanged();
        });
        layeredStage.add(passOverlay, JLayeredPane.POPUP_LAYER);
        passOverlay.setVisible(false);

        // ── Scale wrapper ──────────────────────────────────────────────────
        JPanel scaleWrap = new JPanel(null) {
            @Override public void doLayout() {
                int pw = getWidth(), ph = getHeight();
                double sx = (double) pw / STAGE_W;
                double sy = (double) ph / STAGE_H;
                double s  = Math.min(sx, sy);
                int sw = (int)(STAGE_W * s), sh = (int)(STAGE_H * s);
                int ox = (pw - sw) / 2, oy = (ph - sh) / 2;
                for (Component c : getComponents()) {
                    c.setBounds(ox, oy, sw, sh);
                    if (c == stage) {
                        layoutStage(layeredStage, sw, sh);
                    }
                }
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(STAGE_W, STAGE_H);
            }
        };
        scaleWrap.setOpaque(true);
        scaleWrap.setBackground(new Color(0x0C, 0x10, 0x1E)); // Matches stage bottom color
        scaleWrap.add(stage);

        setContentPane(scaleWrap);

        GameManager.getInstance().addListener(this);
        lastKnownPlayer = GameManager.getInstance().getCurrentPlayer();
        onStateChanged(); // Trigger initial check
    }

    private void layoutStage(JLayeredPane p, int w, int h) {
        int leftW  = 220;
        int rightW = 120;
        int handH  = 100;
        int boardW = w - leftW - rightW;
        int boardH = h - handH;

        leftRail.setBounds(0, 0, leftW, h);
        boardPanel.setBounds(leftW, 0, boardW, boardH);
        rightRail.setBounds(w - rightW, 0, rightW, h);
        handBar.setBounds(leftW, boardH, boardW, handH);
        scorePanel.setBounds(leftW, 0, boardW, boardH);

        int toastW = 480, toastH = 40;
        toast.setBounds((w - toastW) / 2, h - handH - toastH - 12, toastW, toastH);
        passOverlay.setBounds(0, 0, w, h);
    }

    private void drawInitialTickets() {
        Player current = GameManager.getInstance().getCurrentPlayer();
        int minKeep = GameManager.getInstance().getVariant().getMinTicketsKeptInitial();
        
        DrawDestinationTicketAction action = new DrawDestinationTicketAction(
            current,
            GameManager.getInstance().getTicketDeck(),
            minKeep);
        
        List<DestinationTicketCard> drawn = action.peekDrawnTickets();
        if (drawn.isEmpty()) return;

        TicketKeepDialog dialog = new TicketKeepDialog(this, drawn, minKeep, true, kept -> {
            action.setKeptTickets(kept);
            var result = GameManager.getInstance().submitAction(action);
            toast.show("Welcome " + current.getName() + "! Initial tickets picked.");
            isDrawingInitial = false;
        });
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { isDrawingInitial = false; }
        });
        dialog.setVisible(true);
    }

    private void drawTickets() {
        Player current = GameManager.getInstance().getCurrentPlayer();
        DrawDestinationTicketAction action = new DrawDestinationTicketAction(
            current,
            GameManager.getInstance().getTicketDeck(),
            GameManager.getInstance().getVariant().getMinTicketsKeptDraw());
        List<DestinationTicketCard> drawn = action.peekDrawnTickets();
        if (drawn.isEmpty()) {
            toast.show("No tickets remaining in deck.");
            return;
        }
        new TicketKeepDialog(this, drawn,
            GameManager.getInstance().getVariant().getMinTicketsKeptDraw(),
            false,
            kept -> {
                action.setKeptTickets(kept);
                var result = GameManager.getInstance().submitAction(action);
                toast.show(result.getMessage());
            }).setVisible(true);
    }

    private boolean isDrawingInitial = false;

    @Override
    public void onStateChanged() {
        boolean ended = GameManager.getInstance().getPhase() == GamePhase.ENDED;
        boardPanel.setVisible(!ended);
        scorePanel.setVisible(ended);

        Player current = GameManager.getInstance().getCurrentPlayer();
        
        // Handle Pass-Device overlay
        if (!ended && lastKnownPlayer != null && lastKnownPlayer != current) {
            passOverlay.setNextPlayer(current);
            passOverlay.setVisible(true);
        }
        
        // Mandatory initial ticket draw
        if (!ended && !passOverlay.isVisible() && current.getTickets().isEmpty() && !isDrawingInitial) {
            isDrawingInitial = true;
            SwingUtilities.invokeLater(this::drawInitialTickets);
        }

        lastKnownPlayer = current;

        String currentName = current.getName();
        Player you = GameManager.getInstance().getPlayers().get(0);
        if (!ended) {
            String banner = currentName.equals(you.getName())
                ? "It's your turn" : "Waiting on " + currentName + "…";
            setTitle("London Lines — " + banner);
        } else {
            setTitle("London Lines — Game Over");
        }
        repaint();
    }
}
