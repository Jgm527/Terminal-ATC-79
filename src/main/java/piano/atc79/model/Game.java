package piano.atc79.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo lógico principal del juego. Administra el estado del aeropuerto, los vuelos,
 * la puntuación, los eventos del juego, y comprueba las condiciones de victoria o derrota.
 */
public class Game {
    private String player;
    private Airport airport;
    private List<Flight> flights;
    private Score score;
    private CommandParser commandParser;
    private boolean gameOver;
    private Map<String, String> radioTemplates;
    private List<String> eventLog;
    private SeparationRules separationRules;

    // Persistencia: seguimiento de la partida guardada
    private String currentSaveName;
    private String currentSaveFilePath;

    /**
     * Construye un nuevo Juego (Game) con un aeropuerto especificado.
     * Inicializa componentes como la puntuación y el analizador de comandos.
     * 
     * @param airport el {@link Airport} que se utilizará en el juego
     */
    public Game(Airport airport) {
        this.airport = airport;
        this.flights = new ArrayList<>();
        this.score = new Score();
        this.commandParser = new CommandParser();
        this.gameOver = false;
        this.eventLog = new ArrayList<>();
        this.separationRules = new SeparationRules();
        initRadioTemplates();
    }

    private void initRadioTemplates() {
        radioTemplates = new HashMap<>();
        radioTemplates.put("CMD_H", "%s giró a rumbo %d");
        radioTemplates.put("CMD_A", "%s se mantendrá un nivel de %d pies");
        radioTemplates.put("CMD_S", "%s cambió su velocidad a %d nudos");
        radioTemplates.put("CMD_HLD", "%s, proceda al punto de espera %s y mantenga %d pies");
        radioTemplates.put("CMD_CLRVIS", "%s, autorizado a aproximación VISUAL en la pista %s");
        radioTemplates.put("CMD_CLRILS", "%s, autorizado a aproximación ILS en la pista %s");

        radioTemplates.put("EVT_LANDED", ">>> %s ha aterrizado — +%d pts");
        radioTemplates.put("EVT_TCAS", "TCAS: Alerta de proximidad entre %s y %s.");
        radioTemplates.put("EVT_COLLISION", "COLISIÓN: Impacto frontal entre %s y %s.");
        radioTemplates.put("EVT_FUEL", "EMERGENCIA: %s con combustible crítico.");
        radioTemplates.put("EVT_STREAK", ">>> RACHA ×%d  |  +%,d pts");
    }

    public String getTemplate(String key) {
        return radioTemplates.getOrDefault(key, "Mensaje desconocido: " + key);
    }

    public void addEvent(String key, Object... args) {
        String template = getTemplate(key);
        eventLog.add(String.format(template, args));
    }

    public List<String> pullEvents() {
        List<String> currentEvents = new ArrayList<>(eventLog);
        eventLog.clear();
        return currentEvents;
    }

    /**
     * Actualiza el estado del juego para cada ciclo. Mueve los vuelos, consume combustible,
     * evalúa condiciones de aterrizaje y genera alertas si es necesario.
     */
    public void update() {
        for (int i = flights.size() - 1; i >= 0; i--) {
            Flight f = flights.get(i);
            f.updatePosition();
            f.updateFuel();

            if (f.getStatus().equals(FlightStatus.LANDING)) {
                if (f.getSpeed() <= 0 && f.getCurrentPosition().getZ() <= 5) {
                    int pts = (int) (100 * airport.getDifficultyMultiplier());
                    int streakBonus = score.addLanding(pts);
                    addEvent("EVT_LANDED", f.getCallsign(), pts);
                    if (streakBonus > 0) {
                        addEvent("EVT_STREAK", score.getStreakLevel(), streakBonus);
                    }
                    flights.remove(i);
                    continue;
                }
            }

            if (f.getApproachType() != null && f.isReadyToLand()) {
                f.setStatus(FlightStatus.LANDING);
            }

            if (f.getFuel() < 20) {
                if (Math.random() < 0.05) {
                    addEvent("EVT_FUEL", f.getCallsign());
                    score.resetStreak();
                }
            }
        }
        checkGameOver();
    }

    /**
     * Comprueba si se ha cumplido alguna condición de fin de partida, como
     * por ejemplo una colisión de aeronaves o agotamiento del combustible.
     * Activa el indicador "gameOver" si es cierto.
     */
    public void checkGameOver() {
        // Buscar aviones sin combustible
        for (int i = flights.size() - 1; i >= 0; i--) {
            Flight f = flights.get(i);

            // Buscar conflictos y colisiones entre pares de vuelos
            for (int j = i + 1; j < flights.size(); j++) {
                Flight f2 = flights.get(j);

                if (separationRules.areInCollision(f, f2)) {
                    addEvent("EVT_COLLISION", f.getCallsign(), f2.getCallsign());
                    score.resetStreak();
                    gameOver = true;
                    return;
                }

                if (separationRules.areInConflict(f, f2)) {
                    addEvent("EVT_TCAS", f.getCallsign(), f2.getCallsign());
                }
            }

            if (f.getFuel() <= 0) {
                gameOver = true;
            }
        }
    }

    /**
     * Ejecuta un comando de texto pasándolo al analizador de comandos.
     * 
     * @param command la cadena de comando introducida por el usuario
     * @return el mensaje de respuesta resultante de ejecutar el comando
     * @throws CommandExceptions si el análisis o la ejecución fallan
     */
    public String executeCommand(String command) throws CommandExceptions {
        return commandParser.parse(command, this.flights, this.airport, this);
    }

    public void addFlight(Flight f) {
        if (f != null) {
            flights.add(f);
        }
    }

    public Score getScore() {
        return score;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public Airport getAirport() {
        return airport;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    // ---------------------------------------------------------------
    //  Persistencia — seguimiento de partida guardada
    // ---------------------------------------------------------------

    /**
     * Indica si esta partida fue cargada desde un archivo existente.
     * Si es true, al guardar se reutiliza el nombre y se elimina el archivo anterior.
     */
    public boolean hasExistingSave() {
        return currentSaveFilePath != null;
    }

    public String getCurrentSaveName() {
        return currentSaveName;
    }

    public void setCurrentSaveName(String saveName) {
        this.currentSaveName = saveName;
    }

    public String getCurrentSaveFilePath() {
        return currentSaveFilePath;
    }

    public void setCurrentSaveFilePath(String filePath) {
        this.currentSaveFilePath = filePath;
    }

    /**
     * Persiste el estado actual de la partida en un archivo JSON.
     * <p>
     * Si la partida fue cargada ({@link #hasExistingSave()}), sobreescribe
     * el archivo anterior pero con un nuevo timestamp en el nombre. Si es
     * una partida nueva, genera un archivo nuevo.</p>
     *
     * @param saveName nombre descriptivo que el jugador asigna a la partida
     * @return el nombre del archivo generado (ej. "LEAL_20260520_121700.json")
     */
    public String saveGame(String saveName) {
        try {
            String oldPath = this.currentSaveFilePath;
            this.currentSaveName = saveName;
            String result = SaveManager.saveGame(this, saveName, oldPath);
            // Actualizar la ruta al nuevo archivo (se devuelve la ruta completa)
            this.currentSaveFilePath = new java.io.File(
                    SaveManager.getSavesDir(), result
            ).getAbsolutePath();
            return result;
        } catch (java.io.IOException e) {
            return "Error al guardar: " + e.getMessage();
        }
    }
}
