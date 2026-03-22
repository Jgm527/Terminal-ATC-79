package piano.atc79.view;

import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Runway;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainGamePanel extends JPanel {
    private RadarCanvas canvas;
    private JTextArea headerArea;
    private JTextArea infoArea;
    private JTextArea errorLog;
    private JTextField commandInput;
    private Map<String, Color> messageColors;
    private Airport airport;

    public MainGamePanel(Airport airport, List<Flight> flights) {
        this.airport = airport;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);

        // Añadir Area de cabecera
        headerArea = new JTextArea();
        headerArea.setBackground(Color.BLACK);
        headerArea.setForeground(Color.WHITE);
        headerArea.setEditable(false);
        headerArea.setText(getHeaderString());
        this.add(headerArea, BorderLayout.NORTH);

        // 2. Radar Real (CENTRO) - Ahora es un componente separado
        canvas = new RadarCanvas(airport, flights);
        this.add(canvas, BorderLayout.CENTER);

        // Añadir panel lateral
        this.add(createSidePanel(), BorderLayout.EAST);

        // 4. Input de comandos (SUR)
        commandInput = new JTextField();
        commandInput.setBackground(Color.BLACK);
        commandInput.setForeground(Color.GREEN);
        commandInput.setCaretColor(Color.GREEN);
        this.add(commandInput, BorderLayout.SOUTH);

        initColorMap();
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(400, 0));
        sidePanel.setBackground(Color.BLACK);

        infoArea = createCustomTextArea();
        errorLog = createCustomTextArea();

        // Añadir Area de info de aviones
        JLabel dataLabel = new JLabel("INFORMACION DE VUELOS");
        dataLabel.setBackground(Color.BLACK);
        dataLabel.setForeground(Color.WHITE);
        sidePanel.add(dataLabel);
        sidePanel.add(new JScrollPane(infoArea));

        // Añadir Area de avisos y eventos
        JLabel errorLabel = new JLabel("LOGS DEL SISTEMA");
        errorLabel.setBackground(Color.BLACK);
        errorLabel.setForeground(Color.WHITE);
        sidePanel.add(errorLabel);
        sidePanel.add(new JScrollPane(errorLog));

        return sidePanel;
    }

    private JTextArea createCustomTextArea() {
        JTextArea area = new JTextArea();
        area.setBackground(Color.BLACK);
        area.setForeground(Color.WHITE);
        area.setEditable(false);
        return area;
    }

    public void updateData(List<Flight> newFlights) {
        canvas.setFlights(newFlights);
        canvas.repaint(); // Redibuja el radar
        updateFlightInfo(newFlights); // Actualiza la lista de la derecha
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

    private void initColorMap() {
        messageColors = new HashMap<>();
        messageColors.put("ERROR", Color.RED);
        messageColors.put("SUCCESS", Color.GREEN);
        messageColors.put("INFO", Color.CYAN);
    }

    public void logTypedMessage(String message, String type) {
        Color color = messageColors.getOrDefault(type.toUpperCase(), Color.WHITE);
        errorLog.setForeground(color);
        errorLog.append(message + "\n");
    }

    private String getHeaderString() {
        StringBuilder stringSB = new StringBuilder();
        stringSB.append("Aeropuerto: " + airport.getName() + " - " + airport.getId() + " | " + airport.getRunways().size() + " pistas → ");
        for (Runway r : airport.getRunways()) {
            stringSB.append("| Pista " + r.getId() + ": " + r.getHeading() + "º  -  " + Math.round(r.getLength() * 100) / 100.0 + " millas \t");
        }

        return stringSB.toString();
    }

    public JTextField getCommandInput() {
        return commandInput;
    }
}