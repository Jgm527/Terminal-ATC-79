package piano.atc79.model;

public enum AircraftCategory {
    LIGHT(1, 3.0, false),
    MEDIUM(2, 4.0, false),
    HEAVY(3, 6.0, false);

    private final int wakeIntensity;
    private final double minSeparationNM;
    private final boolean requiresLongRunway;

    //Constructor
    AircraftCategory(int wakeIntensity, double minSeparationNM, boolean requiresLongRunway) {
        this.wakeIntensity = wakeIntensity;
        this.minSeparationNM = minSeparationNM;
        this.requiresLongRunway = requiresLongRunway;
    }

    //Getters
    public int getWakeIntensity() {
        return wakeIntensity;
    }

    public double getMinSeparationNM() {
        return minSeparationNM;
    }

    public boolean requiresLongRunway() {
        return requiresLongRunway;
    }
}
