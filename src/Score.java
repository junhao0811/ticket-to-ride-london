public class Score {
    private int points;

    public void addPoints(int value) {
        points += value;
    }

    public void subtractPoints(int value) {
        points -= value;
    }

    public int getPoints() {
        return points;
    }
}