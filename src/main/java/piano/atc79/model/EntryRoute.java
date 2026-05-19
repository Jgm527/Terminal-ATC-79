package piano.atc79.model;

import java.util.List;
import java.util.Random;

/**
 * Representa un corredor de llegada (aerovia / STAR simplificado) al espacio aereo
 * de un aeropuerto. Cada ruta define un heading de entrada y varios puntos posibles
 * de spawn a lo largo del corredor, para crear flujo de trafico realista y variado.
 */
public class EntryRoute {
    private final String name;
    private final int heading;
    private final List<Position> spawnPoints;

    /**
     * Construye una nueva ruta de entrada.
     *
     * @param name        el nombre identificativo de la ruta (ej. "GIVAR", "SOLAR")
     * @param heading     el rumbo magnetico del corredor en grados
     * @param spawnPoints lista de posiciones posibles donde pueden aparecer aeronaves
     */
    public EntryRoute(String name, int heading, List<Position> spawnPoints) {
        this.name = name;
        this.heading = heading;
        this.spawnPoints = spawnPoints;
    }

    /**
     * Devuelve el nombre de esta ruta de entrada.
     *
     * @return el nombre identificativo
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve el rumbo magnetico del corredor.
     *
     * @return el heading en grados (0-359)
     */
    public int getHeading() {
        return heading;
    }

    /**
     * Devuelve la lista completa de puntos de spawn definidos para esta ruta.
     *
     * @return la lista de {@link Position} posibles
     */
    public List<Position> getSpawnPoints() {
        return spawnPoints;
    }

    /**
     * Selecciona aleatoriamente uno de los puntos de spawn de esta ruta.
     *
     * @param random el generador de numeros aleatorios
     * @return una {@link Position} elegida al azar entre las disponibles
     */
    public Position pickRandomSpawnPoint(Random random) {
        return spawnPoints.get(random.nextInt(spawnPoints.size()));
    }
}
