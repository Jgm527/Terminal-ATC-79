package piano.atc79.view;

import piano.atc79.persistence.DAO;
import piano.atc79.persistence.LeaderboardEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialogo modal que muestra la clasificacion de un aeropuerto.
 *
 * <p>Incluye una tabla con posicion, alias, puntuacion, aterrizajes,
 * racha maxima, duracion y fecha de cada partida. Permite cambiar de
 * aeropuerto sin cerrar el dialogo.</p>
 */
public class LeaderboardPanel extends JDialog {

    private static final Color BG_COLOR = Color.BLACK;
    private static final Color ACCENT = new Color(0, 255, 100);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color DIM_TEXT = new Color(180, 180, 180);
    private static final Color HEADER_BG = new Color(12, 12, 12);
    private static final Color ROW_EVEN = Color.BLACK;
    private static final Color ROW_ODD = new Color(10, 10, 10);
    private static final Color HIGHLIGHT_BG = new Color(0, 35, 12);

    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 20);
    private static final Font HEADER_FONT = new Font("Monospaced", Font.BOLD, 12);
    private static final Font ROW_FONT = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 14);

    private static final String[] HEADERS = {
            "#", "JUGADOR", "PUNTOS", "ATERR.", "RACHA", "DURACION", "FECHA"
    };
    private static final int[] COL_ALIGNMENTS = {
            SwingConstants.RIGHT,  // #
            SwingConstants.LEFT,   // JUGADOR
            SwingConstants.RIGHT,  // PUNTOS
            SwingConstants.RIGHT,  // ATTER.
            SwingConstants.RIGHT,  // RACHA
            SwingConstants.RIGHT,  // DURACION
            SwingConstants.LEFT    // FECHA
    };
    private static final double[] COL_WEIGHTS = {
            0.06, 0.22, 0.14, 0.10, 0.10, 0.12, 0.26
    };

    private static final String[] AIRPORT_CODES = {
            "LEAL", "LEBL", "KLAX", "EGLL", "GCXO", "BIKF", "KJFK"
    };

    private final DAO dao;
    private final String playerAlias;
    private String currentAirport;

    private JLabel titleLabel;
    private JPanel tableContainer;

    /**
     * Construye y muestra el dialogo de clasificacion.
     *
     * @param owner         ventana padre
     * @param dao           acceso a base de datos
     * @param airportCode   codigo ICAO del aeropuerto inicial
     * @param playerAlias   alias del jugador actual (para resaltar su fila)
     */
    public static void showDialog(Frame owner, DAO dao,
                                  String airportCode, String playerAlias) {
        LeaderboardPanel dialog = new LeaderboardPanel(
                owner, dao, airportCode, playerAlias
        );
        dialog.setVisible(true);
    }

    private LeaderboardPanel(Frame owner, DAO dao,
                             String airportCode, String playerAlias) {
        super(owner, "CLASIFICACION", true);
        this.dao = dao;
        this.playerAlias = playerAlias;
        this.currentAirport = airportCode;

        setSize(680, 480);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(createScrollableTable(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        loadData();
    }

    // ---------------------------------------------------------------
    //  Construccion de componentes
    // ---------------------------------------------------------------

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        titleLabel = new JLabel("  CLASIFICACION", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(ACCENT);
        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createScrollableTable() {
        tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(BG_COLOR);

        JScrollPane scroll = new JScrollPane(tableContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG_COLOR);
        scroll.getViewport().setBackground(BG_COLOR);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton changeBtn = new JButton("CAMBIAR AEROPUERTO");
        styleButton(changeBtn, new Color(100, 150, 255));
        changeBtn.addActionListener(e -> showAirportSelector());

        JButton closeBtn = new JButton("CERRAR");
        styleButton(closeBtn, ACCENT);
        closeBtn.addActionListener(e -> dispose());

        panel.add(changeBtn);
        panel.add(closeBtn);
        return panel;
    }

    private static void styleButton(JButton btn, Color bg) {
        btn.setFont(BUTTON_FONT);
        btn.setForeground(Color.BLACK);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(230, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ---------------------------------------------------------------
    //  Carga de datos y pintado de filas
    // ---------------------------------------------------------------

    private void loadData() {
        titleLabel.setText("  CLASIFICACION  —  " + currentAirport);
        tableContainer.removeAll();

        List<LeaderboardEntry> entries = dao.getLeaderboard(currentAirport, 10);

        if (entries.isEmpty()) {
            JLabel emptyLabel = new JLabel(
                    "No hay partidas registradas para " + currentAirport,
                    SwingConstants.CENTER
            );
            emptyLabel.setFont(ROW_FONT);
            emptyLabel.setForeground(DIM_TEXT);
            emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
            tableContainer.add(Box.createVerticalStrut(60));
            tableContainer.add(emptyLabel);
        } else {
            tableContainer.add(createHeaderRow());
            for (int i = 0; i < entries.size(); i++) {
                tableContainer.add(createDataRow(i + 1, entries.get(i)));
            }
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    /**
     * Fila de cabecera con los nombres de columna.
     */
    private JPanel createHeaderRow() {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(HEADER_BG);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 2, 0, 2);

        for (int c = 0; c < HEADERS.length; c++) {
            gbc.gridx = c;
            gbc.weightx = COL_WEIGHTS[c];
            JLabel label = new JLabel(HEADERS[c], COL_ALIGNMENTS[c]);
            label.setFont(HEADER_FONT);
            label.setForeground(ACCENT);
            row.add(label, gbc);
        }
        return row;
    }

    /**
     * Fila de datos con las estadisticas de una partida.
     * Resalta la fila si pertenece al jugador actual.
     */
    private JPanel createDataRow(int position, LeaderboardEntry entry) {
        boolean isPlayer = entry.getAlias().equals(playerAlias);
        Color bg = isPlayer ? HIGHLIGHT_BG : (position % 2 == 0 ? ROW_EVEN : ROW_ODD);
        Color fg = isPlayer ? ACCENT : TEXT_COLOR;

        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(bg);
        row.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 2, 0, 2);

        String[] values = {
                String.valueOf(position),
                entry.getAlias(),
                String.format("%,d", entry.getScore()),
                String.valueOf(entry.getLandings()),
                entry.getStreakMax() > 0 ? String.valueOf(entry.getStreakMax()) : "—",
                formatDuration(entry.getDurationSeconds()),
                entry.getCompletedAt().isEmpty() ? "—" : formatDate(entry.getCompletedAt())
        };

        for (int c = 0; c < values.length; c++) {
            gbc.gridx = c;
            gbc.weightx = COL_WEIGHTS[c];
            JLabel label = new JLabel(values[c], COL_ALIGNMENTS[c]);
            label.setFont(ROW_FONT);
            label.setForeground(c == 0 && position <= 3 ? ACCENT : fg);
            row.add(label, gbc);
        }
        return row;
    }

    // ---------------------------------------------------------------
    //  Selector de aeropuerto
    // ---------------------------------------------------------------

    private void showAirportSelector() {
        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona un aeropuerto:",
                "CAMBIAR AEROPUERTO",
                JOptionPane.PLAIN_MESSAGE,
                null,
                AIRPORT_CODES,
                currentAirport
        );

        if (selected != null && !selected.equals(currentAirport)) {
            currentAirport = selected;
            loadData();
        }
    }

    // ---------------------------------------------------------------
    //  Utilidades de format
    // ---------------------------------------------------------------

    private static String formatDuration(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    /**
     * Acorta la fecha ISO a DD/MM para la tabla.
     * Entrada tipica: "2026-06-01 12:30:00.0" o similar.
     */
    private static String formatDate(String iso) {
        if (iso == null || iso.isEmpty()) return "—";
        // Tomar solo YYYY-MM-DD
        String datePart = iso.contains(" ") ? iso.substring(0, 10) : iso;
        // Reordenar a DD/MM
        String[] parts = datePart.split("-");
        if (parts.length == 3) {
            return parts[2] + "/" + parts[1];
        }
        return datePart;
    }
}
