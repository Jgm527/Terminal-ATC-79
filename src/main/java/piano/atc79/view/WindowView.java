package piano.atc79.view;

import piano.atc79.controller.GameController;

import javax.swing.*;
import java.awt.*;

public class WindowView {
    private JFrame window;
    private GameController gameController;

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
        sidePanel.add(dataLabel);

        JTextArea infoArea = new JTextArea();
        infoArea.setBackground(Color.BLACK);
        infoArea.setCaretColor(Color.BLACK);
        infoArea.setEditable(false);
        sidePanel.add(infoArea);

        JLabel errorLabel = new JLabel("LOGS DEL SISTEMA");
        errorLabel.setBackground(Color.BLACK);
        sidePanel.add(errorLabel);

        JTextArea errorLog = new JTextArea();
        errorLog.setBackground(Color.BLACK);
        errorLog.setCaretColor(Color.BLACK);
        errorLog.setEditable(false);
        JScrollPane scrollErrors = new JScrollPane(errorLog);
        scrollErrors.setPreferredSize(new Dimension(window.getWidth() / 3, window.getHeight() / 3 ));
        sidePanel.add(scrollErrors);

        window.add(sidePanel, BorderLayout.EAST);
    }

    public void show() {
        window.setVisible(true);
    }
}
