package vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class ModificationProfilView extends JFrame {
    private JTextField emailField;
    private JPasswordField mdpField;
    private JTextField nomField;
    private JTextField prenomField;
    private JButton btnSauvegarder;
    private JButton btnRetour;
    
    // Couleurs du thème
    private final Color NOIR = new Color(30, 30, 30);
    private final Color GRIS = new Color(128, 128, 128);
    private final Color BLANC = new Color(255, 255, 255);
    private final Color ROUGE = new Color(220, 0, 0);
    
    public ModificationProfilView(String currentEmail, String currentNom, String currentPrenom) {
        setTitle("Jeu de Dames - Modification Profil");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Panel principal avec le damier en arrière-plan
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dessiner le damier
                int tileSize = 75;
                for (int row = 0; row < getHeight() / tileSize + 1; row++) {
                    for (int col = 0; col < getWidth() / tileSize + 1; col++) {
                        if ((row + col) % 2 == 0) {
                            g.setColor(NOIR);
                        } else {
                            g.setColor(GRIS);
                        }
                        g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                    }
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Titre en haut
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("JEU DE DAMES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(BLANC);
        JLabel subtitleLabel = new JLabel("MODIFICATION PROFIL");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(BLANC);
        
        JPanel titlesContainer = new JPanel();
        titlesContainer.setLayout(new BoxLayout(titlesContainer, BoxLayout.Y_AXIS));
        titlesContainer.setOpaque(false);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlesContainer.add(titleLabel);
        titlesContainer.add(subtitleLabel);
        titlePanel.add(titlesContainer);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Formulaire au centre
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 20));
        inputPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        inputPanel.setOpaque(false);
        
        // Style des labels
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        
        // Email
        JLabel emailLabel = new JLabel("Adresse mail :");
        emailLabel.setForeground(BLANC);
        emailLabel.setFont(labelFont);
        inputPanel.add(emailLabel);
        
        emailField = new JTextField(currentEmail);
        emailField.setEditable(false);
        emailField.setBackground(BLANC);
        emailField.setPreferredSize(new Dimension(250, 30));
        emailField.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        inputPanel.add(emailField);
        
        // Mot de passe
        JLabel mdpLabel = new JLabel("Mot de passe :");
        mdpLabel.setForeground(BLANC);
        mdpLabel.setFont(labelFont);
        inputPanel.add(mdpLabel);
        
        mdpField = new JPasswordField();
        mdpField.setBackground(BLANC);
        mdpField.setPreferredSize(new Dimension(250, 30));
        inputPanel.add(mdpField);
        
        // Nom
        JLabel nomLabel = new JLabel("Nom :");
        nomLabel.setForeground(BLANC);
        nomLabel.setFont(labelFont);
        inputPanel.add(nomLabel);
        
        nomField = new JTextField(currentNom);
        nomField.setBackground(BLANC);
        nomField.setPreferredSize(new Dimension(250, 30));
        inputPanel.add(nomField);
        
        // Prénom
        JLabel prenomLabel = new JLabel("Prénom :");
        prenomLabel.setForeground(BLANC);
        prenomLabel.setFont(labelFont);
        inputPanel.add(prenomLabel);
        
        prenomField = new JTextField(currentPrenom);
        prenomField.setBackground(BLANC);
        prenomField.setPreferredSize(new Dimension(250, 30));
        inputPanel.add(prenomField);
        
        formPanel.add(inputPanel);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Boutons en bas
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));
        buttonPanel.setOpaque(false);
        
        btnSauvegarder = new JButton("Sauvegarder");
        btnSauvegarder.setPreferredSize(new Dimension(150, 40));
        btnSauvegarder.setBackground(ROUGE);
        btnSauvegarder.setForeground(BLANC);
        btnSauvegarder.setFont(new Font("Arial", Font.BOLD, 14));
        btnSauvegarder.setFocusPainted(false);
        btnSauvegarder.setBorder(null);
        
        btnRetour = new JButton("Retour");
        btnRetour.setPreferredSize(new Dimension(150, 40));
        btnRetour.setBackground(new Color(50, 50, 50));
        btnRetour.setForeground(BLANC);
        btnRetour.setFont(new Font("Arial", Font.BOLD, 14));
        btnRetour.setFocusPainted(false);
        btnRetour.setBorder(null);
        
        buttonPanel.add(btnSauvegarder);
        buttonPanel.add(btnRetour);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // Getters
    public String getEmail() {
        return emailField.getText();
    }
    
    public String getMdp() {
        return new String(mdpField.getPassword());
    }
    
    public String getNom() {
        return nomField.getText();
    }
    
    public String getPrenom() {
        return prenomField.getText();
    }
    
    // Listeners
    public void addSauvegarderListener(ActionListener listener) {
        btnSauvegarder.addActionListener(listener);
    }
    
    public void addRetourListener(ActionListener listener) {
        btnRetour.addActionListener(listener);
    }
    
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
   
}