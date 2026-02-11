package piano.atc79.model;

public class AircraftModel {
    private String id;
    private String name;
    private AircraftCategory category;
    private int cruiseSpeed;
    private double fuelConsumption;
    private double maxFuel;

    public AircraftModel(String id, String name, AircraftCategory category, int cruiseSpeed, double fuelConsumption, double maxFuel) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.cruiseSpeed = cruiseSpeed;
        this.fuelConsumption = fuelConsumption;
        this.maxFuel = maxFuel;
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

    public double getMaxFuel() {
        return maxFuel;
    }
}
