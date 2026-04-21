public class Route {
    private String id;
    private String cityA;
    private String cityB;
    private int length;
    private String color; // Could be "any" for gray routes
    private Player claimedBy;
    
    public Route(String id, String cityA, String cityB, int length, String color) {
        this.id = id;
        this.cityA = cityA;
        this.cityB = cityB;
        this.length = length;
        this.color = color;
        this.claimedBy = null; // Initially unclaimed
    }
    
    public boolean isClaimed() {
        return claimedBy != null;
    }
    
    public void claimRoute(Player player) {
        if (!isClaimed()) {
            this.claimedBy = player;
            // Additional logic to update player's score and train pieces
        }
    }
    
    public String getId() {
        return id;
    }
    
    public String getCityA() {
        return cityA;
    }
    
    public String getCityB() {
        return cityB;
    }
    
    public int getLength() {
        return length;
    }
    
    public String getColor() {
        return color;
    }{
    
    }
    
}
