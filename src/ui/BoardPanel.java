package ui;

import game.GameManager;
import game.GameStateListener;
import model.board.Route;
import model.board.SegmentPoint;
import model.cards.TransportationTypes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Renders the London game board using pixel positions and pre-computed route
 * segment_points from config.json.  All coordinates are defined on a 1550×1200
 * canvas and scaled to the panel's actual paint size at render time.
 *
 * <p>This panel talks to {@link GameManager} only — never directly to GameBoard.</p>
 */
public class BoardPanel extends JPanel implements GameStateListener {

    // Config canvas reference size
    private static final double MAP_W = 1550.0;
    private static final double MAP_H = 1200.0;

    // Node display sizes (screen pixels, not scaled — stay readable at all sizes)
    private static final int NODE_R   = 14;
    private static final int HUB_R    = 14;
    private static final int FONT_NODE = 9;

    // Hub cities rendered with a larger, bolder node
    private static final Set<String> HUB_JAVA_NAMES = Set.of(
        "British Museum", "St Paul's", "Covent Garden", "King's Cross"
    );

    private final ConfigLoader    cfg = ConfigLoader.get();
    private final Image           bgImage;
    private final Image           streetBg;
    private final java.util.Map<model.player.PlayerColour, Image> busImages;
    private       TransportationTypes selectedColor;
    private       int             selectedTotalPower;
    private       String          targetedRouteId;
    private       Consumer<Route> onRouteTargeted;

    public BoardPanel() {
        setOpaque(false);
        bgImage = cfg.loadImage("/londonMap.png");
        streetBg = cfg.loadImage("/randomLondonStreet.png");
        
        busImages = new java.util.EnumMap<>(model.player.PlayerColour.class);
        busImages.put(model.player.PlayerColour.RED,    makeTransparent(cfg.loadImage("/busPiece/redbus.png")));
        busImages.put(model.player.PlayerColour.BLUE,   makeTransparent(cfg.loadImage("/busPiece/blueBus.png")));
        busImages.put(model.player.PlayerColour.WHITE,  makeTransparent(cfg.loadImage("/busPiece/whiteBus.png")));
        busImages.put(model.player.PlayerColour.PURPLE, makeTransparent(cfg.loadImage("/busPiece/purpleBus.png")));

        GameManager.getInstance().addListener(this);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleClick(e); }
        });
    }

    /** Sets the color and power for route highlighting. */
    public void setSelectedColor(TransportationTypes color, int totalPower) {
        this.selectedColor = color;
        this.selectedTotalPower = totalPower;
        this.targetedRouteId = null;
        repaint();
    }

    /** Sets the targeted route for confirmation. */
    public void setTargetedRoute(Route route) {
        this.targetedRouteId = (route != null) ? route.getId() : null;
        repaint();
    }

    public Route getTargetedRoute() {
        if (targetedRouteId == null) return null;
        return GameManager.getInstance().getBoard().findRouteById(targetedRouteId).orElse(null);
    }

    /** Registers a callback invoked when the user targets a highlighted route. */
    public void setOnRouteTargeted(Consumer<Route> callback) { this.onRouteTargeted = callback; }

    /** Clears selection and targeting. */
    public void clearSelection() { 
        selectedColor = null; 
        selectedTotalPower = 0;
        targetedRouteId = null; 
        repaint(); 
    }

    @Override public void onStateChanged() { repaint(); }

    // ── Paint ────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth(), h = getHeight();

        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, w, h, null);
        } else {
            // Draw street background as a fallback if the main map image fails
            if (streetBg != null) {
                double imgW = streetBg.getWidth(null);
                double imgH = streetBg.getHeight(null);
                double scale = Math.max((double) w / imgW, (double) h / imgH);
                int sw = (int) (imgW * scale);
                int sh = (int) (imgH * scale);
                int ox = (w - sw) / 2;
                int oy = (h - sh) / 2;
                g2.drawImage(streetBg, ox, oy, sw, sh, null);
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRect(0, 0, w, h);
            } else {
                drawBackground(g2, w, h);
            }
            drawRiver(g2, w, h);
            drawMapDecor(g2, w, h);
        }

        // Apply dimming overlay if a color is selected
        if (selectedColor != null) {
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRect(0, 0, w, h);
        }

        drawRoutes(g2, w, h);
        drawNodes(g2, w, h);
    }

    // ── Static map elements ───────────────────────────────────────────────────

    private void drawBackground(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(UiTheme.MAP_CREAM.getRGB() & 0xFFFFFF | 0xDD000000, true));
        g2.fillRoundRect(8, 8, w - 16, h - 16, 12, 12);
        g2.setColor(new Color(0, 0, 0, 12));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 20; x < w; x += 40) g2.drawLine(x, 8, x, h - 8);
        for (int y = 20; y < h; y += 40) g2.drawLine(8, y, w - 8, y);
    }

    private void drawRiver(Graphics2D g2, int w, int h) {
        int[] xp = {(int)(0.25*w),(int)(0.55*w),(int)(0.85*w), w};
        int[] yp = {(int)(0.72*h),(int)(0.65*h),(int)(0.62*h),(int)(0.68*h)};
        int n = xp.length, band = 18;
        int[] xa = new int[n*2], ya = new int[n*2];
        for (int i = 0; i < n; i++) {
            xa[i]       = xp[i]; ya[i]       = yp[i] - band;
            xa[n*2-1-i] = xp[i]; ya[n*2-1-i] = yp[i] + band;
        }
        g2.setColor(new Color(0x9A, 0xC0, 0xD8, 140));
        g2.fillPolygon(xa, ya, n * 2);
        g2.setColor(new Color(0x6A, 0x9A, 0xB8, 100));
        g2.setStroke(new BasicStroke(1f));
        g2.drawPolygon(xa, ya, n * 2);
        g2.setFont(UiTheme.body(9));
        g2.setColor(new Color(0x3A, 0x6A, 0x88, 200));
        g2.drawString("River Thames", (int)(0.52 * w), (int)(0.67 * h));
    }

    private void drawMapDecor(Graphics2D g2, int w, int h) {
        g2.setFont(UiTheme.display(11));
        g2.setColor(UiTheme.INK_MUTE);
        g2.drawString("London", 20, h - 20);
        g2.setFont(UiTheme.mono(8));
        g2.drawString("NETWORK MAP  ·  EDITION I  ·  MMXXVI", 20, h - 8);
        g2.setFont(UiTheme.display(13));
        g2.setColor(UiTheme.INK_DIM);
        g2.drawString("N", w - 22, 24);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawLine(w - 18, 26, w - 18, 36);
        g2.drawLine(w - 18, 26, w - 14, 32);
        g2.drawLine(w - 18, 26, w - 22, 32);
    }

    // ── Routes ───────────────────────────────────────────────────────────────

    private void drawRoutes(Graphics2D g2, int w, int h) {
        for (Route route : GameManager.getInstance().getBoard().getRoutes()) {
            drawRoute(g2, route, w, h);
        }
    }

    private void drawRoute(Graphics2D g2, Route route, int w, int h) {
        List<SegmentPoint> segments = route.getSegmentPoints();
        if (segments.isEmpty()) return;

        double scaleX = (double) w / MAP_W;
        double scaleY = (double) h / MAP_H;
        double scale  = Math.min(scaleX, scaleY);
        double slotLen = 86 * scale;
        double slotH   = 24 * scale;
        int    arc     = Math.max(3, (int)(6 * scale));

        boolean claimed  = route.isClaimed();
        boolean targeted = route.getId().equals(targetedRouteId);
        boolean highlighted = !claimed && selectedColor != null && 
                            route.getLength() <= selectedTotalPower &&
                            (route.getRequiredColour() == null || route.getRequiredColour() == selectedColor);

        Image busImg = claimed ? busImages.get(route.getClaimedBy().getColour()) : null;
        Color routeColor = UiTheme.routeColour(route.getRequiredColour());

        AffineTransform saved = g2.getTransform();
        for (SegmentPoint sp : segments) {
            double cx = sp.x() * scaleX;
            double cy = sp.y() * scaleY;

            g2.setTransform(saved);
            g2.translate(cx, cy);
            g2.rotate(sp.rotationRadians());

            // Targeting glow (intense teal)
            if (targeted) {
                g2.setColor(new Color(0x3A, 0xBD, 0xD0, 220));
                g2.fill(new RoundRectangle2D.Double(
                    -slotLen / 2 - 8, -slotH / 2 - 8,
                    slotLen + 16, slotH + 16, arc + 4, arc + 4));
            } else if (highlighted) {
                // Highlight glow (Golden yellowish)
                g2.setColor(new Color(0xF0, 0xC4, 0x3A, 180));
                g2.fill(new RoundRectangle2D.Double(
                    -slotLen / 2 - 6, -slotH / 2 - 6,
                    slotLen + 12, slotH + 12, arc + 2, arc + 2));
            }

            // Dim non-relevant elements if a selection is active
            float alpha = 1.0f;
            if (selectedColor != null && !highlighted && !targeted) {
                alpha = claimed ? 0.6f : 0.2f;
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // Slot background
            g2.setColor(claimed ? routeColor.darker() : routeColor);
            g2.fill(new RoundRectangle2D.Double(-slotLen / 2, -slotH / 2, slotLen, slotH, arc, arc));

            g2.setColor(new Color(0, 0, 0, 60));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(-slotLen / 2, -slotH / 2, slotLen, slotH, arc, arc));

            if (claimed && busImg != null) {
                double busH = slotH * 1.55;
                double busW = slotLen * 0.98;
                g2.drawImage(busImg, (int)(-busW / 2), (int)(-busH / 2), (int)busW, (int)busH, null);
            }
            
            // Reset alpha for next segment/element
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        g2.setTransform(saved);
    }

    /**
     * BFS flood-fill from every border pixel: any white/near-white pixel reachable
     * from the image edge is treated as background and made fully transparent.
     */
    private Image makeTransparent(Image src) {
        if (src == null) return null;
        int iw = src.getWidth(null), ih = src.getHeight(null);
        if (iw <= 0 || ih <= 0) return src;
        BufferedImage buf = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buf.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();

        boolean[][] visited = new boolean[iw][ih];
        Queue<int[]> queue  = new LinkedList<>();

        for (int x = 0; x < iw; x++) {
            addIfBackground(buf, queue, visited, x, 0,      iw, ih);
            addIfBackground(buf, queue, visited, x, ih - 1, iw, ih);
        }
        for (int y = 0; y < ih; y++) {
            addIfBackground(buf, queue, visited, 0,      y, iw, ih);
            addIfBackground(buf, queue, visited, iw - 1, y, iw, ih);
        }

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            buf.setRGB(p[0], p[1], 0);
            for (int i = 0; i < 4; i++) {
                addIfBackground(buf, queue, visited, p[0] + dx[i], p[1] + dy[i], iw, ih);
            }
        }
        return buf;
    }

    private void addIfBackground(BufferedImage buf, Queue<int[]> q, boolean[][] visited,
                                  int x, int y, int iw, int ih) {
        if (x < 0 || x >= iw || y < 0 || y >= ih || visited[x][y]) return;
        int rgba = buf.getRGB(x, y);
        int a = (rgba >> 24) & 0xFF;
        int r = (rgba >> 16) & 0xFF;
        int g = (rgba >>  8) & 0xFF;
        int b =  rgba        & 0xFF;
        if (a > 10 && r > 200 && g > 200 && b > 200) {
            visited[x][y] = true;
            q.add(new int[]{x, y});
        }
    }

    // ── City nodes ───────────────────────────────────────────────────────────

    private void drawNodes(Graphics2D g2, int w, int h) {
        double scaleX = w / MAP_W;
        double scaleY = h / MAP_H;
        for (ConfigLoader.CityConfig city : cfg.getCitiesById().values()) {
            int    cx       = (int)(city.x() * scaleX);
            int    cy       = (int)(city.y() * scaleY);
            String javaName = ConfigLoader.CITY_ID_TO_JAVA_NAME.getOrDefault(city.id(), city.displayName());
            drawNode(g2, javaName, cx, cy, HUB_JAVA_NAMES.contains(javaName));
        }
    }

    private void drawNode(Graphics2D g2, String name, int cx, int cy, boolean hub) {
        int r = HUB_R;
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillOval(cx - r + 2, cy - r + 2, r * 2, r * 2);
        g2.setColor(UiTheme.MAP_CREAM2);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        g2.setColor(UiTheme.INK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);
    }

    // ── Click handling ───────────────────────────────────────────────────────

    private void handleClick(MouseEvent e) {
        Route hit = hitTestRoutes(e.getX(), e.getY(), getWidth(), getHeight());
        if (hit == null || hit.isClaimed()) return;

        // Only handle clicks on highlighted routes
        boolean highlighted = selectedColor != null && 
                            hit.getLength() <= selectedTotalPower &&
                            (hit.getRequiredColour() == null || hit.getRequiredColour() == selectedColor);
        
        if (highlighted) {
            targetedRouteId = hit.getId();
            repaint();
            if (onRouteTargeted != null) onRouteTargeted.accept(hit);
        }
    }

    private Route hitTestRoutes(int px, int py, int w, int h) {
        double scaleX = w / MAP_W;
        double scaleY = h / MAP_H;
        Route  best     = null;
        double bestDist = 22;
        for (Route route : GameManager.getInstance().getBoard().getRoutes()) {
            for (SegmentPoint sp : route.getSegmentPoints()) {
                double d = Math.hypot(px - sp.x() * scaleX, py - sp.y() * scaleY);
                if (d < bestDist) { bestDist = d; best = route; }
            }
        }
        return best;
    }
}
