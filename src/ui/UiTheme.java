package ui;

import java.awt.*;

/**
 * Centralised colour and font constants ported from the London Lines CSS design.
 * All UI classes pull from here — never hardcode colours or fonts elsewhere.
 */
public final class UiTheme {

    private UiTheme() {}

    // ── Background palette ───────────────────────────────────────────────────
    public static final Color BG_DEEP    = new Color(0x1F, 0x27, 0x40);
    public static final Color BG_WATER   = new Color(0x2A, 0x35, 0x55);
    public static final Color MAP_CREAM  = new Color(0xF4, 0xEF, 0xDD);
    public static final Color MAP_CREAM2 = new Color(0xE8, 0xE2, 0xCC);
    public static final Color INK        = new Color(0x1A, 0x1A, 0x1A);
    public static final Color INK_DIM    = new Color(0x50, 0x50, 0x60);
    public static final Color INK_MUTE   = new Color(0x80, 0x80, 0x95);
    public static final Color GOLD       = new Color(0xC8, 0x9A, 0x2A);
    public static final Color ACCENT_RED = new Color(0xC8, 0x24, 0x2A);
    public static final Color PANEL_BG   = new Color(0x17, 0x1F, 0x33);
    public static final Color PANEL_BORDER = new Color(0x2E, 0x3A, 0x55);

    // ── Route / card colours ─────────────────────────────────────────────────
    public static final Color ROUTE_BLUE   = new Color(0x28, 0x64, 0xC4);
    public static final Color ROUTE_GREEN  = new Color(0x2C, 0x8C, 0x4D);
    public static final Color ROUTE_YELLOW = new Color(0xF0, 0xC4, 0x3A);
    public static final Color ROUTE_BLACK  = new Color(0x1A, 0x1A, 0x1A);
    public static final Color ROUTE_ORANGE = new Color(0xE0, 0x8A, 0x2B);
    public static final Color ROUTE_PINK   = new Color(0xFF, 0x6A, 0xD5);
    public static final Color ROUTE_WILD   = new Color(0xB8, 0xB8, 0xB8);

    // ── Player token colours ─────────────────────────────────────────────────
    public static final Color[] PLAYER_COLOURS = {
        new Color(0xC8, 0x50, 0x40),  // p1 red-orange
        new Color(0x40, 0xA0, 0x70),  // p2 teal-green
        new Color(0x70, 0x60, 0xC8),  // p3 purple
        new Color(0xC8, 0xA0, 0x30),  // p4 amber
    };

    // ── Fonts ────────────────────────────────────────────────────────────────
    /** Fallback display font (Bricolage Grotesque → system sans-serif). */
    public static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD, 14);
    /** Fallback body font (Inter → system sans-serif). */
    public static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 12);
    /** Fallback mono font (JetBrains Mono → system monospaced). */
    public static final Font FONT_MONO    = new Font("Monospaced", Font.BOLD, 11);

    // ── Derived sized fonts ──────────────────────────────────────────────────
    public static Font display(float size) { return FONT_DISPLAY.deriveFont(Font.BOLD, size); }
    public static Font body(float size)    { return FONT_BODY.deriveFont(Font.PLAIN, size); }
    public static Font mono(float size)    { return FONT_MONO.deriveFont(Font.BOLD, size); }

    // ── Route colour lookup ──────────────────────────────────────────────────
    public static Color routeColour(model.cards.CardColour c) {
        if (c == null) return ROUTE_WILD;
        return switch (c) {
            case BLUE   -> ROUTE_BLUE;
            case GREEN  -> ROUTE_GREEN;
            case YELLOW -> ROUTE_YELLOW;
            case BLACK  -> ROUTE_BLACK;
            case ORANGE -> ROUTE_ORANGE;
            case PURPLE -> ROUTE_PINK;
            case WILD   -> ROUTE_WILD;
        };
    }

    /** Returns a contrasting ink colour for text drawn on top of a route colour. */
    public static Color inkOn(Color bg) {
        double luminance = 0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue();
        return luminance > 160 ? INK : Color.WHITE;
    }
}
