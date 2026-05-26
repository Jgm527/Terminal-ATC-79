package piano.atc79.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para hash de contraseñas usando SHA-256.
 * <p>
 * Metodos estaticos sin estado. No usa salt porque es un juego
 * local de un solo PC — no merece la complejidad.</p>
 */
public final class PasswordHash {

    private PasswordHash() {}

    /**
     * Calcula el hash SHA-256 de una contraseña y lo devuelve
     * como cadena hexadecimal de 64 caracteres.
     *
     * @param password la contraseña en texto plano
     * @return hash hexadecimal de 64 caracteres
     */
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible en esta JVM", e);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     *
     * @param password    la contraseña a verificar
     * @param storedHash  el hash almacenado (64 caracteres hex)
     * @return true si coinciden, false en caso contrario
     */
    public static boolean verify(String password, String storedHash) {
        return hash(password).equals(storedHash);
    }
}
