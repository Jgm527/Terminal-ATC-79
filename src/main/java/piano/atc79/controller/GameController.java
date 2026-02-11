package piano.atc79.controller;

import piano.atc79.logic.ConflictDetector;
import piano.atc79.logic.FuelManager;
import piano.atc79.logic.PhysicsEngine;
import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Score;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private Airport airport;
    private List<Flight> flights;
    private Score score;
    private PhysicsEngine physicsEngine;
    private ConflictDetector conflictDetector;
    private FuelManager fuelManager;
    private boolean gameOver;

    public GameController(Airport airport) {
        this.airport = airport;
        flights = new ArrayList<Flight>();
        score = new Score();
        physicsEngine = new PhysicsEngine();
        conflictDetector = new ConflictDetector();
        fuelManager = new FuelManager();
        gameOver = false;
    }

    public void update() {
        if (gameOver) return;

        for (Flight f : flights) {
            physicsEngine.updatePosition(f);
            fuelManager.updateFuel(f);

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
