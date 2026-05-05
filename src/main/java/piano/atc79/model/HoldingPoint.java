package piano.atc79.model;

public class HoldingPoint {
    public static final double DEFAULT_HOLD_RADIUS_NM = (double)1.0F;
    private final String id;
    private final Position position;
    private final double radiusNm;

    public HoldingPoint(String id, Position position, double radiusNm) {
        this.id = id;
        this.position = position;
        this.radiusNm = radiusNm;
    }

    public String getId() {
        return this.id;
    }

    public Position getPosition() {
        return this.position;
    }

    public double getRadiusNm() {
        return this.radiusNm;
    }
}
