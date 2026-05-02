package model.cards;

/** Colour values for train cards. WILD is the locomotive sentinel (any colour). */
public enum CardColour {
    BLUE, GREEN, YELLOW, BLACK, ORANGE, PURPLE, WILD;

    /** Display name shown in the UI. */
    public String displayName() {
        return switch (this) {
            case BLUE   -> "Blue";
            case GREEN  -> "Green";
            case YELLOW -> "Yellow";
            case BLACK  -> "Black";
            case ORANGE -> "Orange";
            case PURPLE -> "Pink";
            case WILD   -> "Locomotive";
        };
    }

    /** Short 3-letter badge shown on cards. */
    public String badge() {
        return displayName().substring(0, 3).toUpperCase();
    }

    /** Hex colour used by the UI renderer. */
    public String hex() {
        return switch (this) {
            case BLUE   -> "#2864C4";
            case GREEN  -> "#2C8C4D";
            case YELLOW -> "#F0C43A";
            case BLACK  -> "#1A1A1A";
            case ORANGE -> "#E08A2B";
            case PURPLE -> "#FF6AD5";
            case WILD   -> "#B8B8B8";
        };
    }
}
