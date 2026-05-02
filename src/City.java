import java.util.Objects;

public class City {

    private String name;

    public City (String name){
        this.name = name;
    }

    public String getCityName(){
        return this.name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof City city)) {
            return false;
        }

        return this.name.equals(city.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
