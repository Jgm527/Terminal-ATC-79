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
}
