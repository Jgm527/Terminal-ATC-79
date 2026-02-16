package piano.atc79.controller;

import piano.atc79.logic.ConflictDetector;
import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Score;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private Airport airport;
    private List<Flight> flights;
    private Score score;
    private ConflictDetector conflictDetector;
    private boolean gameOver;

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
        gameOver = false;
    }

    public void addFlight(Flight f) {
        if (f != null) {
            flights.add(f);
        }
    }

    public void update() {
        if (gameOver) return;

        for (Flight f : flights) {
            f.updatePosition();
            f.updateFuel();

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
}
