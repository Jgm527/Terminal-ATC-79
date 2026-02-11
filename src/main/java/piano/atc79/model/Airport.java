package piano.atc79.model;

import java.util.ArrayList;
import java.util.List;

public class Airport {
    private String id;
    private String name;
    private List<Runway> runways;
    private int minimumVectoringAltitude;

    public Airport(String id, String name, int minimumVectoringAltitude) {
        this.id = id;
        this.name = name;
        this.minimumVectoringAltitude = minimumVectoringAltitude;
        runways = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List getRunways() {
        return runways;
    }

    public int getMinimumVectoringAltitude() {
        return minimumVectoringAltitude;
    }

    public void addRunway(Runway runway) {
        runways.add(runway);
    }
}
