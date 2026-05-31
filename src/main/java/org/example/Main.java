package org.example;

import piano.atc79.model.*;
import piano.atc79.controller.*;
import piano.atc79.persistence.DAO;
import piano.atc79.persistence.PostgresDAO;
import piano.atc79.util.SessionManager;
import piano.atc79.view.*;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada principal para la aplicacion Terminal ATC 79.
 * <p>
 * Flujo de inicio:
 * <ol>
 *   <li>Conectar a la base de datos</li>
 *   <li>Comprobar si hay sesion persistente (last_player.txt)</li>
 *   <li>Si no hay sesion, mostrar LoginDialog</li>
 *   <li>Mostrar pantalla de titulo con el alias del jugador</li>
 * </ol>
 */
public class Main {

    /** Alias del jugador activo en esta sesion. */
    private static String currentAlias;

    /** ID del jugador activo en esta sesion. */
    private static int playerId;

    /** DAO para acceso a base de datos. */
    private static final DAO dao = new PostgresDAO();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startLoginFlow);
    }

    /**
     * Inicia el flujo de login: sesion persistente o dialogo de login.
     */
    private static void startLoginFlow() {
        String savedAlias = SessionManager.loadSession();
        if (savedAlias != null) {
            currentAlias = savedAlias;
            Integer id = dao.getPlayerIdByAlias(savedAlias);
            playerId = id != null ? id : -1;
            showTitleScreen();
        } else {
            showLoginDialog();
        }
    }

    /**
     * Muestra el dialogo de inicio de sesion.
     */
    private static void showLoginDialog() {
        JDialog dialog = new LoginDialog(null, dao);
        dialog.setVisible(true);

        if (dialog instanceof LoginDialog && ((LoginDialog) dialog).isSucceeded()) {
            currentAlias = SessionManager.loadSession();
            playerId = ((LoginDialog) dialog).getPlayerId();
            showTitleScreen();
        } else {
            System.exit(0);
        }
    }

    /**
     * Muestra la pantalla de titulo con la rejilla de aeropuertos disponibles.
     */
    private static void showTitleScreen() {
        JFrame frame = new JFrame("Terminal ATC 79");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        TitleScreen titleScreen = new TitleScreen(
                currentAlias,
                dao,
                airportCode -> {
                    frame.dispose();
                    startGame(airportCode);
                },
                filePath -> {
                    frame.dispose();
                    loadGame(filePath);
                },
                () -> {
                    frame.dispose();
                    SessionManager.clearSession();
                    currentAlias = null;
                    showLoginDialog();
                }
        );

        frame.add(titleScreen);
        frame.setVisible(true);
    }

    /**
     * Inicia el juego con el aeropuerto seleccionado por el jugador.
     */
    private static void startGame(String airportCode) {
        Airport airport = createAirportFromCode(airportCode);
        Game game = new Game(airport);

        SpawnProfile profile = SpawnProfile.forAirport(airportCode);
        FlightSpawner spawner = new FlightSpawner(game, profile);

        GameController controller = new GameController(game, spawner, dao, playerId);
        controller.setOnReturnToMenu(() -> SwingUtilities.invokeLater(Main::showTitleScreen));

        SwingUtilities.invokeLater(() -> {
            WindowView view = new WindowView(controller);
            controller.setView(view);
            view.show();
            controller.start();
        });
    }

    /**
     * Carga una partida guardada desde un archivo JSON y reanuda el juego.
     */
    private static void loadGame(String filePath) {
        try {
            Game game = SaveManager.loadGame(filePath);
            String airportCode = game.getAirport().getId();

            FlightSpawner spawner = new FlightSpawner(game, SpawnProfile.forAirport(airportCode));
            spawner.suppressBurst();

            GameController controller = new GameController(game, spawner, dao, playerId);
            controller.setOnReturnToMenu(() -> SwingUtilities.invokeLater(Main::showTitleScreen));

            SwingUtilities.invokeLater(() -> {
                WindowView view = new WindowView(controller);
                controller.setView(view);
                view.show();
                controller.start();
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al cargar la partida: " + e.getMessage(),
                    "Error de carga",
                    JOptionPane.ERROR_MESSAGE
            );
            SwingUtilities.invokeLater(Main::showTitleScreen);
        }
    }

    /**
     * Carga un aeropuerto desde la base de datos o, si falla, desde la fabrica.
     *
     * @param code el codigo ICAO (ej. "LEAL", "LEBL", etc.)
     * @return el {@link Airport} configurado
     */
    private static Airport createAirportFromCode(String code) {
        Airport airport = dao.loadAirport(code);
        if (airport != null) {
            System.out.println(">>> Aeropuerto " + code + " cargado desde BD");
            return airport;
        }
        // Fallback a AirportFactory si la BD no esta disponible
        System.out.println(">>> Aeropuerto " + code + " cargado desde AirportFactory (fallback)");
        return switch (code) {
            case "LEAL" -> AirportFactory.createLEAL();
            case "LEBL" -> AirportFactory.createLEBL();
            case "KLAX" -> AirportFactory.createKLAX();
            case "EGLL" -> AirportFactory.createEGLL();
            case "GCXO" -> AirportFactory.createGCXO();
            case "BIKF" -> AirportFactory.createBIKF();
            case "KJFK" -> AirportFactory.createKJFK();
            default -> throw new IllegalArgumentException("Aeropuerto desconocido: " + code);
        };
    }
}
