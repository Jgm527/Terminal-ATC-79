package piano.atc79.view;

import piano.atc79.controller.GameController;

import javax.swing.*;
import java.awt.*;

public class RadarView extends JPanel {
    private GameController gameController;

    public RadarView(GameController gameController) {
        this.gameController = gameController;
        setBackground(Color.black); // ya puedo dibujar lo q sea :D
    }
}
