package piano.atc79.model;

public class AircraftModel {
    private String id;
    private String name;
    private AircraftCategory category;
    private int cruiseSpeed;
    private double fuelConsumption;

    public AircraftModel(String id, String name, AircraftCategory category, int cruiseSpeed, double fuelConsumption) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.cruiseSpeed = cruiseSpeed;
        this.fuelConsumption = fuelConsumption;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AircraftCategory getCategory() {
        return category;
    }

    public int getCruiseSpeed() {
        return cruiseSpeed;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }
}
