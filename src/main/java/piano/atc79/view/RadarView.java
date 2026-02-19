package piano.atc79.view;

import piano.atc79.controller.GameController;
import piano.atc79.model.Flight;
import piano.atc79.model.Position;
import piano.atc79.model.Runway;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class RadarView extends JPanel {
    private GameController gameController;
    private static final int SCALE = 20;
    private static final int CENTER_X = 400;// Offset to center 0,0
    private static final int CENTER_Y = 600;

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

        // Dibujar pistas
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(4.0f));
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
        g2d.setStroke(new BasicStroke(1.0f));


        for (Flight f : gameController.getFlights()) {
            drawFlight(g2d, f);
        }
    }

    private void drawFlight(Graphics2D g2d, Flight f) {
        Point flightPosition = toScreen(f.getCurrentPosition());

        // guardar posicion orignal
        AffineTransform old = g2d.getTransform();

        // rotar hacia heading
        g2d.translate(flightPosition.x, flightPosition.y);
        g2d.rotate(Math.toRadians(f.getHeading()));

        // dibujar avion
        g2d.setColor(Color.CYAN);
        Polygon poly = new Polygon();
        poly.addPoint(0, -8);
        poly.addPoint(4, 4);
        poly.addPoint(-4, 4);
        g2d.fillPolygon(poly);

        // volver a la vertical para el texto
        g2d.setTransform(old);

        g2d.setColor(Color.GREEN);
        g2d.drawString(f.getCallsign(), flightPosition.x + 10, flightPosition.y);
        g2d.drawString("Alt: " + f.getCurrentPosition().getZ(), flightPosition.x + 10, flightPosition.y + 12);
        g2d.drawString("Spd: " + f.getSpeed(), flightPosition.x + 10, flightPosition.y + 24);
    }

    // relacionar coordenadas con la escala y posicion en el radar
    private Point toScreen(Position pos) {
        int x = (int) (pos.getX() * SCALE) + getWidth() / 2;
        int y = getHeight() / 2 - (int) (pos.getY() * SCALE);
        return new Point(x, y);
    }
}
