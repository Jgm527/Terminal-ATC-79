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
 * selecciona el aeropuerto antes de comenzar la partida.</p>
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

        TitleScreen titleScreen = new TitleScreen(airportCode -> {
            frame.dispose();
            startGame(airportCode);
        });

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

        SwingUtilities.invokeLater(() -> {
            WindowView view = new WindowView(controller);
            controller.setView(view);
            view.show();
            controller.start();
        });
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
