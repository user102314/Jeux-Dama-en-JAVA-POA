package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InscriFrame extends JFrame {

    // Instance variables
    private JTextField emailField, nomField, prenomField;
    private JPasswordField passwordField;
    private JButton inscrireButton, btnQuitter;
    private JPanel contentPane;
    private JLabel logoLabel;
	private JTextField PasswordField;

    // Constructor
    public InscriFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Inscription");
        setBounds(100, 100, 600, 450);
        setLocationRelativeTo(null);

        initializeUI();
    }

    // Initialize UI components
    private void initializeUI() {
        // Main panel with gradient background
        contentPane = createGradientPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // Header section
        contentPane.add(createHeader(), BorderLayout.NORTH);

        // Form section
        contentPane.add(createFormPanel(), BorderLayout.CENTER);

        // Button section
        contentPane.add(createButtonPanel(), BorderLayout.SOUTH);

        // Title animation
        animateTitle();
    }

    // Create gradient panel
    private JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(50, 50, 50), 0, getHeight(), new Color(100, 100, 100));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    // Create header section
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        logoLabel = new JLabel("INSCRIPTION", JLabel.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(new Color(245, 245, 220)); // Light beige
        headerPanel.add(logoLabel);

        return headerPanel;
    }

    // Create form panel
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Consistent spacing
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure fields stretch
        gbc.weightx = 1.0; // Distribute width evenly

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color labelColor = new Color(245, 245, 220); // Light beige

        // Add labels and fields
        formPanel.add(createLabel("Adresse mail :", labelFont, labelColor), updateConstraints(gbc, 0, 0));
        emailField = createTextField();
        formPanel.add(emailField, updateConstraints(gbc, 1, 0));

        formPanel.add(createLabel("Mot de passe :", labelFont, labelColor), updateConstraints(gbc, 0, 1));
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        formPanel.add(passwordField, updateConstraints(gbc, 1, 1));

        formPanel.add(createLabel("Nom :", labelFont, labelColor), updateConstraints(gbc, 0, 2));
        nomField = createTextField();
        formPanel.add(nomField, updateConstraints(gbc, 1, 2));

        formPanel.add(createLabel("Prénom :", labelFont, labelColor), updateConstraints(gbc, 0, 3));
        prenomField = createTextField();
        formPanel.add(prenomField, updateConstraints(gbc, 1, 3));

        return formPanel;
    }

   

    // Create button panel
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        inscrireButton = createStyledButton("S'inscrire", new Color(34, 139, 34)); // Forest green
        btnQuitter = createStyledButton("Quitter", new Color(178, 34, 34)); // Firebrick

        buttonPanel.add(inscrireButton);
        buttonPanel.add(btnQuitter);

        return buttonPanel;
    }

    // Helper methods
    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        styleTextField(textField);
        return textField;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(245, 245, 245)); // Off-white
        field.setForeground(new Color(50, 50, 50)); // Black
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(205, 133, 63), 2), // Golden beige
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 215, 0), 2), // Gold
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(205, 133, 63), 2), // Golden beige
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 1),
                    BorderFactory.createEmptyBorder(11, 29, 11, 29)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
            }
        });

        return button;
    }

    private GridBagConstraints updateConstraints(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }

    private void animateTitle() {
        Timer timer = new Timer(3000, e -> {
            float[] fractions = {0f, 0.5f, 1f};
            Color[] colors = {
                new Color(245, 245, 220), // Light beige
                new Color(255, 215, 0),   // Gold
                new Color(245, 245, 220)  // Light beige
            };
            AnimationUtils.animateTextColor(logoLabel, fractions, colors, 1500);
        });
        timer.setRepeats(true);
        timer.start();
    }

    // Public accessor methods
    public JTextField getEmailField() { return emailField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JTextField getNomField() { return nomField; }
    public JTextField getPrenomField() { return prenomField; }

    public String getEmail() { return emailField.getText(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getNom() { return nomField.getText(); }
    public String getPrenom() { return prenomField.getText(); }
    public void setEmail(String email) { emailField.setText(email); }
    public void setPassword(String password) { passwordField.setText(password); }
    public void setNom(String nom) { nomField.setText(nom); }
    public void setPrenom(String prenom) { prenomField.setText(prenom); }
    public void addInscriptionListener(ActionListener listener) { inscrireButton.addActionListener(listener); }
    public void addQuitterListener(ActionListener listener) { btnQuitter.addActionListener(listener); }
    public void showMessage(String message, String titre, int type) {
        JOptionPane.showMessageDialog(this, message, titre, type);
    }
}
