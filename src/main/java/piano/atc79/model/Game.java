package piano.atc79.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String player;
    private Airport airport;
    private List<Flight> flights;
    private Score score;
    private CommandParser commandParser;
    private boolean gameOver;

    public Game(Airport airport) {
        this.airport = airport;
        this.flights = new ArrayList<>();
        this.score = new Score();
        this.commandParser = new CommandParser();
        this.gameOver = false;
    }

    public void update() {
        for (int i = flights.size() - 1; i >= 0; i--) {
            Flight f = flights.get(i);
            f.updatePosition();
            f.updateFuel();

            if (f.getStatus().equals(FlightStatus.LANDING)) {
                if (f.getCurrentPosition().distanceTo(f.getAssignedRunway().getStartPoint()) < 0.3) {
                f.land();
                }
                if (f.getSpeed() <= 0) {
                    flights.remove(i);
                    continue; // Importante para no seguir procesando este avión
                }
            }
            // 2. Si está volando, ver si puede empezar a aterrizar
            else if (f.isReadyToLand()) {
                f.setStatus(FlightStatus.LANDING);
            }
        }
        checkGameOver();
    }

    public void checkGameOver() {
        // Buscar aviones sin combustible
        for (int i = flights.size() - 1; i >= 0; i--) {
            Flight f = flights.get(i);

            // Buscar aviones que han colisionado
            for (int j = i + 1; j < flights.size(); j++) {
                Flight f2 = flights.get(j);

                if (f.areInConflict(f2)) {
                    gameOver = true;
                    return;
                }
            }

            // Buscar aviones sin combustible
            if (f.getFuel() <= 0) {
                gameOver = true;
            }
        }
    }

    public void executeCommand(String command) throws CommandExceptions {
        commandParser.parse(command, this.flights, this.airport);
    }

    public void addFlight(Flight f) {
        if (f != null) {
            flights.add(f);
        }
    }

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
}
