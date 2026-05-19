package piano.atc79.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Registro singleton que almacena todos los modelos de aeronave disponibles en el juego.
 * Proporciona acceso centralizado a las especificaciones de cada avion.
 */
public final class AircraftModelRegistry {

    private static final Map<String, AircraftModel> MODELS = new HashMap<>();

    static {
        register(new AircraftModel("C172", "Cessna 172", AircraftCategory.LIGHT, 100, 50, 800.0, 5000, 10, 5, 5));
        register(new AircraftModel("PA28", "Piper PA-28", AircraftCategory.LIGHT, 90, 45, 700.0, 4500, 10, 5, 5));
        register(new AircraftModel("F27", "Fokker F27", AircraftCategory.LIGHT, 120, 60, 1200.0, 8000, 12, 4, 4));
        register(new AircraftModel("B727", "Boeing 727", AircraftCategory.MEDIUM, 150, 80, 2200.0, 28000, 18, 3, 3));
        register(new AircraftModel("B737", "Boeing 737-200", AircraftCategory.MEDIUM, 140, 75, 2500.0, 26000, 15, 3, 3));
        register(new AircraftModel("DC9", "Douglas DC-9", AircraftCategory.MEDIUM, 140, 75, 2300.0, 24000, 15, 3, 3));
        register(new AircraftModel("CRJ", "Sud Aviation Caravelle", AircraftCategory.MEDIUM, 140, 75, 2800.0, 22000, 14, 3, 3));
        register(new AircraftModel("B747", "Boeing 747-200", AircraftCategory.HEAVY, 160, 90, 4500.0, 50000, 12, 2, 2));
        register(new AircraftModel("CONC", "Concorde", AircraftCategory.HEAVY, 190, 100, 6000.0, 35000, 20, 4, 5));
        register(new AircraftModel("DC10", "McDonnell Douglas DC-10", AircraftCategory.HEAVY, 150, 80, 4000.0, 45000, 13, 2, 2));
    }

    private AircraftModelRegistry() {}

    private static void register(AircraftModel model) {
        MODELS.put(model.getId(), model);
    }

    /**
     * Obtiene un modelo de aeronave por su identificador.
     *
     * @param id el identificador del modelo (ej. "B737")
     * @return el {@link AircraftModel} correspondiente, o null si no existe
     */
    public static AircraftModel get(String id) {
        return MODELS.get(id);
    }

    /**
     * Devuelve todos los modelos de aeronave registrados.
     *
     * @return una coleccion con todos los {@link AircraftModel}
     */
    public static Collection<AircraftModel> all() {
        return MODELS.values();
    }
}
