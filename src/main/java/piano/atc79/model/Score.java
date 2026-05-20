package piano.atc79.model;

/**
 * Registra y controla el rendimiento del jugador, incluyendo la puntuación total,
 * los aterrizajes completados con éxito y los despegues.
 */
public class Score {
    private int totalPoints;
    private int successfulLandings;
    private int successfulTakesOff;

    /**
     * Inicializa un nuevo objeto Score (Puntuación) con cero puntos y cero operaciones.
     */
    public Score() {
        totalPoints = 0;
        successfulLandings = 0;
        successfulTakesOff = 0;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getSuccessfulLandings() {
        return successfulLandings;
    }

    public int getSuccessfulTakesOff() {
        return successfulTakesOff;
    }

    /**
     * Registra un aterrizaje con éxito y suma los puntos a la puntuación total.
     * 
     * @param points puntos obtenidos por el aterrizaje
     */
    public void addLanding(int points) {
        this.successfulLandings++;
        this.totalPoints += points;
    }

    public void addTakeOff(int points) {
        this.successfulTakesOff++;
        this.totalPoints += points;
    }

    // Setters para reconstruccion desde guardado
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public void setSuccessfulLandings(int successfulLandings) { this.successfulLandings = successfulLandings; }
    public void setSuccessfulTakesOff(int successfulTakesOff) { this.successfulTakesOff = successfulTakesOff; }
}
