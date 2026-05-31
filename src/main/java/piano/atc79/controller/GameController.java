package piano.atc79.controller;

import piano.atc79.model.*;
import piano.atc79.persistence.DAO;
import piano.atc79.view.GameOverPanel;
import piano.atc79.view.WindowView;

import java.util.List;

import javax.swing.*;


/**
 * Controlador que actúa de puente entre la interfaz visual {@link WindowView} y el modelo de dominio {@link Game}.
 * Controla el reloj lógico del juego y delega los comandos.
 */
public class GameController {
    private Game game;
    private FlightSpawner spawner;
    private Timer gameTimer;
    private WindowView view;
    private Runnable onReturnToMenu;
    private final DAO dao;
    private final int playerId;

    /**
     * Construye un GameController (Controlador de Juego) para una instancia especifica.
     *
     * @param game    el modelo {@link Game} con la logica de negocio
     * @param spawner el generador de trafico {@link FlightSpawner}
     * @param dao     el DAO para persistencia en base de datos
     * @param playerId el ID del jugador logueado
     */
    public GameController(Game game, FlightSpawner spawner, DAO dao, int playerId) {
        this.game = game;
        this.spawner = spawner;
        this.dao = dao;
        this.playerId = playerId;
    }

    public void setView(WindowView view) {
        this.view = view;
    }

    /**
     * Inicia el componente temporizador principal del juego, actualizando periódicamente el modelo y la vista.
     */
    public void start() {
        gameTimer = new Timer(1000, e -> {
            if (!game.isGameOver()) {
                spawner.tick();
                game.update();

                view.getRadar().updateScore(
                    game.getScore().getTotalPoints(),
                    game.getScore().getSuccessfulLandings(),
                    game.getScore().getStreakLevel()
                );

                for (String event : game.pullEvents()) {
                    view.getRadar().logTypedMessage(event, "INFO");
                }

                view.updateView(game.getFlights());
            } else {
                // Recolectar estadisticas de la partida
                Score s = game.getScore();
                String airportCode = game.getAirport().getId();
                int score = s.getTotalPoints();
                int landings = s.getSuccessfulLandings();
                int streakMax = s.getMaxConsecutiveLandings();
                int duration = game.getTickCount();
                String cause = game.getGameOverCause() != null ? game.getGameOverCause() : "UNKNOWN";

                // Borrar archivo de guardado local si existe
                SaveManager.deleteSave(game.getCurrentSaveFilePath());

                // Guardar en base de datos
                dao.saveGameSession(playerId, airportCode, score, landings, streakMax, duration, cause);

                // Parar el timer
                gameTimer.stop();

                // Mostrar pantalla de fin de partida
                JFrame gameWindow = view.getWindow();
                GameOverPanel.showDialog(gameWindow, airportCode, score, landings,
                        streakMax, duration, cause, this::quitToMenu);
            }
        });
        gameTimer.start();
    }

    /**
     * Pasa un comando de texto al modelo del juego y registra su salida en la consola de la vista.
     * 
     * @param command orden escrita en bruto
     */
    public void executeCommand(String command) {
        try {
            String feedback = game.executeCommand(command);
            view.getRadar().logTypedMessage(feedback, "SUCCESS");
        } catch (CommandExceptions e) {
            view.getRadar().logTypedMessage(e.getMessage(), "ERROR");
        }
    }

    public List<Flight> getFlights() { return game.getFlights(); }
    public Airport getAirport() { return game.getAirport(); }

    /**
     * Guarda la partida actual con el nombre especificado y muestra feedback en el log.
     *
     * @param saveName nombre descriptivo para la partida
     */
    public void saveGame(String saveName) {
        String result = game.saveGame(saveName);
        view.getRadar().logTypedMessage(">>> PARTIDA GUARDADA: " + result, "SUCCESS");
    }

    /**
     * Indica si la partida actual fue cargada desde un archivo existente.
     * Si es true, al guardar se reutiliza el nombre sin preguntar.
     */
    public boolean hasExistingSave() {
        return game.hasExistingSave();
    }

    /**
     * Devuelve el nombre de la partida si fue cargada, o null si es nueva.
     */
    public String getCurrentSaveName() {
        return game.getCurrentSaveName();
    }

    /**
     * Establece el callback que se ejecuta al solicitar volver al menu principal.
     */
    public void setOnReturnToMenu(Runnable callback) {
        this.onReturnToMenu = callback;
    }

    /**
     * Cierra la partida y vuelve al menu principal.
     */
    public void quitToMenu() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (view != null) {
            view.close();
        }
        if (onReturnToMenu != null) {
            onReturnToMenu.run();
        }
    }
}