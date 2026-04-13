package org.example;

import piano.atc79.model.*;
import piano.atc79.controller.*;
import piano.atc79.view.*;

import javax.swing.*;

/**
 * Punto de entrada principal para la aplicación Terminal ATC 79.
 * Inicializa los componentes de la arquitectura MVC y comienza el juego.
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 *   {@code
 *   // Automáticamente ejecutado al iniciar el programa
 *   Main.main();
 *   }
 * </pre>
 */
public class Main {
    /**
     * Inicializa la aplicación, crea los componentes del aeropuerto y del juego,
     * y muestra la vista principal de la ventana.
     */
    static void main() {
        Airport alicante = createAlicanteAirport();

        Game game = new Game(alicante);

        GameController controller = new GameController(game);

        setupInitialFlights(game);

        SwingUtilities.invokeLater(() -> {
            WindowView view = new WindowView(controller);
            controller.setView(view);
            view.show();
            controller.start();
        });
    }

    /**
     * Crea y configura el aeropuerto de Alicante con sus pistas correspondientes.
     * 
     * @return el objeto {@link Airport} completamente configurado para Alicante
     */
    private static Airport createAlicanteAirport() {
        Airport alicante = new Airport("LEAL", "Alicante-Elche", 2000);

        Position start10 = new Position(0, 0, 0);
        Position end10 = new Position(1.6, 0.5, 0);
        alicante.addRunway(new Runway("10", start10, end10, true));

        Position start28 = new Position(1.6, 0.5, 0);
        Position end28 = new Position(0, 0, 0);
        alicante.addRunway(new Runway("28", start28, end28, false));

        return alicante;
    }

    /**
     * Prepara los vuelos iniciales en el juego para pruebas o el escenario de inicio.
     * 
     * @param game la instancia de {@link Game} donde se añadirán los vuelos
     */
    private static void setupInitialFlights(Game game) {
        AircraftModel b737 = new AircraftModel("B737", "Boeing 737", AircraftCategory.MEDIUM, 250, 120, 2500.0, 26000, 15, 3, 3);

        game.addFlight(new Flight("IBE1234", b737, new Position(-5.0, -1.9, 1000), 72, 140));
        game.addFlight(new Flight("VLG4455", b737, new Position(4.0, 1.3, 2000), 252, 220));
    }
}