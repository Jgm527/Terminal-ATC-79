package piano.atc79.model;

/**
 * Registra y controla el rendimiento del jugador, incluyendo la puntuación total,
 * los aterrizajes completados con éxito y los despegues.
 */
public class Score {
    private int totalPoints;
    private int successfulLandings;
    private int successfulTakesOff;
    private int consecutiveLandings;
    private int streakLevel;
    private int maxConsecutiveLandings;

    /**
     * Inicializa un nuevo objeto Score (Puntuación) con cero puntos, cero operaciones
     * y sin racha activa.
     */
    public Score() {
        totalPoints = 0;
        successfulLandings = 0;
        successfulTakesOff = 0;
        consecutiveLandings = 0;
        streakLevel = 0;
        maxConsecutiveLandings = 0;
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
     * Registra un aterrizaje con éxito y suma los puntos base al marcador.
     * Si se alcanza un hito de racha (cada 5 aterrizajes consecutivos),
     * añade el bonus de racha automáticamente.
     *
     * @param basePoints puntos base obtenidos por el aterrizaje
     * @return el bonus de racha otorgado (0 si no hubo hito)
     */
    public int addLanding(int basePoints) {
        this.successfulLandings++;
        this.totalPoints += basePoints;
        consecutiveLandings++;
        if (consecutiveLandings > maxConsecutiveLandings) {
            maxConsecutiveLandings = consecutiveLandings;
        }

        if (consecutiveLandings % 5 == 0) {
            streakLevel = consecutiveLandings / 5;
            int streakBonus = 50 * streakLevel;
            this.totalPoints += streakBonus;
            return streakBonus;
        }
        return 0;
    }

    /**
     * Reinicia la racha al producirse un incidente (colisión o fuel crítico).
     */
    public void resetStreak() {
        consecutiveLandings = 0;
        streakLevel = 0;
    }

    public int getStreakLevel() {
        return streakLevel;
    }

    public int getConsecutiveLandings() {
        return consecutiveLandings;
    }

    public int getMaxConsecutiveLandings() {
        return maxConsecutiveLandings;
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
