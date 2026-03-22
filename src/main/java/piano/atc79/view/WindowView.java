package piano.atc79.view;

import piano.atc79.controller.GameController;
import piano.atc79.model.Flight;

import java.util.List;

import javax.swing.*;
import java.awt.*;

public class WindowView {
    private JFrame window;
    private GameController gameController;
    private MainGamePanel radar;

    public WindowView(GameController gameController) {
        this.gameController = gameController;
        window = new JFrame();
        window.setTitle("Terminal ATC 79");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(1200, 800);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        // Añadir radar a la ventana
        radar = new MainGamePanel(gameController.getAirport(), gameController.getFlights());
        window.add(radar, BorderLayout.CENTER);

        radar.getCommandInput().addActionListener(e -> {
            String command = radar.getCommandInput().getText();
            gameController.executeCommand(command);
            radar.getCommandInput().setText("");
        });
    }

    public void updateView(List<Flight> flights) {
        radar.updateFlightInfo(flights);
        radar.updateData(flights);
    }

    public void show() {
        window.setVisible(true);
    }

    public MainGamePanel getRadar() {
        return radar;
    }
}
