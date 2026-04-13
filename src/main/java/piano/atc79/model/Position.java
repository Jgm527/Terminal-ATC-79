package piano.atc79.model;

/**
 * Representa una posición en 3D en el espacio del juego.
 * Los ejes X e Y se miden en Millas Náuticas (NM), y la altitud Z en Pies (Feet).
 */
public class Position {
    private double x; //Millas
    private double y; //Millas
    private int z; //Pies

    /**
     * Construye un objeto Position.
     * 
     * @param x la coordenada X en millas náuticas
     * @param y la coordenada Y en millas náuticas
     * @param z la coordenada Z (altitud) en pies
     */
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

    /**
     * Calcula la distancia euclidiana 2D (en el plano X, Y) hacia otra posición.
     * La Z (altitud) no se tiene en cuenta en este cálculo.
     * 
     * @param other la posición objetivo
     * @return la distancia en millas náuticas
     */
    public double distanceTo(Position other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return  Math.sqrt(dx * dx + dy * dy);
    }
}
