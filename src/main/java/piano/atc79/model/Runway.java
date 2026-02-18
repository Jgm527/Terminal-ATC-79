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
        double dx = endPoint.getX() - startPoint.getX();
        double dy = endPoint.getY() - startPoint.getY();

        double radians = Math.atan2(dx, dy);
        double degrees = Math.toDegrees(radians);

        return (int) (degrees + 360) % 360;
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