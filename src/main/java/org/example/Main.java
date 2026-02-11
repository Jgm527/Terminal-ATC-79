package org.example;

import piano.atc79.model.*;
import piano.atc79.controller.*;
import piano.atc79.logic.*;
import piano.atc79.view.*;

public class Main {
    static void main() {
        System.out.println("=== INICIALIZANDO SIMULADOR ATC-79 ===\n");

        // 1. Creamos el Aeropuerto (Alicante)
        Airport alicante = new Airport("LEAL", "Alicante-Elche", 2000);

        // 2. Creamos las Pistas (Usando Posiciones)
        // Una pista de 3km aprox (unas 1.6 millas náuticas)
        Position start10 = new Position(0, 0, 0);
        Position end10 = new Position(1.6, 0.5, 0);
        Runway r10 = new Runway("10L", start10, end10);

        alicante.addRunway(r10);

        // 3. Creamos un Modelo de Avión (Boeing 737)
        // Consumo 15.5, Tanque 26000
        AircraftModel b737 = new AircraftModel("B737", "Boeing 737-800",
                AircraftCategory.MEDIUM, 250, 15.5, 26000);

        // 4. Creamos un Vuelo real
        Position avionPos = new Position(10.5, 12.0, 15000); // Está a 15,000 pies
        Flight myFlight = new Flight("IBE1234", b737, avionPos, 70, 240, 21000);

        // 5. Mostramos la "Foto" del sistema
        System.out.println("Aeropuerto: " + alicante.getName() + " [" + alicante.getId() + "]");
        System.out.println("MVA (Altitud mínima): " + alicante.getMinimumVectoringAltitude() + " ft");

        System.out.println("\nPistas disponibles:");
        for (Object obj : alicante.getRunways()) { // Si no usaste genéricos <Runway> aún, usa Object
            Runway r = (Runway) obj;
            System.out.println("- Pista " + r.getId() + " | Longitud: " + String.format("%.2f", r.getLength()) + " NM");
        }

        System.out.println("\nAvión en radar:");
        System.out.println("Callsign: " + myFlight.getCallsign());
        System.out.println("Modelo: " + b737.getName() + " (" + b737.getCategory() + ")");
        System.out.println("Posición: " + myFlight.getCurrentPosition());
        System.out.println("Combustible inicial: " + String.format("%.2f", myFlight.getFuel()));
        System.out.println("Estado: " + myFlight.getStatus());

        // 6. Probamos una regla de negocio
        System.out.println("\nVerificación de Seguridad:");
        if (myFlight.getAltitude() < alicante.getMinimumVectoringAltitude()) {
            System.out.println("¡ALERTA! El avión está por debajo de la altitud mínima.");
        } else {
            System.out.println("Altitud de seguridad confirmada.");
        }

        GameController controller = new GameController(alicante);
        controller.addFlight(myFlight);
        ConsoleView view = new ConsoleView();


        myFlight.setTargetHeading(90);
        myFlight.setTargetAltitude(20000);

        // BUCLE DE JUEGO MANUAL (30 segundos)
        for(int i = 0; i < 120; i++) {
            controller.update();
            view.showInformation(controller.getAirport(), controller.getFlights(), controller.getScore());

            if(controller.isGameOver()) {
                view.showGameOver(controller.getScore());
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("El simulador se ha interrumpido.");
            }
        }
    }
}