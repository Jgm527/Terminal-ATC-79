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
    private int targetSpeed;

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
        this.targetSpeed = speed;
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

    public int getTargetSpeed() {
        return targetSpeed;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    public void setTargetHeading(int targetHeading) {
        this.targetHeading = targetHeading;
    }

    public void setTargetAltitude(int targetAltitude) {
        this.targetAltitude = targetAltitude;
    }

    public void setTargetSpeed(int targetSpeed) {
        this.targetSpeed = targetSpeed;
    }

    private double calculateFuel() {
        return model.getMaxFuel() * (0.7 + Math.random() * 0.3);
    }

    public void updatePosition() {
        updateHeading();
        updateLatitude();
        updateAltitude();
        updateSpeed();
    }

    private void updateLatitude() {
        double speedPerSeconds = getSpeed() / 3600.0;
        double rad = Math.toRadians(getHeading());

        double deltaX = speedPerSeconds * Math.sin(rad);
        double deltaY = speedPerSeconds * Math.cos(rad);

        Position pos = getCurrentPosition();
        pos.setX(pos.getX() + deltaX);
        pos.setY(pos.getY() + deltaY);
    }

    private void updateAltitude() {
        int current = getAltitude();
        int target = getTargetAltitude();

        if (current < target) {
            setAltitude(Math.min(current + getModel().getClimbRate(), target));
        } else if (current > target) {
            setAltitude(Math.max(current - getModel().getClimbRate(), target));
        }
    }

    private void updateHeading() {
        int current = getHeading();
        int target = getTargetHeading();

        int diff = (target - current + 360) % 360;

        if (diff <= getModel().getTurningRate()) {
            setHeading(target);
        } else if (diff < 180) {
            setHeading(current + getModel().getTurningRate());
        } else if (diff > 180) {
            setHeading(current - getModel().getTurningRate());
        }

        if (Math.abs(target - getHeading()) < getModel().getTurningRate() || Math.abs(target - getHeading()) > 360 - getModel().getTurningRate()) {
            setHeading(target);
        }

        setHeading((getHeading() + 360) % 360);
    }

    private void updateSpeed() {
        int current = getSpeed();
        int target = getTargetSpeed();

        if (current < target) {
            setSpeed(Math.min(current + getModel().getSpeedingRate(), target));
        } else if (current > target) {
            setSpeed(Math.max(current - getModel().getSpeedingRate(), target));
        }
    }

    public void updateFuel() {
        double consumptionPerSecond = getModel().getFuelConsumption() / 3600.0;
        double newFuel = getFuel() - consumptionPerSecond;

        if (newFuel < 0) { newFuel = 0; }

        setFuel(newFuel);
    }

    public boolean isReadyToLand() {
        boolean altitudeOk = this.altitude < 1000;
        boolean speedOk = this.speed < 160;

        return altitudeOk && speedOk;
    }
}