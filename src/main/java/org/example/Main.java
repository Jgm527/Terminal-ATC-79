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

        // 3. Creamos un Modelo de Avión (Boeing 737)
        AircraftModel b737 = new AircraftModel("B737", "Boeing 737-800",
                AircraftCategory.MEDIUM, 250, 2500.0, 26000, 15, 3, 3);

        // 4. Creamos un Vuelo real
        Position avionPos = new Position(-5.0, -2.0, 2000);
        Flight myFlight = new Flight("IBE1234", b737, avionPos, 72, 140, 400);

        // 5. Creamos el GameController y añadimos el vuelo
        GameController controller = new GameController(alicante);
        controller.addFlight(myFlight);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WindowView radar = new WindowView(controller);
                radar.show();
            }
        });
    }
}