package piano.atc79.model;

/**
 * Representa una pista en un aeropuerto con sus coordenadas de inicio y fin.
 */
public class Runway {
    private String id;
    private Position startPoint;
    private Position endPoint;
    private boolean isOccupied;
    private boolean hasILS;

    /**
     * Construye una nueva pista de aterrizaje (Runway).
     * 
     * @param id identificador de la pista (ej. "10", "28R")
     * @param startPoint la {@link Position} de inicio
     * @param endPoint la {@link Position} de finalización
     * @param hasILS indica si la pista tiene Sistema Instrumental de Aterrizaje
     */
    public Runway(String id, Position startPoint, Position endPoint, boolean hasILS) {
        this.id = id;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.hasILS = hasILS;
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

    public boolean hasILS() {
        return hasILS;
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

    /**
     * Comprueba si un vuelo está alineado correctamente con la pista para la aproximación.
     * 
     * @param flight el {@link Flight} a verificar
     * @return true si el vuelo está alineado y en rango, false en caso contrario
     */
    public boolean isAligned(Flight flight) {
        double distance = flight.getCurrentPosition().distanceTo(this.startPoint);

        int diff = Math.abs(flight.getHeading() - getHeading());
        if (diff > 180) {
            diff = 360 - diff;
        }
        return distance < 3.0 && diff < 10;
    }
}