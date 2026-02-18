package piano.atc79.view;

import piano.atc79.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WindowView {
    private JFrame window;
    private GameController gameController;
    private JTextArea infoArea;
    private JTextArea errorLog;

    public WindowView(GameController gameController) {
        this.gameController = gameController;
        window = new JFrame();
        window.setTitle("Terminal ATC 79");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(1200, 800);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        JPanel radar = new RadarView(gameController);
        window.add(radar, BorderLayout.CENTER);

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

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(window.getWidth() / 3, window.getHeight()));
        sidePanel.setBackground(Color.BLACK);

        JLabel dataLabel = new JLabel("INFORMACION DE VUELOS");
        dataLabel.setBackground(Color.BLACK);
        dataLabel.setForeground(Color.WHITE);
        sidePanel.add(dataLabel);

        infoArea = new JTextArea();
        infoArea.setBackground(Color.BLACK);
        infoArea.setCaretColor(Color.BLACK);
        infoArea.setEditable(false);
        sidePanel.add(infoArea);

        JLabel errorLabel = new JLabel("LOGS DEL SISTEMA");
        errorLabel.setBackground(Color.BLACK);
        errorLabel.setForeground(Color.WHITE);
        sidePanel.add(errorLabel);

        errorLog = new JTextArea();
        errorLog.setBackground(Color.BLACK);
        errorLog.setCaretColor(Color.BLACK);
        errorLog.setEditable(false);
        JScrollPane scrollErrors = new JScrollPane(errorLog);
        scrollErrors.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollErrors.setPreferredSize(new Dimension(window.getWidth() / 3, window.getHeight() / 3 ));
        sidePanel.add(scrollErrors);

        window.add(sidePanel, BorderLayout.EAST);
    }

    public void logMessage(String message, Color color) {
        errorLog.setForeground(color);
        errorLog.append(message + "\n");

        errorLog.setCaretPosition(errorLog.getDocument().getLength());
    }

    public void show() {
        window.setVisible(true);
    }
}
