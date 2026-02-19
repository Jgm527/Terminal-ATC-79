package piano.atc79.model;

public class Position {
    private double x; //Millas
    private double y; //Millas
    private int z; //Pies

    public Position(double x, double y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("[X:%.1f, Y:%.1f, Alt:%.0f]", x, y, z);
    }

    public double distanceTo(Position other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return  Math.sqrt(dx * dx + dy * dy);
    }
}
