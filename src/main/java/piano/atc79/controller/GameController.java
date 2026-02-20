package piano.atc79.controller;

import piano.atc79.model.CommandExceptions;
import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Game;
import piano.atc79.view.WindowView;

import java.util.List;

import javax.swing.*;
import java.awt.*;


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
                view.updateView(game.getFlights());
            } else {
                gameTimer.stop();
            }
        });
        gameTimer.start();
    }

    public void executeCommand(String command) {
        try {
            game.executeCommand(command);
        } catch (CommandExceptions e) {
            view.logMessage(e.getMessage(), Color.RED);
        }
    }

    public List<Flight> getFlights() { return game.getFlights(); }
    public Airport getAirport() { return game.getAirport(); }
}