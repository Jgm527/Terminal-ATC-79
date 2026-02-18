package piano.atc79.view;

import piano.atc79.controller.GameController;
import piano.atc79.model.Flight;
import piano.atc79.model.Runway;

import javax.swing.*;
import java.awt.*;

public class RadarView extends JPanel {
    private GameController gameController;
    private static final int SCALE = 20;

    public RadarView(GameController gameController) {
        this.gameController = gameController;
        setBackground(Color.black); // ya puedo dibujar lo q sea :D

        Timer repaintTimer = new Timer(100, e -> repaint());
        repaintTimer.start();
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
        for (int i = 1; i <= 5; i++) {
            int radio = i * 100;

            g2d.drawOval(centerX - radio, centerY - radio, radio * 2, radio * 2);
        }

        g2d.setColor(Color.WHITE);
        for (Runway r : gameController.getAirport().getRunways()) {
            int x1 = centerX + (int)(r.getStartPoint().getX() * SCALE);
            int y1 = centerY - (int)(r.getStartPoint().getY() * SCALE);
            int x2 = centerX + (int)(r.getEndPoint().getX() * SCALE);
            int y2 = centerY - (int)(r.getEndPoint().getY() * SCALE);
            g2d.drawLine(x1, y1, x2, y2);

            int x = centerX + (int)(r.getStartPoint().getX() * SCALE);
            int y = centerY - (int)(r.getStartPoint().getY() * SCALE);
            g2d.drawString(r.getId(), x, y + 15);
        }


        for (Flight f : gameController.getFlights()) {
            int x = centerX + (int)(f.getCurrentPosition().getX() * SCALE);
            int y = centerY - (int)(f.getCurrentPosition().getY() * SCALE);

            g2d.setColor(Color.CYAN);
            g2d.fillRect(x - 3, y - 3, 6, 6); // por ahora un rectangulillo luego lo poongo bonico

            g2d.setColor(Color.WHITE);
            g2d.drawString(f.getCallsign(), x + 5, y - 5);
            g2d.drawString("Altitud:" + f.getAltitude(), x + 5, y + 8);
        }
    }
}
