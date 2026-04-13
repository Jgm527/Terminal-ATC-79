package piano.atc79.view;

import piano.atc79.controller.GameController;
import piano.atc79.model.Flight;

import java.util.List;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal principal de la aplicación. Maneja la configuración del JFrame del menú de la UI principal y en el interior incluye el diseño.
 */
public class WindowView {
    private JFrame window;
    private GameController gameController;
    private MainGamePanel radar;

    /**
     * Construye y crea la instancia base interactiva principal de la ventana inicial inicial para jugar a la aplicación.
     * 
     * @param gameController el objeto base interconector con el estado que manejará y tomará las acciones de interfaz.
     */
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

    /**
     * Refresca y ajusta los valores referenciados para reflejar y aplicar cambios.
     * 
     * @param flights el listado en matriz modificado actual de los vuelos representados en directo.
     */
    public void updateView(List<Flight> flights) {
        radar.updateFlightInfo(flights);
        radar.updateData(flights);
    }

    /**
     * Abre y muestra por pantalla de forma representativa principal la gráfica.
     */
    public void show() {
        window.setVisible(true);
    }

    public MainGamePanel getRadar() {
        return radar;
    }
}
