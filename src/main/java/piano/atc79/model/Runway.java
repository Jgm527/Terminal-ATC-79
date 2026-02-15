package piano.atc79.model;

public class Runway {
    private String id;
    private Position startPoint;
    private Position endPoint;
    private boolean isOccupied;

    public Runway(String id, Position startPoint, Position endPoint) {
        this.id = id;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        isOccupied = false;
    }

    public String getId() {
        return id;
    }

    public Position getStartPoint() {
        return startPoint;
    }

    public Position getEndPoint() {
        return endPoint;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public double getLength() {
        return startPoint.distanceTo(endPoint);
    }

    public int getHeading() {
        double dx = startPoint.getX() - endPoint.getX();
        double dy = startPoint.getY() - endPoint.getY();
        double runwayHeadingRadians = Math.atan2(dy, dx);
        double runwayHeadingDegrees = Math.toDegrees(runwayHeadingRadians);

        return (int) (runwayHeadingDegrees + 360) % 360;
    }

    public boolean isAligned(Flight flight) {
        double distance = flight.getCurrentPosition().distanceTo(this.startPoint);

        int diff = Math.abs(flight.getHeading() - getHeading());
        if (diff > 180) {
            diff = 360 - diff;
        }
        return distance < 3.0 && diff < 10;
    }
}