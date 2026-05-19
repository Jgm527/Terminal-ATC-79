package piano.atc79.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa un aeropuerto con pistas e informacion basica.
 */
public class Airport {
    private String id;
    private String name;
    private List<Runway> runways;
    private List<HoldingPoint> holdingPoints;
    private List<EntryRoute> entryRoutes;
    private int minimumVectoringAltitude;

    /**
     * Construye un nuevo Aeropuerto.
     *
     * @param id identificador unico del aeropuerto (ej. "LEAL")
     * @param name el nombre completo del aeropuerto
     * @param minimumVectoringAltitude la altitud minima de seguridad en pies
     */
    public Airport(String id, String name, int minimumVectoringAltitude) {
        this.id = id;
        this.name = name;
        this.minimumVectoringAltitude = minimumVectoringAltitude;
        runways = new ArrayList<>();
        holdingPoints = new ArrayList<>();
        entryRoutes = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Runway> getRunways() {
        return runways;
    }

    public int getMinimumVectoringAltitude() {
        return minimumVectoringAltitude;
    }

    public List<HoldingPoint> getHoldingPoints() {
        return holdingPoints;
    }

    /**
     * Añade una nueva pista a este aeropuerto.
     * 
     * @param runway la {@link Runway} (pista) a añadir
     */
    public void addRunway(Runway runway) {
        runways.add(runway);
    }

    /**
     * Añade un punto de espera al aeropuerto.
     *
     * @param holdingPoint punto de espera a añadir
     */
    public void addHoldingPoint(HoldingPoint holdingPoint) {
        holdingPoints.add(holdingPoint);
    }

    /**
     * Busca una pista por su identificador.
     * 
     * @param id el identificador de la pista
     * @return la {@link Runway} si se encuentra, o null en caso contrario
     */
    public Runway findRunway(String id) {
        for (Runway r : runways) {
            if (r.getId().equalsIgnoreCase(id)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Busca un punto de espera por identificador.
     *
     * @param id identificador del punto de espera
     * @return el punto si existe, o null en caso contrario
     */
    public HoldingPoint findHoldingPoint(String id) {
        for (HoldingPoint point : holdingPoints) {
            if (point.getId().equalsIgnoreCase(id)) {
                return point;
            }
        }
        return null;
    }

    /**
     * Anade una nueva ruta de entrada (aerovia) a este aeropuerto.
     *
     * @param route la {@link EntryRoute} a anadir
     */
    public void addEntryRoute(EntryRoute route) {
        entryRoutes.add(route);
    }

    /**
     * Devuelve la lista de rutas de entrada configuradas para este aeropuerto.
     *
     * @return la lista de {@link EntryRoute}
     */
    public List<EntryRoute> getEntryRoutes() {
        return entryRoutes;
    }

    /**
     * Selecciona aleatoriamente una de las rutas de entrada disponibles.
     *
     * @param random el generador de numeros aleatorios
     * @return una {@link EntryRoute} elegida al azar
     */
    public EntryRoute pickRandomEntryRoute(Random random) {
        return entryRoutes.get(random.nextInt(entryRoutes.size()));
    }
}
