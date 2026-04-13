package piano.atc79.model;

/**
 * Excepción lanzada cuando un comando del usuario no puede ser analizado o ejecutado correctamente.
 */
public class CommandExceptions extends Exception {
    public CommandExceptions(String message) {
        super(message);
    }
}
