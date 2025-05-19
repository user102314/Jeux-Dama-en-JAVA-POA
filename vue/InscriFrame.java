package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class InscriFrame extends JFrame {
    // Instance variables
    private JTextField emailField, nomField, prenomField;
    private JPasswordField passwordField;
    private JButton inscrireButton, btnQuitter;
    private JPanel contentPane;
    private JLabel logoLabel;
    private JLabel gameIconLabel;

    // Constantes pour les couleurs du jeu de dames
    private static final Color DARK_SQUARE = new Color(45, 45, 45);
    private static final Color LIGHT_SQUARE = new Color(240, 240, 240);
    private static final Color RED_PIECE = new Color(220, 0, 0);
    private static final Color GRAY_PIECE = new Color(150, 150, 150);
    private static final Color GOLD_ACCENT = new Color(255, 215, 0);

    // Constructor
    public InscriFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Jeu de Dames - Inscription");
        setBounds(100, 100, 600, 500);
        setLocationRelativeTo(null);
        setIconImage(createCheckerIcon().getImage());
        
        initializeUI();
    }

    // Initialize UI components
    private void initializeUI() {
        // Main panel with checkerboard pattern
        contentPane = createCheckerboardPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // Header section with game logo
        contentPane.add(createHeader(), BorderLayout.NORTH);

        // Form section
        contentPane.add(createFormPanel(), BorderLayout.CENTER);

        // Button section
        contentPane.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    // Create checkerboard panel
    private JPanel createCheckerboardPanel() {
        return new JPanel() {
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

    // Create header section
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
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
        JLabel subtitleLabel = new JLabel("INSCRIPTION", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        subtitleLabel.setForeground(GRAY_PIECE);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    // Create form panel
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 30, 30, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        // Add labels and fields
        formPanel.add(createLabel("Adresse mail :", labelFont, LIGHT_SQUARE), updateConstraints(gbc, 0, 0));
        emailField = createTextField();
        formPanel.add(emailField, updateConstraints(gbc, 1, 0));

        formPanel.add(createLabel("Mot de passe :", labelFont, LIGHT_SQUARE), updateConstraints(gbc, 0, 1));
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        formPanel.add(passwordField, updateConstraints(gbc, 1, 1));

        formPanel.add(createLabel("Nom :", labelFont, LIGHT_SQUARE), updateConstraints(gbc, 0, 2));
        nomField = createTextField();
        formPanel.add(nomField, updateConstraints(gbc, 1, 2));

        formPanel.add(createLabel("Prénom :", labelFont, LIGHT_SQUARE), updateConstraints(gbc, 0, 3));
        prenomField = createTextField();
        formPanel.add(prenomField, updateConstraints(gbc, 1, 3));

        // Panel pour centrer le formulaire
        JPanel centerWrapperPanel = new JPanel(new GridBagLayout());
        centerWrapperPanel.setOpaque(false);
        centerWrapperPanel.add(formPanel);

        return centerWrapperPanel;
    }

    // Create button panel
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 30, 0));

        inscrireButton = createStyledButton("S'inscrire", RED_PIECE);
        btnQuitter = createStyledButton("Retour", DARK_SQUARE);

        buttonPanel.add(inscrireButton);
        buttonPanel.add(btnQuitter);

        return buttonPanel;
    }

    // Helper methods
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
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(LIGHT_SQUARE);
        field.setForeground(DARK_SQUARE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_SQUARE, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD_ACCENT, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DARK_SQUARE, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD_ACCENT, 2),
                    BorderFactory.createEmptyBorder(10, 28, 10, 28)
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

    // Public accessor methods
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

/**
 * Classe utilitaire pour l'animation des composants
 * Cette classe est conservée de vos fichiers originaux
 */
class AnimationUtils {
    // Déclaration de la classe LongHolder en tant que classe interne statique
    private static class LongHolder {
        long value;
        
        LongHolder(long value) {
            this.value = value;
        }
    }

    public static void animateTextColor(JLabel label, float[] fractions, Color[] colors, int duration) {
        Timer colorTimer = new Timer(50, null);
        LongHolder startTimeHolder = new LongHolder(-1);
        
        colorTimer.addActionListener(e -> {
            if (startTimeHolder.value < 0) {
                startTimeHolder.value = System.currentTimeMillis();
            }
            
            long currentTime = System.currentTimeMillis();
            float progress = (currentTime - startTimeHolder.value) / (float) duration;
            
            if (progress > 1f) {
                progress = 1f;
                colorTimer.stop();
            }
            
            Color color = getGradientColor(progress, fractions, colors);
            label.setForeground(color);
        });
        
        colorTimer.start();
    }
    
    private static Color getGradientColor(float progress, float[] fractions, Color[] colors) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions array must not be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colors array must not be null");
        }
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Fractions and colors must have equal length");
        }
        
        int[] indicies = getFractionIndicies(progress, fractions);
        
        float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
        Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
        
        float max = range[1] - range[0];
        float value = progress - range[0];
        float weight = value / max;
        
        return blend(colorRange[0], colorRange[1], 1f - weight);
    }
    
    private static int[] getFractionIndicies(float progress, float[] fractions) {
        int[] range = new int[2];
        
        for (int i = 0; i < fractions.length; i++) {
            if (fractions[i] >= progress) {
                range[1] = i;
                if (i == 0) {
                    range[0] = 0;
                } else {
                    range[0] = i - 1;
                }
                break;
            }
        }
        
        return range;
    }

    private static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;
        
        float[] rgb1 = color1.getRGBColorComponents(null);
        float[] rgb2 = color2.getRGBColorComponents(null);
        
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        
        return new Color(red, green, blue);
    }
}