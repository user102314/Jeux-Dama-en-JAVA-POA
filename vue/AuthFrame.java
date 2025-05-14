package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AuthFrame extends JFrame {
    public static final String getPasswordField = null;
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

    public AuthFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        setTitle("Authentification");
        setLocationRelativeTo(null); // Centrer la fenêtre
        
        // Création d'un panel avec un fond dégradé
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(50, 50, 50); // Noir foncé
                Color color2 = new Color(100, 100, 100); // Gris foncé
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        
        // Panel d'en-tête
        headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        logoLabel = new JLabel("CONNEXION", JLabel.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(new Color(245, 245, 220)); // Beige clair
        headerPanel.add(logoLabel);
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Panel central pour les champs de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Style commun pour les labels
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color labelColor = new Color(245, 245, 220); // Beige clair
        
        // Champ email
        lblAdrMail = new JLabel("Adresse mail : ");
        lblAdrMail.setFont(labelFont);
        lblAdrMail.setForeground(labelColor);
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
        lblMotDePasse.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblMotDePasse, gbc);
        
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);
        
        contentPane.add(formPanel, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        btnInscrire = createStyledButton("S'inscrire", new Color(139, 69, 19)); // Brun
        buttonPanel.add(btnInscrire);
        
        btnLogin = createStyledButton("Login", new Color(70, 130, 180)); // Bleu acier
        buttonPanel.add(btnLogin);
        
        btnQuitter = createStyledButton("Quitter", new Color(178, 34, 34)); // Rouge brique
        buttonPanel.add(btnQuitter);
        
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        // Animation subtile pour le logo
        animateLogo();
    }
    public JPasswordField getPasswordField() { return passwordField; }

    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(245, 245, 245)); // Blanc cassé
        field.setForeground(new Color(50, 50, 50)); // Noir
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(205, 133, 63), 2), // Beige doré
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Animation de focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 215, 0), 2), // Or
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(205, 133, 63), 2), // Beige doré
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
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
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 1),
                    BorderFactory.createEmptyBorder(9, 24, 9, 24)
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
    
    private void animateLogo() {
        Timer timer = new Timer(3000, e -> {
            float[] fractions = {0f, 0.5f, 1f};
            Color[] colors = {
                new Color(245, 245, 220), // Beige clair
                new Color(255, 215, 0),   // Or
                new Color(245, 245, 220)  // Beige clair
            };
            
            AnimationUtils.animateTextColor(logoLabel, fractions, colors, 1500);
        });
        timer.setRepeats(true);
        timer.start();
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