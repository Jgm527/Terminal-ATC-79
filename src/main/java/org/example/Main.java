package org.example;

import piano.atc79.model.*;
import piano.atc79.controller.*;
import piano.atc79.view.*;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada principal para la aplicacion Terminal ATC 79.
 * Inicializa los componentes de la arquitectura MVC y comienza el juego.
 *
 * <p>El flujo de inicio ahora muestra una pantalla de titulo donde el jugador
 * puede seleccionar un aeropuerto para empezar una partida nueva, o cargar
 * una partida guardada desde el menu principal.</p>
 */
public class Main {

    /**
     * Inicializa la aplicacion mostrando la pantalla de titulo para la seleccion
     * del aeropuerto, y posteriormente lanza el juego con la eleccion del jugador.
     */
    static void main() {
        SwingUtilities.invokeLater(Main::showTitleScreen);
    }

    /**
     * Muestra la pantalla de titulo con la rejilla de aeropuertos disponibles.
     * Cuando el jugador selecciona uno, se cierra el menu y se inicia el juego.
     */
    private static void showTitleScreen() {
        JFrame frame = new JFrame("Terminal ATC 79");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        TitleScreen titleScreen = new TitleScreen(
                airportCode -> {
                    frame.dispose();
                    startGame(airportCode);
                },
                filePath -> {
                    frame.dispose();
                    loadGame(filePath);
                }
        );

        frame.add(titleScreen);
        frame.setVisible(true);
    }

    /**
     * Inicia el juego con el aeropuerto seleccionado por el jugador.
     *
     * @param airportCode el codigo ICAO del aeropuerto elegido
     */
    private static void startGame(String airportCode) {
        Airport airport = createAirportFromCode(airportCode);
        Game game = new Game(airport);

        SpawnProfile profile = SpawnProfile.forAirport(airportCode);
        FlightSpawner spawner = new FlightSpawner(game, profile);

        GameController controller = new GameController(game, spawner);
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
     *
     * @param filePath ruta completa al archivo .json de la partida guardada
     */
    private static void loadGame(String filePath) {
        try {
            Game game = SaveManager.loadGame(filePath);
            String airportCode = game.getAirport().getId();

            FlightSpawner spawner = new FlightSpawner(game, SpawnProfile.forAirport(airportCode));
            spawner.suppressBurst();

            GameController controller = new GameController(game, spawner);
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
            // Si falla la carga, volver a la pantalla de titulo
            SwingUtilities.invokeLater(Main::showTitleScreen);
        }
    }

    /**
     * Fabrica el aeropuerto correspondiente al codigo ICAO seleccionado.
     *
     * @param code el codigo ICAO (ej. "LEAL", "LEBL", etc.)
     * @return el {@link Airport} configurado
     * @throws IllegalArgumentException si el codigo no corresponde a ningun aeropuerto
     */
    private static Airport createAirportFromCode(String code) {
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
