package piano.atc79.view;

import piano.atc79.persistence.DAO;
import piano.atc79.persistence.PostgresDAO;
import piano.atc79.util.PasswordHash;
import piano.atc79.util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Dialogo modal de inicio de sesion.
 * <p>
 * Permite al jugador introducir su alias y contrasena. Si el alias
 * no existe, se crea un nuevo jugador automaticamente. Si existe,
 * se verifica la contrasena. Tras un login exitoso, la sesion se
 * persiste en {@code last_player.txt}.</p>
 */
public class LoginDialog extends JDialog {

    private final JTextField aliasField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;
    private final DAO dao;
    private boolean succeeded;

    /**
     * Crea el dialogo de login.
     *
     * @param owner la ventana padre (puede ser null)
     */
    public LoginDialog(Frame owner) {
        super(owner, "INICIAR SESION — Terminal ATC-79", true);
        this.dao = new PostgresDAO();

        setSize(400, 280);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titulo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("TERMINAL ATC-79", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(new Color(0, 255, 100));
        panel.add(title, gbc);

        // Alias
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel aliasLabel = new JLabel("ALIAS:");
        aliasLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        aliasLabel.setForeground(Color.WHITE);
        panel.add(aliasLabel, gbc);

        gbc.gridx = 1;
        aliasField = new JTextField(15);
        aliasField.setBackground(Color.DARK_GRAY);
        aliasField.setForeground(Color.GREEN);
        aliasField.setCaretColor(Color.GREEN);
        aliasField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(aliasField, gbc);

        // Contrasena
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passLabel = new JLabel("CONTRASENA:");
        passLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        passLabel.setForeground(Color.WHITE);
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setBackground(Color.DARK_GRAY);
        passwordField.setForeground(Color.GREEN);
        passwordField.setCaretColor(Color.GREEN);
        passwordField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        // Error
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        errorLabel.setForeground(Color.RED);
        panel.add(errorLabel, gbc);

        // Botones
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JButton enterButton = new JButton("ENTRAR");
        enterButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        enterButton.setForeground(Color.BLACK);
        enterButton.setBackground(new Color(0, 200, 80));
        enterButton.setFocusPainted(false);
        enterButton.addActionListener(e -> attemptLogin());
        panel.add(enterButton, gbc);

        gbc.gridx = 1;
        JButton cancelButton = new JButton("SALIR");
        cancelButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBackground(new Color(200, 80, 80));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> {
            succeeded = false;
            dispose();
        });
        panel.add(cancelButton, gbc);

        // Enter en los campos de texto intenta login
        aliasField.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        add(panel);
    }

    /**
     * Intenta iniciar sesion con los datos introducidos.
     * Si el alias no existe, lo crea. Si existe, verifica la contrasena.
     */
    private void attemptLogin() {
        String alias = aliasField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (alias.isEmpty()) {
            errorLabel.setText("Introduce un alias");
            return;
        }
        if (password.isEmpty()) {
            errorLabel.setText("Introduce una contrasena");
            return;
        }

        String hash = PasswordHash.hash(password);

        // Intentar login primero
        Integer playerId = dao.loginPlayer(alias, hash);
        if (playerId != null) {
            // Login exitoso
            SessionManager.saveSession(alias);
            succeeded = true;
            dispose();
            return;
        }

        // Podria ser que el alias exista pero la contrasena sea incorrecta
        // Lo comprobamos: si login fallo y crear nuevo jugador falla (unique),
        // entonces la contrasena era incorrecta
        if (dao.createPlayer(alias, hash)) {
            // Nuevo jugador creado
            SessionManager.saveSession(alias);
            succeeded = true;
            dispose();
        } else {
            // El alias ya existe pero la contrasena no coincide
            errorLabel.setText("Contrasena incorrecta");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    /**
     * Indica si el login fue exitoso.
     *
     * @return true si el usuario se logueo correctamente
     */
    public boolean isSucceeded() {
        return succeeded;
    }
}
