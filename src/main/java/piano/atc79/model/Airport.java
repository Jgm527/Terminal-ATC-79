package piano.atc79.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un aeropuerto con pistas e información básica.
 */
public class Airport {
    private String id;
    private String name;
    private List<Runway> runways;
    private int minimumVectoringAltitude;

    /**
     * Construye un nuevo Aeropuerto.
     * 
     * @param id identificador único del aeropuerto (ej. "LEAL")
     * @param name el nombre completo del aeropuerto
     * @param minimumVectoringAltitude la altitud mínima de seguridad en pies
     */
    public Airport(String id, String name, int minimumVectoringAltitude) {
        this.id = id;
        this.name = name;
        this.minimumVectoringAltitude = minimumVectoringAltitude;
        runways = new ArrayList<>();
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

    /**
     * Añade una nueva pista a este aeropuerto.
     * 
     * @param runway la {@link Runway} (pista) a añadir
     */
    public void addRunway(Runway runway) {
        runways.add(runway);
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
}
