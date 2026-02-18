package piano.atc79.controller;

import piano.atc79.logic.CommandExceptions;
import piano.atc79.logic.CommandParser;
import piano.atc79.logic.ConflictDetector;
import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Score;
import piano.atc79.view.WindowView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private Airport airport;
    private List<Flight> flights;
    private Score score;
    private ConflictDetector conflictDetector;
    private CommandParser commandParser;
    private boolean gameOver;
    private Timer gameTimer;
    private WindowView view;

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

    public GameController(Airport airport) {
        this.airport = airport;
        flights = new ArrayList<Flight>();
        score = new Score();
        conflictDetector = new ConflictDetector();
        commandParser = new CommandParser();
        gameOver = false;
    }

    public void setView (WindowView view) {
        this.view = view;
    }

    public void addFlight(Flight f) {
        if (f != null) {
            flights.add(f);
        }
    }

    public void start() {
        gameTimer = new Timer(1000, e -> {
            if (!gameOver) {
                update();
            } else {
                gameTimer.stop();
            }
        });
        gameTimer.start();
    }

    public void update() {
        for (int i = flights.size() - 1; i >= 0; i--) {
            Flight f = flights.get(i);
            f.updatePosition();
            f.updateFuel();

            if (f.getAssignedRunway() != null) {
                if (f.getAssignedRunway().isAligned(f) && f.isReadyToLand()) {
                    System.out.println("¡CRÍTICO: " + f.getCallsign() + " ha aterrizado con éxito!");
                    score.addLanding(500);
                    flights.remove(i);
                    continue;
                }
            }

            if (view != null) {
                view.updateFlightInfo(flights);
            }

            if (f.getFuel() <= 0) {
                gameOver = true;
            }
        }

        for (int i = 0; i < flights.size(); i++) {
            for (int j = i + 1; j < flights.size(); j++) {
                Flight f1 = flights.get(i);
                Flight f2 = flights.get(j);

                if (conflictDetector.areInConflict(f1, f2)) {
                    gameOver = true;
                }
            }
        }
    }

    public void executeCommand(String command) {
        try {
            commandParser.parse(command, this.flights, this.airport);
        } catch (CommandExceptions e) {
            if (view != null) {
                view.logMessage(e.getMessage(), Color.RED);
            }
        }
    }
}
