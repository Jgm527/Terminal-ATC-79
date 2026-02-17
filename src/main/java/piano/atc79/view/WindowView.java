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
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        JPanel radar = new RadarView(gameController);
        window.add(radar, BorderLayout.CENTER);
    }

    public void show() {
        window.setVisible(true);
    }
}
