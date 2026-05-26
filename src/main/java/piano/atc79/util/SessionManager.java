package piano.atc79.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Gestiona la sesion persistente del jugador mediante un archivo local.
 * <p>
 * Al hacer login correcto, se guarda el alias en {@code last_player.txt}.
 * Al arrancar el juego, si el archivo existe, se reanuda la sesion automaticamente.
 * Al cerrar sesion, se borra el archivo.</p>
 */
public final class SessionManager {

    private static final String SESSION_FILE = "last_player.txt";

    private SessionManager() {}

    /**
     * Guarda el alias del jugador en el archivo de sesion.
     * Sobrescribe cualquier sesion anterior.
     *
     * @param alias el alias del jugador logueado
     */
    public static void saveSession(String alias) {
        try (PrintWriter writer = new PrintWriter(SESSION_FILE, StandardCharsets.UTF_8.name())) {
            writer.println(alias);
        } catch (IOException e) {
            System.err.println("No se pudo guardar la sesion: " + e.getMessage());
        }
    }

    /**
     * Carga el alias de la sesion persistida, si existe.
     *
     * @return el alias del jugador, o {@code null} si no hay sesion activa
     */
    public static String loadSession() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String alias = reader.readLine();
            return (alias != null && !alias.trim().isEmpty()) ? alias.trim() : null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Elimina el archivo de sesion. Se llama al cerrar sesion.
     */
    public static void clearSession() {
        File file = new File(SESSION_FILE);
        if (file.exists() && !file.delete()) {
            System.err.println("No se pudo borrar el archivo de sesion");
        }
    }
}
