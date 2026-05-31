package piano.atc79.persistence;

import piano.atc79.model.Airport;

import java.util.List;

/**
 * Interfaz de acceso a datos para Terminal ATC-79.
 * <p>
 * Define las operaciones de persistencia necesarias para el juego.
 * La implementacion concreta ({@link PostgresDAO}) usa PostgreSQL,
 * pero se puede sustituir por otra base de datos (MongoDB, etc.)
 * sin cambiar el resto del juego.</p>
 */
public interface DAO {

    // ========== JUGADORES ==========

    /**
     * Crea un nuevo jugador en la base de datos.
     *
     * @param alias        nombre unico del jugador
     * @param passwordHash hash SHA-256 de la contrasena
     * @return true si se creo correctamente, false si el alias ya existe
     */
    boolean createPlayer(String alias, String passwordHash);

    /**
     * Verifica las credenciales de un jugador y devuelve su ID.
     *
     * @param alias        nombre del jugador
     * @param passwordHash hash SHA-256 de la contrasena introducida
     * @return el player_id si las credenciales son correctas, null si no
     */
    Integer loginPlayer(String alias, String passwordHash);

    /**
     * Devuelve el alias de un jugador por su ID.
     *
     * @param playerId el identificador del jugador
     * @return el alias, o null si no existe
     */
    String getAlias(int playerId);

    /**
     * Devuelve el ID de un jugador por su alias.
     *
     * @param alias el nombre del jugador
     * @return el player_id, o null si no existe
     */
    Integer getPlayerIdByAlias(String alias);

    // ========== AEROPUERTOS ==========

    /**
     * Carga un aeropuerto completo desde la base de datos, incluyendo
     * sus pistas, puntos de espera, rutas de entrada y perfil de spawn.
     *
     * @param airportCode codigo ICAO del aeropuerto (ej. "LEAL")
     * @return el objeto {@link Airport} completamente configurado
     */
    Airport loadAirport(String airportCode);

    // ========== PARTIDAS ==========

    /**
     * Guarda una partida completada en la base de datos.
     */
    void saveGameSession(int playerId, String airportCode, int score,
                         int landings, int streakMax, int duration, String cause);

    /**
     * Obtiene las mejores puntuaciones para un aeropuerto.
     *
     * @param airportCode codigo ICAO del aeropuerto
     * @param limit       maximo de resultados
     * @return lista de entradas del leaderboard
     */
    List<LeaderboardEntry> getLeaderboard(String airportCode, int limit);
}
