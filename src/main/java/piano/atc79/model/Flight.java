package piano.atc79.model;

public class Flight {
    private String callsign;
    private AircraftModel model;
    private Position currentPosition;
    private int heading;
    private int speed;
    private int altitude;
    private double fuel;
    private FlightStatus status;
    private int targetHeading;
    private int targetAltitude;

    public Flight(String callsign, AircraftModel model, Position currentPosition, int heading, int speed, int altitude) {
        this.callsign = callsign;
        this.model = model;
        this.currentPosition = currentPosition;
        this.heading = heading;
        this.speed = speed;
        this.altitude = altitude;
        this.fuel = calculateFuel();
        this.status = FlightStatus.EN_ROUTE;
        this.targetHeading = heading;
        this.targetAltitude = altitude;
    }

    public String getCallsign() {
        return callsign;
    }

    public AircraftModel getModel() {
        return model;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public int getHeading() {
        return heading;
    }

    public int getSpeed() {
        return speed;
    }

    public int getAltitude() {
        return altitude;
    }

    public double getFuel() {
        return fuel;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public int getTargetHeading() {
        return targetHeading;
    }

    public int getTargetAltitude() {
        return targetAltitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    private double calculateFuel() {
        return model.getMaxFuel() * (0.7 + Math.random() * 0.3);
    }
}
