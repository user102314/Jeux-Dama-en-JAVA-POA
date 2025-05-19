package vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

/**
 * Vue principale du Jeu de Dames
 * Affiche le menu principal avec les boutons de navigation
 */
public class MainView extends JFrame {
    
    // Composants de l'interface
    private JPanel contentPane;
    private JButton btnNouvellePartie;
    private JButton btnReprendre;
    private JButton btnStatistiques;
    private JButton btnModifierProfil;
    private JButton btnQuitterJeu;
    
    // Couleurs
    private final Color BACKGROUND_BLACK = new Color(30, 30, 30);
    private final Color BACKGROUND_GRAY = new Color(128, 128, 128);
    private final Color BUTTON_BLUE = new Color(77, 210, 226);
    private final Color RED_CHECKER = new Color(255, 0, 0);
    
    /**
     * Constructeur de la vue principale
     */
    public MainView() {
        // Configuration de la fenêtre
        setTitle("Jeu de Dames");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,500);
        setLocationRelativeTo(null);
        
        // Panneau principal avec damier en fond
        contentPane = new CheckerboardPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        
        // En-tête avec le titre
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("JEU DE DAMES");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Panneau central avec les boutons de menu
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(20, 80, 20, 80));
        
        // Création des boutons hexagonaux
        btnNouvellePartie = createHexagonButton("nouvelle partie");
        btnReprendre = createHexagonButton("reprendre");
        btnStatistiques = createHexagonButton("statistiques");
        btnModifierProfil = createHexagonButton("modifier le profil");
        btnQuitterJeu = createHexagonButton("quitter le jeu");
        
        // Ajout des boutons au menu avec espacement
        addButtonWithSpacing(menuPanel, btnNouvellePartie);
        addButtonWithSpacing(menuPanel, btnReprendre);
        addButtonWithSpacing(menuPanel, btnStatistiques);
        addButtonWithSpacing(menuPanel, btnModifierProfil);
        addButtonWithSpacing(menuPanel, btnQuitterJeu);
        
        contentPane.add(menuPanel, BorderLayout.CENTER);
        
        // Panneau pour les boutons audio
        JPanel audioPanel = new JPanel();
        audioPanel.setOpaque(false);
        audioPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
    }
    
    
    /**
     * Ajoute un bouton au panneau avec un espacement vertical
     */
    private void addButtonWithSpacing(JPanel panel, JComponent button) {
        panel.add(Box.createVerticalStrut(15));
        panel.add(button);
    }
    
    /**
     * Crée un bouton hexagonal stylisé
     */
    private JButton createHexagonButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dessiner la forme hexagonale
                int width = getWidth();
                int height = getHeight();
                int[] xPoints = {
                    20, width - 20,
                    width, width - 20,
                    20, 0
                };
                int[] yPoints = {
                    0, 0,
                    height / 2, height,
                    height, height / 2
                };
                
                g2d.setColor(BUTTON_BLUE);
                g2d.fillPolygon(xPoints, yPoints, 6);
                
                // Dessiner le texte
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), 
                        (width - textWidth) / 2, 
                        (height + textHeight / 3) / 2);
            }
        };
        
        button.setPreferredSize(new Dimension(380, 50));
        button.setMaximumSize(new Dimension(380, 50));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return button;
    }
    
    
    /**
     * Classe interne pour créer le fond en damier
     */
    private class CheckerboardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            int width = getWidth();
            int height = getHeight();
            int cellSize = 60;
            
            // Dessiner le damier
            for (int row = 0; row < height / cellSize + 1; row++) {
                for (int col = 0; col < width / cellSize + 1; col++) {
                    if ((row + col) % 2 == 0) {
                        g2d.setColor(BACKGROUND_BLACK);
                    } else {
                        g2d.setColor(BACKGROUND_GRAY);
                    }
                    g2d.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }
            }
            
            // Bandes rouges sur les côtés
            g2d.setColor(RED_CHECKER);
            // Damier rouge à gauche
            int checkerSize = cellSize;
            for (int row = 0; row < height / checkerSize + 1; row++) {
                for (int col = 0; col < 1; col++) {
                    if ((row + col) % 2 == 0) {
                        g2d.fillRect(col * checkerSize, row * checkerSize, checkerSize, checkerSize);
                    }
                }
            }
            
            // Damier rouge à droite
            for (int row = 0; row < height / checkerSize + 1; row++) {
                for (int col = 0; col < 1; col++) {
                    if ((row + col) % 2 == 0) {
                        g2d.fillRect(width - (col + 1) * checkerSize, row * checkerSize, checkerSize, checkerSize);
                    }
                }
            }
        }
    }
    
    // Méthodes pour ajouter des écouteurs
    public void addNouvellePartieListener(ActionListener listener) {
        btnNouvellePartie.addActionListener(listener);
    }
    
    public void addReprendreListener(ActionListener listener) {
        btnReprendre.addActionListener(listener);
    }
    
    public void addStatistiquesListener(ActionListener listener) {
        btnStatistiques.addActionListener(listener);
    }
    
    public void addModifierProfilListener(ActionListener listener) {
        btnModifierProfil.addActionListener(listener);
    }
    
    public void addQuitterJeuListener(ActionListener listener) {
        btnQuitterJeu.addActionListener(listener);
    }
    
    
    
   
    /**
     * Méthode principale pour tester la vue
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            view.setVisible(true);
        });
    }
}