package piano.atc79.view;

import piano.atc79.controller.GameController;
import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Runway;

import java.util.List;

import javax.swing.*;
import java.awt.*;

public class WindowView {
    private JFrame window;
    private GameController gameController;
    private JTextArea infoArea;
    private JTextArea errorLog;
    private JTextArea headerArea;

    public WindowView(GameController gameController) {
        this.gameController = gameController;
        window = new JFrame();
        window.setTitle("Terminal ATC 79");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(1200, 800);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        // Añadir radar a la ventana
        JPanel radar = new RadarView(gameController);
        window.add(radar, BorderLayout.CENTER);

        // Añadir campo para comandos
        JTextField commandInput = new JTextField();
        commandInput.setBackground(Color.BLACK);
        commandInput.setForeground(Color.GREEN);
        commandInput.setCaretColor(Color.GREEN);

        commandInput.addActionListener(e -> {
            String command = commandInput.getText();
            gameController.executeCommand(command);
            commandInput.setText("");
        });
        window.add(commandInput, BorderLayout.SOUTH);

        // Añadir panel lateral
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(window.getWidth() / 3, window.getHeight()));
        sidePanel.setBackground(Color.BLACK);

        JLabel dataLabel = new JLabel("INFORMACION DE VUELOS");
        dataLabel.setBackground(Color.BLACK);
        dataLabel.setForeground(Color.WHITE);
        sidePanel.add(dataLabel);

        // Añadir Area de info de aviones
        infoArea = new JTextArea();
        infoArea.setBackground(Color.BLACK);
        infoArea.setCaretColor(Color.BLACK);
        infoArea.setEditable(false);
        sidePanel.add(infoArea);
        JScrollPane scrollInfo = new JScrollPane(infoArea);
        scrollInfo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollInfo.setPreferredSize(new Dimension(window.getWidth() / 3, window.getHeight() / 3 ));
        sidePanel.add(scrollInfo);

        JLabel errorLabel = new JLabel("LOGS DEL SISTEMA");
        errorLabel.setBackground(Color.BLACK);
        errorLabel.setForeground(Color.WHITE);
        sidePanel.add(errorLabel);

        // Añadir Area de avisos y eventos
        errorLog = new JTextArea();
        errorLog.setBackground(Color.BLACK);
        errorLog.setCaretColor(Color.BLACK);
        errorLog.setEditable(false);
        JScrollPane scrollErrors = new JScrollPane(errorLog);
        scrollErrors.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollErrors.setPreferredSize(new Dimension(window.getWidth() / 3, window.getHeight() / 3 ));
        sidePanel.add(scrollErrors);

        window.add(sidePanel, BorderLayout.EAST);

        // Añadir Area de cabecera
        headerArea = new JTextArea();
        headerArea.setBackground(Color.BLACK);
        headerArea.setCaretColor(Color.BLACK);
        headerArea.setForeground(Color.WHITE);
        headerArea.setEditable(false);
        headerArea.setText(getHeaderStringSB());

        window.add(headerArea, BorderLayout.NORTH);
    }

    private String getHeaderStringSB() {
        Airport a = gameController.getAirport();
        StringBuilder stringSB = new StringBuilder();
        stringSB.append("Aeropuerto: " + a.getName() + " - " + a.getId() + " | " + a.getRunways().size() + " pistas → ");
        for (Runway r : a.getRunways()) {
            stringSB.append("| Pista " + r.getId() + ": " + r.getHeading() + "º  -  " + Math.round(r.getLength() * 100) / 100.0 + " millas \t");
        }

        return stringSB.toString();
    }

    public void logMessage(String message, Color color) {
        errorLog.setForeground(color);
        errorLog.append(message + "\n");

        errorLog.setCaretPosition(errorLog.getDocument().getLength());
    }

    public void updateFlightInfo(List<Flight> flights) {
        StringBuilder stringSB = new StringBuilder();
        for (Flight f : flights) {
            String string = String.format("%5s \t%-15s %15s %15s\n ALT %6d   |   SPD %4d   |   HDNG %4d   |   FUEL %.2f\n\n",
                    f.getCallsign(),
                    f.getModel().getName(),
                    f.getModel().getCategory(),
                    f.getStatus(),
                    f.getCurrentPosition().getZ(),
                    f.getSpeed(),
                    f.getHeading(),
                    f.getFuel());

            stringSB.append(string);
        }
        infoArea.setForeground(Color.WHITE);
        infoArea.setText(stringSB.toString());
    }

    public void show() {
        window.setVisible(true);
    }
}
