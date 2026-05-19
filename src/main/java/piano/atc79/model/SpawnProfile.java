package piano.atc79.model;

import java.util.Map;

/**
 * Configuracion inmutable que define las reglas de generacion de trafico
 * aereo para un aeropuerto concreto. Incluye densidad, variacion, cap de
 * vuelos, pesos de modelos y rangos de altitud.
 */
public class SpawnProfile {

    private final int baseTicksBetweenSpawns;
    private final double spawnVariationPercent;
    private final int maxConcurrentFlights;
    private final int initialBurstMin;
    private final int initialBurstMax;
    private final Map<String, Integer> modelWeights;
    private final int minAltitude;
    private final int maxAltitude;

    /**
     * Construye un perfil de spawn con todos los parametros.
     *
     * @param baseTicksBetweenSpawns  ticks base entre spawns (1 tick = 1 segundo)
     * @param spawnVariationPercent   fraccion de variacion aleatoria (ej. 0.20 para +/-20%)
     * @param maxConcurrentFlights    maximo de vuelos simultaneos permitidos
     * @param initialBurstMin         minimo de vuelos en la rafaga inicial
     * @param initialBurstMax         maximo de vuelos en la rafaga inicial
     * @param modelWeights            mapa ID de modelo -> peso (suma debe ser 100)
     * @param minAltitude             altitud minima de entrada en pies
     * @param maxAltitude             altitud maxima de entrada en pies
     */
    public SpawnProfile(int baseTicksBetweenSpawns, double spawnVariationPercent,
                        int maxConcurrentFlights, int initialBurstMin, int initialBurstMax,
                        Map<String, Integer> modelWeights, int minAltitude, int maxAltitude) {
        this.baseTicksBetweenSpawns = baseTicksBetweenSpawns;
        this.spawnVariationPercent = spawnVariationPercent;
        this.maxConcurrentFlights = maxConcurrentFlights;
        this.initialBurstMin = initialBurstMin;
        this.initialBurstMax = initialBurstMax;
        this.modelWeights = modelWeights;
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
    }

    /**
     * Fabrica un perfil de spawn preconfigurado para un aeropuerto dado.
     * Los pesos de modelos reflejan la distribucion realista de trafico
     * de los anos 70 en cada aeropuerto.
     * <p>
     * La dificultad progresiva se basa en dos parametros clave:
     * <ul>
     *   <li><b>maxConcurrentFlights</b> — cuantos aviones puede haber simultaneamente (4-10)</li>
     *   <li><b>baseTicksBetweenSpawns</b> — cada cuanto aparece uno nuevo (90-50 segundos)</li>
     * </ul>
     *
     * @param airportCode el codigo ICAO del aeropuerto
     * @return el {@link SpawnProfile} correspondiente
     */
    public static SpawnProfile forAirport(String airportCode) {
        return switch (airportCode) {
            case "LEAL" -> new SpawnProfile(
                    90, 0.20, 4, 1, 2,
                    Map.of("C172", 5, "PA28", 5, "F27", 20, "B727", 15,
                           "B737", 20, "DC9", 15, "B747", 5, "CONC", 5,
                           "DC10", 5, "CRJ", 5),
                    3000, 6000);
            case "LEBL" -> new SpawnProfile(
                    80, 0.20, 5, 1, 2,
                    Map.of("C172", 3, "PA28", 3, "F27", 15, "B727", 15,
                           "B737", 22, "DC9", 15, "B747", 12, "CONC", 5,
                           "DC10", 5, "CRJ", 5),
                    3500, 8000);
            case "KLAX" -> new SpawnProfile(
                    70, 0.25, 6, 2, 3,
                    Map.of("C172", 2, "PA28", 2, "F27", 8, "B727", 15,
                           "B737", 25, "DC9", 12, "B747", 18, "CONC", 5,
                           "DC10", 8, "CRJ", 5),
                    4000, 10000);
            case "EGLL" -> new SpawnProfile(
                    62, 0.25, 7, 2, 3,
                    Map.of("C172", 1, "PA28", 1, "F27", 7, "B727", 15,
                           "B737", 25, "DC9", 12, "B747", 20, "CONC", 8,
                           "DC10", 8, "CRJ", 3),
                    4000, 11000);
            case "GCXO" -> new SpawnProfile(
                    56, 0.20, 8, 2, 3,
                    Map.of("C172", 10, "PA28", 10, "F27", 25, "B727", 10,
                           "B737", 15, "DC9", 10, "B747", 5, "CONC", 5,
                           "DC10", 5, "CRJ", 5),
                    2500, 6000);
            case "BIKF" -> new SpawnProfile(
                    52, 0.20, 9, 2, 3,
                    Map.of("C172", 5, "PA28", 5, "F27", 15, "B727", 15,
                           "B737", 20, "DC9", 12, "B747", 12, "CONC", 8,
                           "DC10", 5, "CRJ", 3),
                    3000, 8000);
            case "KJFK" -> new SpawnProfile(
                    50, 0.25, 10, 3, 4,
                    Map.of("C172", 1, "PA28", 1, "F27", 3, "B727", 12,
                           "B737", 25, "DC9", 10, "B747", 22, "CONC", 10,
                           "DC10", 10, "CRJ", 6),
                    4000, 12000);
            default -> forAirport("LEAL");
        };
    }

    public int getBaseTicksBetweenSpawns() {
        return baseTicksBetweenSpawns;
    }

    public double getSpawnVariationPercent() {
        return spawnVariationPercent;
    }

    public int getMaxConcurrentFlights() {
        return maxConcurrentFlights;
    }

    public int getInitialBurstMin() {
        return initialBurstMin;
    }

    public int getInitialBurstMax() {
        return initialBurstMax;
    }

    public Map<String, Integer> getModelWeights() {
        return modelWeights;
    }

    public int getMinAltitude() {
        return minAltitude;
    }

    public int getMaxAltitude() {
        return maxAltitude;
    }
}
