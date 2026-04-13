package piano.atc79.view;

import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Position;
import piano.atc79.model.Runway;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 * Componente gráfico encargado de dibujar la representación visual en base de radar.
 * Dibuja las pistas del aeropuerto, los vuelos activos y los anillos de rango de separación visual.
 */
public class RadarCanvas extends JPanel {
    private Airport airport;
    private List<Flight> flights;
    private static final int SCALE = 20;

    /**
     * Construye el lienzo del radar interactivo.
     * 
     * @param airport la información del aeropuerto para dibujar sus pistas de aterrizaje
     * @param flights la lista actualizada inicial de vuelos a trazar
     */
    public RadarCanvas(Airport airport, List<Flight> flights) {
        this.airport = airport;
        this.flights = flights;
        this.setBackground(Color.BLACK);
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    /**
     * Sobrescribe el método paintComponent para manejar la lógica de repintado del radar,
     * dibujando en su lugar anillos de rango, modelos de las pistas de aterrizaje y las trazas.
     * 
     * @param g el contexto interactivo de Java 2D {@link Graphics} donde pintar
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Dibujar círculos de rango
        g2d.setColor(new Color(0, 50, 0));
        g2d.drawLine(centerX, 0, centerX, getHeight());
        g2d.drawLine(0, centerY, getWidth(), centerY);
        for (int i = 1; i <= 5; i++) {
            int radio = i * 100;
            g2d.drawOval(centerX - radio, centerY - radio, radio * 2, radio * 2);
        }

        // Dibujar pistas
        for (Runway r : airport.getRunways()) {
            drawRunway(g2d, r, centerX, centerY);
        }

        // Dibujar vuelos
        if (flights != null) {
            for (Flight f : flights) {
                drawFlight(g2d, f);
            }
        }
    }

    private void drawRunway(Graphics2D g2d, Runway r, int centerX, int centerY) {
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(4.0f));
        int x1 = centerX + (int) (r.getStartPoint().getX() * SCALE);
        int y1 = centerY - (int) (r.getStartPoint().getY() * SCALE);
        int x2 = centerX + (int) (r.getEndPoint().getX() * SCALE);
        int y2 = centerY - (int) (r.getEndPoint().getY() * SCALE);
        g2d.drawLine(x1, y1, x2, y2);

        if (r.hasILS()) {
            g2d.setColor(new Color(112, 41, 99));
            g2d.setStroke(new BasicStroke(1.0f));

            int heading = r.getHeading();
            double ilsLength = 7.5;  // Longitud del ILS

            // Linea central y cono
            int[] angles = {heading, heading + 7, heading - 7};

            for (int i = 0; i < angles.length; i++) {
                if (i == 0) { // linea discontinua para la central
                    g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                } else {
                    g2d.setStroke(new BasicStroke(1.0f));
                }

                double rad = Math.toRadians(angles[i]);

                double ilsEndX = r.getStartPoint().getX() - Math.sin(rad) * ilsLength;
                double ilsEndY = r.getStartPoint().getY() - Math.cos(rad) * ilsLength;

                // Convertir a píxeles de pantalla
                int screenEndX = centerX + (int)(ilsEndX * SCALE);
                int screenEndY = centerY - (int)(ilsEndY * SCALE);

                g2d.drawLine(x1, y1, screenEndX, screenEndY);
            }
        }
        g2d.setColor(Color.WHITE);
        g2d.drawString(r.getId(), x1, y1 + 15);
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

    private Point toScreen(Position pos) {
        int x = (int) (pos.getX() * SCALE) + getWidth() / 2;
        int y = getHeight() / 2 - (int) (pos.getY() * SCALE);
        return new Point(x, y);
    }
}