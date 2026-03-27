public class RouteScorer {

    public int getPoints(int routeLength) {
        if (routeLength < 1 || routeLength > 6) {
            throw new IllegalArgumentException("Route length must be between 1 and 6.");
        }

        switch (routeLength) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
                return 7;
            case 5:
                return 10;
            case 6:
                return 15;
            default:
                return 0;
        }
    }

    public static void main(String[] args) {
        RouteScorer scorer = new RouteScorer();

        System.out.println("Route length 1 = " + scorer.getPoints(1));
        System.out.println("Route length 2 = " + scorer.getPoints(2));
        System.out.println("Route length 3 = " + scorer.getPoints(3));
        System.out.println("Route length 4 = " + scorer.getPoints(4));
        System.out.println("Route length 5 = " + scorer.getPoints(5));
        System.out.println("Route length 6 = " + scorer.getPoints(6));
    }
}