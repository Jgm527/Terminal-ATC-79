package org.example;

import piano.atc79.model.*;
import piano.atc79.controller.*;
import piano.atc79.view.*;

import javax.swing.*;

public class Main {
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

    private static Airport createAlicanteAirport() {
        Airport alicante = new Airport("LEAL", "Alicante-Elche", 2000);

        Position start10 = new Position(0, 0, 0);
        Position end10 = new Position(1.6, 0.5, 0);
        alicante.addRunway(new Runway("10", start10, end10, true));

        Position start28 = new Position(1.6, 0.5, 0);
        Position end28 = new Position(0, 0, 0);
        alicante.addRunway(new Runway("28", start28, end28, true));

        return alicante;
    }

    private static void setupInitialFlights(Game game) {
        AircraftModel b737 = new AircraftModel("B737", "Boeing 737", AircraftCategory.MEDIUM, 250, 2500.0, 26000, 15, 3, 3);

        game.addFlight(new Flight("IBE1234", b737, new Position(-5.0, -2.0, 400), 72, 140));
        game.addFlight(new Flight("VLG4455", b737, new Position(8.0, 4.0, 4000), 250, 220));
    }
}