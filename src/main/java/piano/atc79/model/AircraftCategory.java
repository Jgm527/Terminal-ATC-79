package piano.atc79.model;

/**
 * Enumeración que representa las categorías de aeronaves y sus propiedades,
 * las cuales afectan a la jugabilidad y reglas de separación.
 */
public enum AircraftCategory {
    LIGHT(1, 3.0, false),
    MEDIUM(2, 4.0, false),
    HEAVY(3, 6.0, false);

    private final int wakeIntensity;
    private final double minSeparationNM;
    private final boolean requiresLongRunway;

    /**
     * Construye un AircraftCategory con la intensidad de estela turbulenta,
     * regla de separación requerida y requisito de pista.
     *
     * @param wakeIntensity      la intensidad de la estela turbulenta de la categoría
     * @param minSeparationNM    la distancia mínima de separación requerida en millas náuticas
     * @param requiresLongRunway si la categoría necesita una pista larga para despegar o aterrizar
     */
    AircraftCategory(int wakeIntensity, double minSeparationNM, boolean requiresLongRunway) {
        this.wakeIntensity = wakeIntensity;
        this.minSeparationNM = minSeparationNM;
        this.requiresLongRunway = requiresLongRunway;
    }

    //Getters
    
    /**
     * Obtiene la intensidad de la estela de la categoría de la aeronave.
     *
     * @return la intensidad de la estela como entero
     */
    public int getWakeIntensity() {
        return wakeIntensity;
    }

    /**
     * Obtiene la distancia de separación mínima requerida en millas náuticas para esta categoría.
     *
     * @return la distancia de separación en millas náuticas (NM)
     */
    public double getMinSeparationNM() {
        return minSeparationNM;
    }

    /**
     * Comprueba si esta categoría de aeronave requiere una pista larga.
     *
     * @return true si requiere una pista larga, false en caso contrario
     */
    public boolean requiresLongRunway() {
        return requiresLongRunway;
    }
}
