package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Classe AuthFrame améliorée pour l'interface d'authentification
 * Thème adapté au jeu de dames avec les couleurs du plateau de jeu
 */
public class AuthFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel lblAdrMail;
    private JLabel lblMotDePasse;
    private JButton btnInscrire;
    private JButton btnLogin;
    private JButton btnQuitter;
    private JPanel headerPanel;
    private JLabel logoLabel;
    private JLabel gameIconLabel;

    // Constantes pour les couleurs du jeu de dames
    private static final Color DARK_SQUARE = new Color(45, 45, 45);
    private static final Color LIGHT_SQUARE = new Color(240, 240, 240);
    private static final Color RED_PIECE = new Color(220, 0, 0);
    private static final Color GRAY_PIECE = new Color(150, 150, 150);
    private static final Color GOLD_ACCENT = new Color(255, 215, 0);

    public AuthFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 500);
        setTitle("Jeu de Dames - Authentification");
        setLocationRelativeTo(null);
        setIconImage(createCheckerIcon().getImage());
        
        initializeUI();
    }
    
    private void initializeUI() {
        // Panel principal avec un motif damier
        contentPane = createCheckerboardPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        
        // Panel d'en-tête avec logo du jeu de dames
        headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Icône du jeu
        gameIconLabel = new JLabel(createCheckerIcon());
        gameIconLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(gameIconLabel, BorderLayout.WEST);
        
        // Titre
        logoLabel = new JLabel("JEU DE DAMES", JLabel.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        logoLabel.setForeground(LIGHT_SQUARE);
        headerPanel.add(logoLabel, BorderLayout.CENTER);
        
        // Sous-titre
        JLabel subtitleLabel = new JLabel("CONNEXION", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        subtitleLabel.setForeground(RED_PIECE);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Panel central avec fond translucide pour les champs de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(30, 30, 30, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Style commun pour les labels
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        
        // Champ email
        lblAdrMail = new JLabel("Adresse mail : ");
        lblAdrMail.setFont(labelFont);
        lblAdrMail.setForeground(LIGHT_SQUARE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblAdrMail, gbc);
        
        emailField = new JTextField(20);
        styleTextField(emailField);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(emailField, gbc);
        
        // Champ mot de passe
        lblMotDePasse = new JLabel("Mot de passe :");
        lblMotDePasse.setFont(labelFont);
        lblMotDePasse.setForeground(LIGHT_SQUARE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblMotDePasse, gbc);
        
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);
        
        // Panel pour centrer le formulaire
        JPanel centerWrapperPanel = new JPanel(new GridBagLayout());
        centerWrapperPanel.setOpaque(false);
        centerWrapperPanel.add(formPanel);
        contentPane.add(centerWrapperPanel, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        btnInscrire = createStyledButton("S'inscrire", GRAY_PIECE);
        buttonPanel.add(btnInscrire);
        
        btnLogin = createStyledButton("Connexion", RED_PIECE);
        buttonPanel.add(btnLogin);
        
        btnQuitter = createStyledButton("Quitter", DARK_SQUARE);
        buttonPanel.add(btnQuitter);
        
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCheckerboardPanel() {
        return new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                int squareSize = 70;
                int width = getWidth();
                int height = getHeight();
                
                // Dessiner le damier
                for (int i = 0; i < width / squareSize + 1; i++) {
                    for (int j = 0; j < height / squareSize + 1; j++) {
                        if ((i + j) % 2 == 0) {
                            g2d.setColor(DARK_SQUARE);
                        } else {
                            g2d.setColor(LIGHT_SQUARE);
                        }
                        g2d.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
                    }
                }
                
                // Effet de transparence pour la lisibilité
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRect(0, 0, width, height);
            }
        };
    }
    
    private ImageIcon createCheckerIcon() {
        // Création d'une icône représentant un pion du jeu de dames
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        
        // Dessin du carré noir
        g2d.setColor(DARK_SQUARE);
        g2d.fillRect(0, 0, 32, 32);
        
        // Dessin du pion rouge
        g2d.setColor(RED_PIECE);
        g2d.fillOval(4, 4, 24, 24);
        
        // Effet 3D
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(8, 8, 10, 10);
        
        g2d.dispose();
        return new ImageIcon(icon);
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(LIGHT_SQUARE);
        field.setForeground(DARK_SQUARE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_SQUARE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Animation de focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD_ACCENT, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DARK_SQUARE, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD_ACCENT, 2),
                    BorderFactory.createEmptyBorder(8, 23, 8, 23)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
            }
        });
        
        return button;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }
    
    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }
    
    public void addGoToInscriListener(ActionListener listener) {
        btnInscrire.addActionListener(listener);
    }
    
    public void addQuitListener(ActionListener listener) {
        btnQuitter.addActionListener(listener);
    }

    public void showMessage(String message, String titre, int type) {
        JOptionPane.showMessageDialog(this, message, titre, type);
    }
}