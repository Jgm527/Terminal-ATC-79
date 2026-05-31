package piano.atc79.view;

import javax.swing.*;
import java.awt.*;

/**
 * Dialogo modal que muestra las estadisticas de la partida al terminar.
 * <p>
 * Incluye puntuacion, aterrizajes, racha maxima, duracion y causa del fin.
 * Un boton "VOLVER AL MENU" permite al jugador regresar a la pantalla de inicio.</p>
 */
public class GameOverPanel extends JDialog {

    private static final Color BG_COLOR = Color.BLACK;
    private static final Color ACCENT = new Color(0, 255, 100);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color DIM_TEXT = new Color(180, 180, 180);

    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 26);
    private static final Font LABEL_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final Font VALUE_FONT = new Font("Monospaced", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 16);

    /**
     * Crea y muestra el dialogo de fin de partida.
     *
     * @param owner       ventana padre
     * @param airportCode codigo ICAO del aeropuerto
     * @param score       puntuacion total
     * @param landings    numero de aterrizajes exitosos
     * @param streakMax   racha maxima de aterrizajes consecutivos
     * @param durationSec duracion en segundos
     * @param cause       causa del fin ("COLLISION" o "FUEL_EXHAUSTION")
     * @param onReturnToMenu callback al pulsar "VOLVER AL MENU"
     */
    public static void showDialog(Frame owner, String airportCode,
                                  int score, int landings, int streakMax,
                                  int durationSec, String cause,
                                  Runnable onReturnToMenu) {
        GameOverPanel dialog = new GameOverPanel(owner, airportCode,
                score, landings, streakMax, durationSec, cause, onReturnToMenu);
        dialog.setVisible(true);
    }

    private GameOverPanel(Frame owner, String airportCode,
                          int score, int landings, int streakMax,
                          int durationSec, String cause,
                          Runnable onReturnToMenu) {
        super(owner, "PARTIDA FINALIZADA", true);
        setSize(450, 400);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));

        // -- Titulo --
        JLabel titleLabel = new JLabel("PARTIDA FINALIZADA", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(ACCENT);
        panel.add(titleLabel, BorderLayout.NORTH);

        // -- Estadisticas --
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Formatear duracion MM:SS
        String durationFormatted = String.format("%d:%02d", durationSec / 60, durationSec % 60);

        // Formatear causa en espanol
        String causeFormatted = switch (cause) {
            case "COLLISION" -> "COLISION";
            case "FUEL_EXHAUSTION" -> "COMBUSTIBLE AGOTADO";
            default -> cause;
        };

        String[][] rows = {
                {"AEROPUERTO", airportCode},
                {"PUNTUACION", String.format("%,d pts", score)},
                {"ATERRIZAJES", String.valueOf(landings)},
                {"RACHA MAX", streakMax > 0 ? streakMax + " consecutivos" : "—"},
                {"DURACION", durationFormatted},
                {"CAUSA", causeFormatted}
        };

        gbc.gridy = 0;
        for (String[] row : rows) {
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            JLabel label = new JLabel(row[0] + ":");
            label.setFont(LABEL_FONT);
            label.setForeground(DIM_TEXT);
            statsPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JLabel value = new JLabel(row[1]);
            value.setFont(VALUE_FONT);
            value.setForeground(TEXT_COLOR);
            statsPanel.add(value, gbc);

            gbc.gridy++;
        }

        panel.add(statsPanel, BorderLayout.CENTER);

        // -- Boton Volver al Menu --
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BG_COLOR);

        JButton backButton = new JButton("VOLVER AL MENU");
        backButton.setFont(BUTTON_FONT);
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(ACCENT);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(260, 45));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose();
            if (onReturnToMenu != null) {
                onReturnToMenu.run();
            }
        });

        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }
}
