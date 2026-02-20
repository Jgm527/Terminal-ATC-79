package piano.atc79.controller;

import piano.atc79.model.CommandExceptions;
import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Game;
import piano.atc79.view.WindowView;

import java.util.List;

import javax.swing.*;


public class GameController {
    private Game game;
    private Timer gameTimer;
    private WindowView view;

    public GameController(Game game) {
        this.game = game;
    }

    public void setView(WindowView view) {
        this.view = view;
    }

    public void start() {
        gameTimer = new Timer(1000, e -> {
            if (!game.isGameOver()) {
                game.update();

                for (String event : game.pullEvents()) {
                    view.logTypedMessage(event, "INFO");
                }

                view.updateView(game.getFlights());
            } else {
                view.logTypedMessage("OPERACIONES SUSPENDIDAS - GAME OVER", "SYSTEM");
                gameTimer.stop();
            }
        });
        gameTimer.start();
    }

    public void executeCommand(String command) {
        try {
            String feedback = game.executeCommand(command);
            view.logTypedMessage(feedback, "SUCCESS");
        } catch (CommandExceptions e) {
            view.logTypedMessage(e.getMessage(), "ERROR");
        }
    }

    public List<Flight> getFlights() { return game.getFlights(); }
    public Airport getAirport() { return game.getAirport(); }
}