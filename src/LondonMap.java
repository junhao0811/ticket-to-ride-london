import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;
import java.util.List;

/**
 * Ticket to Ride: London Map - Java Swing Recreation
 * Renders the London board with cities, routes, and labels.
 */
public class LondonMap extends JPanel {
    
    // ── City coordinates (pixels on a 1100x750 canvas) ──────────────────────
    private static final Map<String, Point> CITIES = new LinkedHashMap<>();
    static {
        CITIES.put("King's Cross",        new Point(540, 80));
        CITIES.put("Globe Theatre",       new Point(700, 75));
        CITIES.put("Red Rover",           new Point(850, 60));
        CITIES.put("Brick Lane",          new Point(950, 165));
        CITIES.put("Tower of London",     new Point(900, 300));
        CITIES.put("Globe Theatre South", new Point(760, 310));  // bottom globe
        CITIES.put("Elephant & Castle",   new Point(820, 490));
        CITIES.put("Waterloo",            new Point(640, 440));
        CITIES.put("Trafalgar Square",    new Point(570, 420));
        CITIES.put("Covent Garden",       new Point(590, 340));
        CITIES.put("St Paul's",           new Point(680, 280));
        CITIES.put("The Charterhouse",    new Point(700, 185));
        CITIES.put("Baker Street",        new Point(330, 230));
        CITIES.put("British Museum",      new Point(470, 290));
        CITIES.put("Piccadilly Circus",   new Point(430, 380));
        CITIES.put("Hyde Park",           new Point(270, 450));
        CITIES.put("Buckingham Palace",   new Point(430, 500));
        CITIES.put("Big Ben",             new Point(540, 520));
        CITIES.put("It's Park",           new Point(230, 195));
        CITIES.put("Buckingham Pl. NW",   new Point(170, 290)); // King's Cross West
        CITIES.put("Trafalgar Sq. West",  new Point(300, 370));
    }
    
    // Re-using cleaner name map
    private static final String[][] CITY_LABELS = {
        {"KC",  "King's Cross"},
        {"GT",  "Globe Theatre"},
        {"RR",  "Red Rover"},
        {"BL",  "Brick Lane"},
        {"TL",  "Tower of London"},
        {"GTS", "Globe Theatre\n(South)"},
        {"EC",  "Elephant &\nCastle"},
        {"WL",  "Waterloo"},
        {"TS",  "Trafalgar\nSquare"},
        {"CG",  "Covent\nGarden"},
        {"SP",  "St Paul's"},
        {"CH",  "The\nCharterhouse"},
        {"BS",  "Baker\nStreet"},
        {"BM",  "British\nMuseum"},
        {"PC",  "Piccadilly\nCircus"},
        {"HP",  "Hyde\nPark"},
        {"BP",  "Buckingham\nPalace"},
        {"BB",  "Big Ben"},
        {"IP",  "It's Park"},
        {"KCW", "King's Cross\nWest"},
        {"TSW", "Baker St/\nTrafalgar"},
    };
    
    // Route definition: {fromCity, toCity, length, color, segments}
    // Color encoding: R=Red, G=Gray, B=Blue, Y=Yellow, K=Black, W=White/Gray,
    //                 O=Orange, P=Pink/Magenta, C=Cyan, GN=Green
    private static final Object[][] ROUTES = {
        // King's Cross area
        {"King's Cross",     "Globe Theatre",      1, Color.GRAY,                  1},
        {"King's Cross",     "Globe Theatre",      1, Color.decode("#C0C0C0"),      1}, // double
        {"King's Cross",     "The Charterhouse",   4, Color.decode("#FF4500"),      4},
        {"King's Cross",     "Baker Street",       5, Color.decode("#FFD700"),      5},
        {"King's Cross",     "It's Park",          5, Color.decode("#1E90FF"),      5},
        // Globe Theatre
        {"Globe Theatre",    "Red Rover",          1, Color.decode("#FF4500"),      1},
        {"Globe Theatre",    "Brick Lane",         2, Color.decode("#1E90FF"),      2},
        {"Globe Theatre",    "The Charterhouse",   2, Color.GRAY,                  2},
        // Red Rover -> Brick Lane
        {"Red Rover",        "Brick Lane",         2, Color.decode("#FFD700"),      2},
        // Brick Lane -> Tower of London
        {"Brick Lane",       "Tower of London",    4, Color.decode("#228B22"),      4},
        // Tower of London
        {"Tower of London",  "Globe Theatre South",4, Color.decode("#FF69B4"),      4},
        {"Tower of London",  "St Paul's",          3, Color.decode("#FF4500"),      3},
        // Globe Theatre South
        {"Globe Theatre South","Elephant & Castle",3, Color.decode("#1E90FF"),      3},
        {"Globe Theatre South","Waterloo",         3, Color.decode("#FF4500"),      3},
        {"Globe Theatre South","St Paul's",        2, Color.GRAY,                  2},
        // St Paul's
        {"St Paul's",        "The Charterhouse",   4, Color.decode("#FFD700"),      4},
        {"St Paul's",        "Covent Garden",      1, Color.decode("#FF4500"),      1},
        {"St Paul's",        "British Museum",     3, Color.decode("#1E90FF"),      3},
        // The Charterhouse
        {"The Charterhouse", "Brick Lane",         4, Color.GRAY,                  4},
        // Covent Garden
        {"Covent Garden",    "Waterloo",           2, Color.decode("#1E90FF"),      2},
        {"Covent Garden",    "Trafalgar Square",   2, Color.decode("#FF69B4"),      2},
        {"Covent Garden",    "British Museum",     1, Color.decode("#FF4500"),      1},
        // Trafalgar Square
        {"Trafalgar Square", "Waterloo",           2, Color.decode("#228B22"),      2},
        {"Trafalgar Square", "Big Ben",            2, Color.decode("#FF4500"),      2},
        {"Trafalgar Square", "Piccadilly Circus",  2, Color.decode("#FF69B4"),      2},
        // Waterloo
        {"Waterloo",         "Elephant & Castle",  3, Color.decode("#FFD700"),      3},
        {"Waterloo",         "Big Ben",            2, Color.decode("#FF69B4"),      2},
        // Big Ben
        {"Big Ben",          "Buckingham Palace",  2, Color.decode("#1E90FF"),      2},
        {"Big Ben",          "Elephant & Castle",  3, Color.decode("#228B22"),      3},
        // British Museum
        {"British Museum",   "Baker Street",       1, Color.decode("#FF4500"),      1},
        {"British Museum",   "Piccadilly Circus",  2, Color.GRAY,                  2},
        // Piccadilly Circus
        {"Piccadilly Circus","Buckingham Palace",  2, Color.decode("#FF4500"),      2},
        {"Piccadilly Circus","Hyde Park",          5, Color.decode("#228B22"),      5},
        {"Piccadilly Circus","Baker Street",       5, Color.decode("#1E90FF"),      5},
        // Baker Street
        {"Baker Street",     "It's Park",          5, Color.GRAY,                  5},
        {"Baker Street",     "King's Cross West",  5, Color.decode("#FF4500"),      5},
        // Hyde Park
        {"Hyde Park",        "Buckingham Palace",  2, Color.GRAY,                  2},
        {"Hyde Park",        "King's Cross West",  5, Color.decode("#1E90FF"),      5},
        // Buckingham Palace
        {"Buckingham Palace","King's Cross West",  6, Color.decode("#FFD700"),      6},
        // It's Park
        {"It's Park",        "King's Cross West",  3, Color.decode("#228B22"),      3},
        // Elephant & Castle
        {"Elephant & Castle","Red Rover",          7, Color.decode("#FF4500"),      7}, // outer ring
    };
    
    private static final Color BG_COLOR   = new Color(240, 220, 180);
    private static final Color CITY_COLOR  = new Color(30, 30, 30);
    private static final Color LABEL_BG    = new Color(255, 255, 255, 200);
    
    // Cleaner city map for rendering
    private static final Map<String, Point> C = new LinkedHashMap<>();
    static {
        C.put("King's Cross",        new Point(560, 90));
        C.put("Globe Theatre",       new Point(710, 80));
        C.put("Red Rover",           new Point(870, 68));
        C.put("Brick Lane",          new Point(970, 175));
        C.put("Tower of London",     new Point(920, 305));
        C.put("Globe Theatre South", new Point(790, 320));
        C.put("Elephant & Castle",   new Point(840, 495));
        C.put("Waterloo",            new Point(660, 445));
        C.put("Trafalgar Square",    new Point(575, 435));
        C.put("Covent Garden",       new Point(610, 355));
        C.put("St Paul's",           new Point(710, 295));
        C.put("The Charterhouse",    new Point(730, 200));
        C.put("Baker Street",        new Point(330, 245));
        C.put("British Museum",      new Point(475, 305));
        C.put("Piccadilly Circus",   new Point(425, 390));
        C.put("Hyde Park",           new Point(255, 455));
        C.put("Buckingham Palace",   new Point(435, 510));
        C.put("Big Ben",             new Point(545, 530));
        C.put("It's Park",           new Point(220, 205));
        C.put("King's Cross West",   new Point(155, 300));
        C.put("Trafalgar West",      new Point(310, 375));
    }
    
    public LondonMap() {
        setPreferredSize(new Dimension(1150, 720));
        setBackground(BG_COLOR);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawBackground(g2);
        drawRoutes(g2);
        drawCities(g2);
        drawTitle(g2);
        drawLegend(g2);
    }
    
    private void drawBackground(Graphics2D g2) {
        // Parchment-style background
        GradientPaint gp = new GradientPaint(0, 0, new Color(245, 228, 195),
            getWidth(), getHeight(), new Color(220, 195, 155));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Subtle grid/map lines
        g2.setColor(new Color(180, 155, 110, 60));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < getWidth(); x += 50)
            g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 50)
            g2.drawLine(0, y, getWidth(), y);
        
        // Border
        g2.setColor(new Color(100, 70, 30));
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(8, 8, getWidth() - 16, getHeight() - 16);
        g2.setColor(new Color(140, 100, 50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(14, 14, getWidth() - 28, getHeight() - 28);
    }
    
    private void drawRoutes(Graphics2D g2) {
        // Draw each route as segmented colored rectangles
        Object[][] routes = {
            // {from, to, length, color}
            {"King's Cross",        "Globe Theatre",        1,  new Color(160,160,160)},
            {"King's Cross",        "The Charterhouse",     4,  new Color(220, 50, 50)},
            {"King's Cross",        "Baker Street",         5,  new Color(220,180, 20)},
            {"King's Cross",        "It's Park",            5,  new Color( 30,100,220)},
            {"Globe Theatre",       "Red Rover",            1,  new Color(220, 50, 50)},
            {"Globe Theatre",       "Brick Lane",           2,  new Color( 30,100,220)},
            {"Globe Theatre",       "The Charterhouse",     2,  new Color(160,160,160)},
            {"Red Rover",           "Brick Lane",           2,  new Color(220,180, 20)},
            {"Brick Lane",          "Tower of London",      4,  new Color( 30,140, 30)},
            {"Tower of London",     "Globe Theatre South",  4,  new Color(220, 80,180)},
            {"Tower of London",     "St Paul's",            3,  new Color(220, 50, 50)},
            {"Globe Theatre South", "Elephant & Castle",    3,  new Color( 30,100,220)},
            {"Globe Theatre South", "Waterloo",             3,  new Color(220, 50, 50)},
            {"Globe Theatre South", "St Paul's",            2,  new Color(160,160,160)},
            {"St Paul's",           "The Charterhouse",     4,  new Color(220,180, 20)},
            {"St Paul's",           "Covent Garden",        1,  new Color(220, 50, 50)},
            {"St Paul's",           "British Museum",       3,  new Color( 30,100,220)},
            {"The Charterhouse",    "Brick Lane",           4,  new Color(160,160,160)},
            {"Covent Garden",       "Waterloo",             2,  new Color( 30,100,220)},
            {"Covent Garden",       "Trafalgar Square",     2,  new Color(220, 80,180)},
            {"Covent Garden",       "British Museum",       1,  new Color(220, 50, 50)},
            {"Trafalgar Square",    "Waterloo",             2,  new Color( 30,140, 30)},
            {"Trafalgar Square",    "Big Ben",              2,  new Color(220, 50, 50)},
            {"Trafalgar Square",    "Piccadilly Circus",    2,  new Color(220, 80,180)},
            {"Waterloo",            "Elephant & Castle",    3,  new Color(220,180, 20)},
            {"Waterloo",            "Big Ben",              2,  new Color(220, 80,180)},
            {"Big Ben",             "Buckingham Palace",    2,  new Color( 30,100,220)},
            {"Big Ben",             "Elephant & Castle",    3,  new Color( 30,140, 30)},
            {"British Museum",      "Baker Street",         1,  new Color(220, 50, 50)},
            {"British Museum",      "Piccadilly Circus",    2,  new Color(160,160,160)},
            {"Piccadilly Circus",   "Buckingham Palace",    2,  new Color(220, 50, 50)},
            {"Piccadilly Circus",   "Hyde Park",            5,  new Color( 30,140, 30)},
            {"Piccadilly Circus",   "Baker Street",         5,  new Color( 30,100,220)},
            {"Baker Street",        "It's Park",            5,  new Color(160,160,160)},
            {"Baker Street",        "King's Cross West",    5,  new Color(220, 50, 50)},
            {"Hyde Park",           "Buckingham Palace",    2,  new Color(160,160,160)},
            {"Hyde Park",           "King's Cross West",    5,  new Color( 30,100,220)},
            {"Buckingham Palace",   "King's Cross West",    6,  new Color(220,180, 20)},
            {"It's Park",           "King's Cross West",    3,  new Color( 30,140, 30)},
        };
        
        for (Object[] r : routes) {
            String from  = (String) r[0];
            String to    = (String) r[1];
            int    len   = (int)    r[2];
            Color  col   = (Color)  r[3];
            Point  pf    = C.get(from);
            Point  pt    = C.get(to);
            if (pf == null || pt == null) continue;
            drawSegmentedRoute(g2, pf.x, pf.y, pt.x, pt.y, len, col);
        }
    }
    
    /**
     * Draws a route as `count` rectangular segments between two city points.
     */
    private void drawSegmentedRoute(Graphics2D g2,
        int x1, int y1, int x2, int y2,
        int count, Color color) {
        double dx   = x2 - x1;
        double dy   = y2 - y1;
        double len  = Math.sqrt(dx * dx + dy * dy);
        double ux   = dx / len;  // unit vector along route
        double uy   = dy / len;
        double nx   = -uy;       // perpendicular (normal)
        double ny   =  ux;
        
        double segW  = 14.0;  // segment width (along route)
        double segH  =  8.0;  // segment height (across route)
        double gap   =  4.0;  // gap between segments
        double step  = segW + gap;
        double total = step * count - gap;
        // centre the array between the two cities
        double startT = (len - total) / 2.0;
        
        for (int i = 0; i < count; i++) {
            double t  = startT + i * step;
            // centre of this segment
            double cx = x1 + ux * (t + segW / 2.0);
            double cy = y1 + uy * (t + segW / 2.0);
            
            // 4 corners of the rectangle
            double[] xs = {
                cx + ux * segW / 2.0 + nx * segH / 2.0,
                cx - ux * segW / 2.0 + nx * segH / 2.0,
                cx - ux * segW / 2.0 - nx * segH / 2.0,
                cx + ux * segW / 2.0 - nx * segH / 2.0
            };
            double[] ys = {
                cy + uy * segW / 2.0 + ny * segH / 2.0,
                cy - uy * segW / 2.0 + ny * segH / 2.0,
                cy - uy * segW / 2.0 - ny * segH / 2.0,
                cy + uy * segW / 2.0 - ny * segH / 2.0
            };
            int[] xi = new int[4];
            int[] yi = new int[4];
            for (int j = 0; j < 4; j++) { xi[j] = (int) xs[j]; yi[j] = (int) ys[j]; }
            
            // Fill
            g2.setColor(color);
            g2.fillPolygon(xi, yi, 4);
            // Border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawPolygon(xi, yi, 4);
        }
    }
    
    private void drawCities(Graphics2D g2) {
        Font cityFont = new Font("SansSerif", Font.BOLD, 10);
        g2.setFont(cityFont);
        
        for (Map.Entry<String, Point> e : C.entrySet()) {
            String name = e.getKey();
            Point  p    = e.getValue();
            
            // City dot
            int r = 9;
            g2.setColor(new Color(240, 230, 200));
            g2.fillOval(p.x - r, p.y - r, r * 2, r * 2);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(p.x - r, p.y - r, r * 2, r * 2);
            g2.setColor(new Color(60, 30, 10));
            g2.fillOval(p.x - 4, p.y - 4, 8, 8);
            
            // Label
            drawCityLabel(g2, name, p.x, p.y, cityFont);
        }
    }
    
    private void drawCityLabel(Graphics2D g2, String name, int cx, int cy, Font f) {
        String[] lines = name.split("\n");
        // For display, use shortened / line-broken versions
        String display = name;
        String[] parts = name.split(" ");
        
        // Label offset — try to avoid overlap with routes
        int ox = cx + 12;
        int oy = cy - 8;
        
        // Special positioning
        if (name.equals("Red Rover"))          { ox = cx - 15; oy = cy - 22; }
        if (name.equals("Brick Lane"))         { ox = cx + 12; oy = cy - 10; }
        if (name.equals("Tower of London"))    { ox = cx + 12; oy = cy + 0;  }
        if (name.equals("Globe Theatre South")){ ox = cx + 12; oy = cy + 0;  }
        if (name.equals("Elephant & Castle"))  { ox = cx + 12; oy = cy + 5;  }
        if (name.equals("Waterloo"))           { ox = cx - 10; oy = cy + 18; }
        if (name.equals("Trafalgar Square"))   { ox = cx - 60; oy = cy + 18; }
        if (name.equals("Covent Garden"))      { ox = cx + 12; oy = cy - 8;  }
        if (name.equals("St Paul's"))          { ox = cx + 12; oy = cy - 18; }
        if (name.equals("The Charterhouse"))   { ox = cx - 30; oy = cy - 20; }
        if (name.equals("Baker Street"))       { ox = cx - 20; oy = cy - 22; }
        if (name.equals("British Museum"))     { ox = cx - 55; oy = cy + 18; }
        if (name.equals("Piccadilly Circus"))  { ox = cx - 65; oy = cy + 18; }
        if (name.equals("Hyde Park"))          { ox = cx - 55; oy = cy + 18; }
        if (name.equals("Buckingham Palace"))  { ox = cx - 65; oy = cy + 18; }
        if (name.equals("Big Ben"))            { ox = cx + 12; oy = cy + 5;  }
        if (name.equals("It's Park"))          { ox = cx - 40; oy = cy - 20; }
        if (name.equals("King's Cross West"))  { ox = cx - 30; oy = cy + 18; }
        if (name.equals("Trafalgar West"))     { ox = cx - 55; oy = cy + 18; }
        
        // Render label with background
        FontMetrics fm = g2.getFontMetrics(f);
        int tw = fm.stringWidth(name);
        int th = fm.getHeight();
        
        g2.setColor(new Color(255, 255, 240, 210));
        g2.fillRoundRect(ox - 2, oy - th + 2, tw + 4, th + 2, 4, 4);
        g2.setColor(new Color(40, 20, 5));
        g2.setFont(f);
        g2.drawString(name, ox, oy);
    }
    
    private void drawTitle(Graphics2D g2) {
        // Title box top-right
        int bx = getWidth() - 210, by = 20, bw = 190, bh = 55;
        g2.setColor(new Color(180, 30, 30));
        g2.fillRoundRect(bx, by, bw, bh, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(bx, by, bw, bh, 10, 10);
        
        g2.setFont(new Font("Serif", Font.BOLD, 22));
        g2.setColor(Color.WHITE);
        g2.drawString("LONDON", bx + 25, by + 28);
        g2.setFont(new Font("Serif", Font.ITALIC, 13));
        g2.drawString("Ticket to Ride", bx + 30, by + 47);
    }
    
    private void drawLegend(Graphics2D g2) {
        int lx = 22, ly = getHeight() - 135;
        g2.setColor(new Color(255, 250, 230, 220));
        g2.fillRoundRect(lx - 5, ly - 18, 170, 125, 8, 8);
        g2.setColor(new Color(100, 70, 30));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(lx - 5, ly - 18, 170, 125, 8, 8);
        
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(40, 20, 5));
        g2.drawString("Route Colours", lx + 20, ly - 3);
        
        Object[][] legend = {
            {"Red",    new Color(220, 50, 50)},
            {"Blue",   new Color(30, 100, 220)},
            {"Yellow", new Color(220, 180, 20)},
            {"Green",  new Color(30, 140, 30)},
            {"Pink",   new Color(220, 80, 180)},
            {"Gray",   new Color(160, 160, 160)},
        };
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < legend.length; i++) {
            String label = (String) legend[i][0];
            Color  col   = (Color)  legend[i][1];
            int ry = ly + 12 + i * 17;
            g2.setColor(col);
            g2.fillRoundRect(lx, ry - 10, 30, 12, 3, 3);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(0.8f));
            g2.drawRoundRect(lx, ry - 10, 30, 12, 3, 3);
            g2.setColor(new Color(40, 20, 5));
            g2.drawString(label, lx + 36, ry);
        }
    }
    
    // ── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ticket to Ride – London Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            LondonMap map = new LondonMap();
            JScrollPane scroll = new JScrollPane(map);
            frame.add(scroll);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
