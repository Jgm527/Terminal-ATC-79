package org.example;

import piano.atc79.model.*;
import piano.atc79.controller.*;
import piano.atc79.view.*;

import javax.swing.*;

public class Main {
    static void main() {
        System.out.println("=== INICIALIZANDO SIMULADOR ATC-79 ===\n");

        // 1. Creamos el Aeropuerto (Alicante)
        Airport alicante = new Airport("LEAL", "Alicante-Elche", 2000);

        // 2. Creamos las Pistas (Usando Posiciones)
        Position start10 = new Position(0, 0, 0);
        Position end10 = new Position(1.6, 0.5, 0);
        Runway r10 = new Runway("10L", start10, end10);
        alicante.addRunway(r10);

        Position start35 = new Position(1.6, 0.5, 0);
        Position end35 = new Position(0, 0, 0);
        Runway r35 = new Runway("35", start35, end35);
        alicante.addRunway(r35);

        // 3. Creamos un Modelo de Avión (Boeing 737)
        AircraftModel b737 = new AircraftModel("B737", "Boeing 737-800",
                AircraftCategory.MEDIUM, 250, 2500.0, 26000, 15, 3, 3);

        AircraftModel a320 = new AircraftModel("A320", "Airbus A320-400",
                AircraftCategory.MEDIUM, 260, 2500.0, 28000, 16, 3, 3);

        // 4. Creamos un Vuelo real
        Position flightPos = new Position(-2.0, -0.6, 400);
        Flight myFlight = new Flight("IBE1234", b737, flightPos, 72, 140, 400);

        Position flightPos2 = new Position(7.8, 1.45, 2000);
        Flight myFlight2 = new Flight("RAY367", a320, flightPos2, 220, 220, 800);

        // 5. Creamos el GameController y añadimos el vuelo
        GameController controller = new GameController(alicante);
        controller.addFlight(myFlight);
        controller.addFlight(myFlight2);
        controller.start();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WindowView radar = new WindowView(controller);
                controller.setView(radar);
                radar.show();
            }
        });
    }
}