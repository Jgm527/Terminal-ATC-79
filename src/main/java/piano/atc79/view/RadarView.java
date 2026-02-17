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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Convertimos el pincel básico a Graphics2D para tener más funciones
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar detallitos del radar
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        g2d.setColor(new Color(0, 50, 0));
        g2d.drawLine(centerX, 0, centerX, getHeight());
        g2d.drawLine(0, centerY, getWidth(), centerY);
        for (int i = 1; i <= 3; i++) {
            int radio = i * 100;

            g2d.drawOval(centerX - radio, centerY - radio, radio * 2, radio * 2);
        }
    }
}
