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

        // ── Assemble stage (absolute layout, scaled) ───────────────────────
        stage = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                if (w <= 0 || h <= 0) return;
                Graphics2D g2 = (Graphics2D) g;
                RadialGradientPaint grad = new RadialGradientPaint(
                    w / 2f, h / 2f, Math.max(w, h) / 1.5f,
                    new float[]{0f, 0.6f, 1f},
                    new Color[]{
                        UiTheme.BG_WATER,
                        new Color(0x2A, 0x35, 0x55),
                        UiTheme.BG_DEEP
                    });
                g2.setPaint(grad);
                g2.fillRect(0, 0, w, h);
            }
        };
        stage.setOpaque(true);
        stage.setPreferredSize(new Dimension(STAGE_W, STAGE_H));

        // Left rail: absolute left edge
        stage.add(leftRail);
        // Board: centre
        stage.add(boardPanel);
        // Right rail: right edge
        stage.add(rightRail);
        // Hand bar: bottom strip
        stage.add(handBar);
        // Score panel (hidden until game ends)
        stage.add(scorePanel);
        scorePanel.setVisible(false);
        // Toast overlay
        stage.add(toast);
        // Pass-device overlay — added last so it sits above everything
        passOverlay = new PassDeviceOverlay(() -> passOverlay.setVisible(false));
        stage.add(passOverlay);
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
                    if (c instanceof JPanel p) {
                        layoutStage(p, sw, sh);
                    }
                }
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(STAGE_W, STAGE_H);
            }
        };
        scaleWrap.setOpaque(true);
        scaleWrap.setBackground(new Color(0x0C, 0x12, 0x20));
        scaleWrap.add(stage);

        setContentPane(scaleWrap);

        GameManager.getInstance().addListener(this);
        lastKnownPlayer = GameManager.getInstance().getCurrentPlayer();
    }

    private void layoutStage(JPanel p, int w, int h) {
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
            kept -> {
                action.setKeptTickets(kept);
                var result = GameManager.getInstance().submitAction(action);
                toast.show(result.getMessage());
            }).setVisible(true);
    }

    @Override
    public void onStateChanged() {
        boolean ended = GameManager.getInstance().getPhase() == GamePhase.ENDED;
        boardPanel.setVisible(!ended);
        scorePanel.setVisible(ended);

        Player current = GameManager.getInstance().getCurrentPlayer();
        if (!ended && lastKnownPlayer != null && lastKnownPlayer != current) {
            passOverlay.setNextPlayer(current);
            passOverlay.setVisible(true);
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
