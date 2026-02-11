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

    private double calculateFuel() {
        return model.getMaxFuel() * (0.7 + Math.random() * 0.3);
    }
}
