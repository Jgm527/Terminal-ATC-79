package piano.atc79.persistence;

import io.github.cdimascio.dotenv.Dotenv;
import piano.atc79.model.*;

import java.sql.*;
import java.util.*;

/**
 * Implementacion de {@link DAO} para PostgreSQL.
 * <p>
 * Lee la configuracion del archivo {@code .env} y abre una conexion
 * JDBC por cada operacion usando try-with-resources.
 * Las excepciones SQL se capturan y se muestran por consola.</p>
 */
public class PostgresDAO implements DAO {

    private final String url;
    private final String user;
    private final String password;

    public PostgresDAO() {
        Dotenv dotenv = Dotenv.configure().load();
        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String db = dotenv.get("DB_NAME");
        this.user = dotenv.get("DB_USER");
        this.password = dotenv.get("DB_PASSWORD");
        this.url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
    }

    // ========================================================================
    //  JUGADORES
    // ========================================================================

    @Override
    public boolean createPlayer(String alias, String passwordHash) {
        String sql = "INSERT INTO players (alias, password_hash) VALUES (?, ?)";
        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, alias);
            sentencia.setString(2, passwordHash);
            sentencia.executeUpdate();
            return true;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return false; // unique violation — alias ya existe
            }
            System.out.println("Error al crear jugador: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Integer loginPlayer(String alias, String passwordHash) {
        String sql = "SELECT player_id, password_hash FROM players WHERE alias = ?";
        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, alias);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    String storedHash = resultado.getString("password_hash");
                    if (storedHash.equals(passwordHash)) {
                        return resultado.getInt("player_id");
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Error al hacer login: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getAlias(int playerId) {
        String sql = "SELECT alias FROM players WHERE player_id = ?";
        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, playerId);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getString("alias") : null;
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener alias: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Integer getPlayerIdByAlias(String alias) {
        String sql = "SELECT player_id FROM players WHERE alias = ?";
        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, alias);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getInt("player_id") : null;
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener playerId por alias: " + e.getMessage());
            return null;
        }
    }

    // ========================================================================
    //  AEROPUERTOS
    // ========================================================================

    @Override
    public Airport loadAirport(String airportCode) {
        String sqlAirport = "SELECT name, min_vectoring_alt, difficulty_multiplier FROM airports WHERE airport_code = ?";
        String sqlRunways = "SELECT runway_code, start_x, start_y, end_x, end_y, has_ils FROM runways WHERE airport_code = ?";
        String sqlHoldings = "SELECT point_code, pos_x, pos_y, radius_nm FROM holding_points WHERE airport_code = ?";
        String sqlRoutes = "SELECT r.route_id, r.route_name, r.heading, s.pos_x, s.pos_y " +
                "FROM entry_routes r " +
                "LEFT JOIN entry_route_spawns s ON s.route_id = r.route_id " +
                "WHERE r.airport_code = ? " +
                "ORDER BY r.route_id, s.spawn_id";

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement stmtAirport = conexion.prepareStatement(sqlAirport);
             PreparedStatement stmtRunways = conexion.prepareStatement(sqlRunways);
             PreparedStatement stmtHoldings = conexion.prepareStatement(sqlHoldings);
             PreparedStatement stmtRoutes = conexion.prepareStatement(sqlRoutes)) {

            // 1. Datos del aeropuerto
            stmtAirport.setString(1, airportCode);
            Airport airport;
            try (ResultSet rs = stmtAirport.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Aeropuerto no encontrado: " + airportCode);
                }
                airport = new Airport(airportCode, rs.getString("name"), rs.getInt("min_vectoring_alt"));
                airport.setDifficultyMultiplier(rs.getDouble("difficulty_multiplier"));
            }

            // 2. Pistas
            stmtRunways.setString(1, airportCode);
            try (ResultSet rs = stmtRunways.executeQuery()) {
                while (rs.next()) {
                    Position start = new Position(rs.getDouble("start_x"), rs.getDouble("start_y"), 0);
                    Position end = new Position(rs.getDouble("end_x"), rs.getDouble("end_y"), 0);
                    airport.addRunway(new Runway(rs.getString("runway_code"), start, end, rs.getBoolean("has_ils")));
                }
            }

            // 3. Puntos de espera
            stmtHoldings.setString(1, airportCode);
            try (ResultSet rs = stmtHoldings.executeQuery()) {
                while (rs.next()) {
                    Position pos = new Position(rs.getDouble("pos_x"), rs.getDouble("pos_y"), 0);
                    airport.addHoldingPoint(new HoldingPoint(rs.getString("point_code"), pos, rs.getDouble("radius_nm")));
                }
            }

            // 4. Rutas de entrada con sus puntos de spawn
            stmtRoutes.setString(1, airportCode);
            java.util.Map<Integer, java.util.List<Position>> routeSpawns = new java.util.HashMap<>();
            java.util.Map<Integer, String> routeNames = new java.util.HashMap<>();
            java.util.Map<Integer, Integer> routeHeadings = new java.util.HashMap<>();
            java.util.Set<Integer> routeIds = new java.util.LinkedHashSet<>();

            try (ResultSet rs = stmtRoutes.executeQuery()) {
                while (rs.next()) {
                    int routeId = rs.getInt("route_id");
                    routeIds.add(routeId);
                    routeNames.put(routeId, rs.getString("route_name"));
                    routeHeadings.put(routeId, rs.getInt("heading"));
                    routeSpawns.computeIfAbsent(routeId, k -> new java.util.ArrayList<>())
                            .add(new Position(rs.getDouble("pos_x"), rs.getDouble("pos_y"), 0));
                }
            }

            for (int routeId : routeIds) {
                airport.addEntryRoute(new EntryRoute(
                        routeNames.get(routeId),
                        routeHeadings.get(routeId),
                        routeSpawns.get(routeId)
                ));
            }

            return airport;

        } catch (SQLException e) {
            System.out.println("Error al cargar aeropuerto: " + e.getMessage());
            return null;
        }
    }

    // ========================================================================
    //  PARTIDAS
    // ========================================================================

    @Override
    public void saveGameSession(int playerId, String airportCode, int score,
                                int landings, int streakMax, int duration, String cause) {
        String sql = "INSERT INTO game_sessions " +
                "(player_id, airport_code, score_total, successful_landings, " +
                "streak_max, duration_seconds, game_over_cause) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, playerId);
            sentencia.setString(2, airportCode);
            sentencia.setInt(3, score);
            sentencia.setInt(4, landings);
            sentencia.setInt(5, streakMax);
            sentencia.setInt(6, duration);
            sentencia.setString(7, cause);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar partida: " + e.getMessage());
        }
    }

    @Override
    public List<LeaderboardEntry> getLeaderboard(String airportCode, int limit) {
        String sql = "SELECT p.alias, g.score_total, g.successful_landings, " +
                "g.game_over_cause, g.completed_at " +
                "FROM game_sessions g " +
                "JOIN players p ON p.player_id = g.player_id " +
                "WHERE g.airport_code = ? " +
                "ORDER BY g.score_total DESC LIMIT ?";
        List<LeaderboardEntry> entries = new ArrayList<>();
        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, airportCode);
            sentencia.setInt(2, limit);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    entries.add(new LeaderboardEntry(
                            resultado.getString("alias"),
                            resultado.getInt("score_total"),
                            resultado.getInt("successful_landings"),
                            resultado.getString("game_over_cause"),
                            resultado.getTimestamp("completed_at") != null
                                    ? resultado.getTimestamp("completed_at").toString()
                                    : ""
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener leaderboard: " + e.getMessage());
        }
        return entries;
    }
}
