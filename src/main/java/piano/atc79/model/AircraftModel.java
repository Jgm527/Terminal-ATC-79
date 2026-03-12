package piano.atc79.model;

public class AircraftModel {
    private String id;
    private String name;
    private AircraftCategory category;
    private int cruiseSpeed;
    private int minSpeed;
    private double fuelConsumption;
    private double maxFuel;
    private final int climbRate;
    private final int turningRate;
    private final int speedingRate;

    public AircraftModel(String id, String name, AircraftCategory category, int cruiseSpeed, int minSpeed, double fuelConsumption,
                         double maxFuel, int climbRate, int turningRate, int speedingRate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.cruiseSpeed = cruiseSpeed;
        this.minSpeed = minSpeed;
        this.fuelConsumption = fuelConsumption;
        this.maxFuel = maxFuel;
        this.climbRate = climbRate;
        this.turningRate = turningRate;
        this.speedingRate = speedingRate;
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

    public int getMinSpeed() {
        return minSpeed;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public double getMaxFuel() {
        return maxFuel;
    }

    public int getClimbRate() {
        return climbRate;
    }

    public int getTurningRate() {
        return turningRate;
    }

    public int getSpeedingRate() {
        return speedingRate;
    }
}
