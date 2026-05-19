package piano.atc79.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pantalla de inicio del juego Terminal ATC-79.
 * Muestra el titulo, una rejilla de tarjetas de aeropuertos para seleccionar,
 * y un boton para comenzar la partida.
 */
public class TitleScreen extends JPanel {

    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color ACCENT_COLOR = new Color(0, 255, 128);
    private static final Color TITLE_COLOR = new Color(0, 255, 100);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color CARD_BACKGROUND = new Color(30, 30, 30);
    private static final Color CARD_HOVER = new Color(50, 50, 50);
    private static final Color CARD_SELECTED = new Color(0, 200, 255);
    private static final Color CARD_BORDER = new Color(80, 80, 80);

    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 36);
    private static final Font SUBTITLE_FONT = new Font("Monospaced", Font.PLAIN, 16);
    private static final Font CODE_FONT = new Font("Monospaced", Font.BOLD, 28);
    private static final Font NAME_FONT = new Font("Monospaced", Font.BOLD, 14);
    private static final Font INFO_FONT = new Font("Monospaced", Font.PLAIN, 12);

    private final AirportSelectionListener listener;
    private JPanel cardsPanel;
    private JButton startButton;
    private AirportCard selectedCard;

    /**
     * Interfaz de callback que se invoca cuando el jugador confirma la seleccion de un aeropuerto.
     */
    public interface AirportSelectionListener {
        /**
         * Notifica que se ha seleccionado un aeropuerto y se desea comenzar la partida.
         *
         * @param airportCode el codigo ICAO del aeropuerto elegido (ej. "LEAL")
         */
        void onAirportSelected(String airportCode);
    }

    /**
     * Construye la pantalla de inicio con el listener de seleccion.
     *
     * @param listener el callback que recibira el codigo del aeropuerto seleccionado
     */
    public TitleScreen(AirportSelectionListener listener) {
        this.listener = listener;
        initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(BACKGROUND_COLOR);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.add(createHeaderPanel(), BorderLayout.NORTH);
        this.add(createCardsPanel(), BorderLayout.CENTER);
        this.add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 5, 5));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("TERMINAL ATC-79", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TITLE_COLOR);
        panel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("SIMULADOR DE CONTROL AEREO  ·  1979", SwingConstants.CENTER);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(TEXT_COLOR);
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createCardsPanel() {
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(4, 2, 15, 15));
        cardsPanel.setBackground(BACKGROUND_COLOR);

        for (AirportInfo info : AirportInfo.values()) {
            AirportCard card = new AirportCard(info);
            cardsPanel.add(card);
        }

        // Celda vacia para completar la rejilla 2x4 (7 aeropuertos + 1 hueco)
        JPanel emptyCell = new JPanel();
        emptyCell.setBackground(BACKGROUND_COLOR);
        cardsPanel.add(emptyCell);

        return cardsPanel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);

        startButton = new JButton("COMENZAR");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 18));
        startButton.setForeground(Color.BLACK);
        startButton.setBackground(ACCENT_COLOR);
        startButton.setFocusPainted(false);
        startButton.setEnabled(false);
        startButton.setPreferredSize(new Dimension(200, 45));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        startButton.addActionListener(e -> {
            if (selectedCard != null) {
                listener.onAirportSelected(selectedCard.getInfo().code);
            }
        });

        panel.add(startButton);
        return panel;
    }

    /**
     * Datos estaticos de los 7 aeropuertos disponibles.
     */
    private enum AirportInfo {
        LEAL("LEAL", "Alicante-Elche", "MUY BAJA", 1, "CLEAR / HAZE"),
        LEBL("LEBL", "Barcelona-El Prat", "MEDIA", 2, "RAIN"),
        KLAX("KLAX", "Los Angeles Intl", "ALTA", 3, "CLEAR / SANDSTORM"),
        EGLL("EGLL", "London Heathrow", "MUY ALTA", 3, "STORM"),
        GCXO("GCXO", "Tenerife Norte", "EXTREMA", 1, "FOG"),
        BIKF("BIKF", "Keflavik", "EXTREMA", 2, "SNOW / CROSSWIND"),
        KJFK("KJFK", "New York JFK", "MAXIMA", 4, "BLIZZARD");

        final String code;
        final String name;
        final String difficulty;
        final int runways;
        final String weather;

        AirportInfo(String code, String name, String difficulty, int runways, String weather) {
            this.code = code;
            this.name = name;
            this.difficulty = difficulty;
            this.runways = runways;
            this.weather = weather;
        }
    }

    /**
     * Tarjeta visual interactiva que representa un aeropuerto en la pantalla de inicio.
     */
    private class AirportCard extends JPanel {

        private final AirportInfo info;
        private boolean hovered;
        private boolean selected;

        AirportCard(AirportInfo info) {
            this.info = info;
            setLayout(new GridLayout(5, 1, 2, 2));
            setBackground(CARD_BACKGROUND);
            setBorder(BorderFactory.createLineBorder(CARD_BORDER, 2));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Codigo ICAO
            JLabel codeLabel = new JLabel(info.code, SwingConstants.CENTER);
            codeLabel.setFont(CODE_FONT);
            codeLabel.setForeground(ACCENT_COLOR);
            add(codeLabel);

            // Nombre
            JLabel nameLabel = new JLabel(info.name, SwingConstants.CENTER);
            nameLabel.setFont(NAME_FONT);
            nameLabel.setForeground(TEXT_COLOR);
            add(nameLabel);

            // Dificultad con color
            JLabel diffLabel = new JLabel(info.difficulty, SwingConstants.CENTER);
            diffLabel.setFont(INFO_FONT);
            diffLabel.setForeground(getDifficultyColor(info.difficulty));
            add(diffLabel);

            // Pistas
            JLabel runwayLabel = new JLabel(info.runways + (info.runways == 1 ? " PISTA" : " PISTAS"), SwingConstants.CENTER);
            runwayLabel.setFont(INFO_FONT);
            runwayLabel.setForeground(TEXT_COLOR);
            add(runwayLabel);

            // Clima
            JLabel weatherLabel = new JLabel(info.weather, SwingConstants.CENTER);
            weatherLabel.setFont(new Font("Monospaced", Font.ITALIC, 11));
            weatherLabel.setForeground(new Color(180, 180, 180));
            add(weatherLabel);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    updateAppearance();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    updateAppearance();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectCard();
                }
            });
        }

        AirportInfo getInfo() {
            return info;
        }

        void selectCard() {
            // Deseleccionar la tarjeta anterior
            if (selectedCard != null && selectedCard != this) {
                selectedCard.selected = false;
                selectedCard.updateAppearance();
            }

            selected = true;
            selectedCard = this;
            updateAppearance();
            startButton.setEnabled(true);
        }

        void updateAppearance() {
            if (selected) {
                setBackground(CARD_HOVER);
                setBorder(BorderFactory.createLineBorder(CARD_SELECTED, 3));
            } else if (hovered) {
                setBackground(CARD_HOVER);
                setBorder(BorderFactory.createLineBorder(CARD_BORDER, 2));
            } else {
                setBackground(CARD_BACKGROUND);
                setBorder(BorderFactory.createLineBorder(CARD_BORDER, 2));
            }
            repaint();
        }
    }

    /**
     * Devuelve el color asociado a cada nivel de dificultad.
     *
     * @param difficulty la etiqueta de dificultad del aeropuerto
     * @return el {@link Color} correspondiente
     */
    private Color getDifficultyColor(String difficulty) {
        return switch (difficulty) {
            case "MUY BAJA" -> Color.GREEN;
            case "MEDIA" -> Color.YELLOW;
            case "ALTA" -> Color.ORANGE;
            case "MUY ALTA" -> new Color(255, 100, 0);
            case "EXTREMA" -> Color.RED;
            case "MAXIMA" -> new Color(180, 0, 0);
            default -> Color.WHITE;
        };
    }
}
