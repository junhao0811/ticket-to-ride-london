public enum TrainTypes {

    BOX("Purple"),
    PASSENGER("Blue"),
    TANKER("Orange"),
    REEFER("White"),
    FREIGHT("Green"),
    HOPPER("Yellow"),
    COAL("Black"),
    CABOOSE("Red"),
    LOCOMOTIVE("Multi-color");

    private final String colour;

    TrainTypes(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

}


