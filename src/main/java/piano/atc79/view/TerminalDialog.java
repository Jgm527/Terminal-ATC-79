package piano.atc79.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Utilidad para mostrar dialogos con la estetica retro de Terminal ATC-79
 * (fondo negro, texto verde, fuente monoespaciada, bordes verdes).
 */
public final class TerminalDialog {

    // Paleta retro
    private static final Color BG = Color.BLACK;
    private static final Color FG = new Color(0, 220, 80);
    private static final Color FG_DIM = new Color(0, 160, 60);
    private static final Color FG_BRIGHT = new Color(0, 255, 120);
    private static final Color BORDER = new Color(0, 180, 70);
    private static final Color FIELD_BG = new Color(10, 20, 10);
    private static final Color FIELD_FG = Color.GREEN;
    private static final Color LIST_SELECTION = new Color(0, 80, 30);

    private static final Font FONT = new Font("Monospaced", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final Font LIST_FONT = new Font("Monospaced", Font.PLAIN, 12);

    private TerminalDialog() {}

    // ---------------------------------------------------------------
    //  Dialogo de texto (para nombrar partida)
    // ---------------------------------------------------------------

    /**
     * Muestra un dialogo estilizado con un campo de texto.
     *
     * @param parent  componente padre para centrar el dialogo
     * @param title   titulo de la ventana
     * @param message mensaje de instruccion
     * @return el texto introducido, o null si se cancelo
     */
    public static String showSaveDialog(Component parent, String title, String message) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModal(true);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mensaje
        JLabel label = new JLabel("> " + message);
        label.setFont(FONT);
        label.setForeground(FG);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);

        // Campo de texto
        JTextField textField = new JTextField(25);
        textField.setBackground(FIELD_BG);
        textField.setForeground(FIELD_FG);
        textField.setCaretColor(FIELD_FG);
        textField.setFont(FONT);
        textField.setBorder(new LineBorder(BORDER));
        textField.requestFocusInWindow();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(textField, gbc);

        // Boton GUARDAR
        JButton okButton = createStyledButton("  GUARDAR  ");
        okButton.addActionListener(e -> dialog.dispose());

        // Boton CANCELAR
        JButton cancelButton = createStyledButton("  CANCELAR  ");
        cancelButton.addActionListener(e -> {
            textField.setText(null);
            dialog.dispose();
        });

        // Atajo Enter para confirmar
        textField.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BG);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 0, 5);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        String result = textField.getText();
        return (result != null && !result.trim().isEmpty()) ? result.trim() : null;
    }

    // ---------------------------------------------------------------
    //  Dialogo de seleccion (para cargar partida)
    // ---------------------------------------------------------------

    /**
     * Muestra un dialogo estilizado con una lista de opciones.
     *
     * @param parent  componente padre para centrar el dialogo
     * @param title   titulo de la ventana
     * @param items   array de opciones a mostrar
     * @return el texto de la opcion seleccionada, o null si se cancelo
     */
    public static String showLoadDialog(Component parent, String title, String[] items) {
        if (items == null || items.length == 0) {
            return null;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModal(true);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Titulo
        JLabel titleLabel = new JLabel("> SELECCIONA PARTIDA");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(FG_BRIGHT);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Lista
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String item : items) {
            listModel.addElement(item);
        }
        JList<String> list = new JList<>(listModel);
        list.setFont(LIST_FONT);
        list.setBackground(FIELD_BG);
        list.setForeground(FG);
        list.setSelectionBackground(LIST_SELECTION);
        list.setSelectionForeground(FG_BRIGHT);
        list.setBorder(new LineBorder(BORDER));
        list.setFixedCellHeight(28);
        list.setSelectedIndex(0);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBackground(BG);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(FIELD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JButton loadButton = createStyledButton("  CARGAR  ");
        loadButton.addActionListener(e -> dialog.dispose());

        JButton cancelButton = createStyledButton("  CANCELAR  ");
        cancelButton.addActionListener(e -> {
            list.clearSelection();
            dialog.dispose();
        });

        // Doble click en la lista tambien confirma
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && list.getSelectedIndex() >= 0) {
                    dialog.dispose();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BG);
        buttonPanel.add(loadButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.pack();
        dialog.setSize(Math.max(500, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        int selectedIndex = list.getSelectedIndex();
        return (selectedIndex >= 0) ? items[selectedIndex] : null;
    }

    // ---------------------------------------------------------------
    //  Dialogo informativo
    // ---------------------------------------------------------------

    /**
     * Muestra un mensaje informativo estilizado.
     */
    public static void showInfoDialog(Component parent, String title, String message) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModal(true);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel label = new JLabel("<html><pre style='color: #00dc50; font-family: monospace;'>"
                + message.replace("\n", "<br>") + "</pre></html>");
        label.setFont(FONT);
        label.setForeground(FG);
        panel.add(label, BorderLayout.CENTER);

        JButton okButton = createStyledButton("  OK  ");
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BG);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    // ---------------------------------------------------------------
    //  Utilidad
    // ---------------------------------------------------------------

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospaced", Font.BOLD, 13));
        button.setForeground(Color.BLACK);
        button.setBackground(FG);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new LineBorder(FG_DIM, 1));
        return button;
    }
}
