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

        // Conectar boton GUARDAR con la confirmacion y vuelta al menu
        radar.setOnSaveCallback(() -> {
            String name;

            // 1. Obtener nombre (partida cargada → reutilizar; nueva → preguntar)
            if (gameController.hasExistingSave()) {
                name = gameController.getCurrentSaveName();
            } else {
                name = TerminalDialog.showSaveDialog(
                        window,
                        "NOMBRAR PARTIDA",
                        "Introduce un nombre para esta partida:"
                );
                if (name == null || name.trim().isEmpty()) {
                    return;
                }
                name = name.trim();
            }

            // 2. Guardar la partida
            gameController.saveGame(name);

            // 3. Mostrar mensaje de exito
            TerminalDialog.showInfoDialog(
                    window,
                    "PARTIDA GUARDADA",
                    "Partida guardada correctamente."
            );

            // 4. Volver al menu principal
            close();
            gameController.quitToMenu();
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

    /**
     * Cierra la ventana del juego.
     */
    public void close() {
        window.dispose();
    }

    public JFrame getWindow() {
        return window;
    }

    public MainGamePanel getRadar() {
        return radar;
    }
}
