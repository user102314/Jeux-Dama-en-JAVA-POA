package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import util.connexionDB;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.sql.*;

public class StatistiquesView extends JFrame {

    private String emailUtilisateur;
    private Color bgColor = new Color(50, 50, 50);   // Gris foncé
    private Color primaryColor = new Color(70, 210, 225);  // Bleu turquoise
    private Color textColor = Color.WHITE;
    private Color accentColor = Color.RED;

    public StatistiquesView(String email) {
        this.emailUtilisateur = email;
        setTitle("Statistiques - Jeu de Dames");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Fond d'écran en damier
        setContentPane(new DamierPanel());
        getContentPane().setLayout(new BorderLayout());
        
        JLabel titleLabel = createTitleLabel("STATISTIQUES");
        getContentPane().add(titleLabel, BorderLayout.NORTH);

        try {
            JPanel statsPanel = creerPanneauStats();
            JScrollPane scrollPane = new JScrollPane(statsPanel);
            scrollPane.getViewport().setBackground(bgColor);
            scrollPane.setBorder(null);
            getContentPane().add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des statistiques : " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Bouton fermer avec le nouveau style
        JButton btnFermer = createStyledButton("Fermer");
        btnFermer.addActionListener(e -> dispose());
        
        JPanel panelBas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBas.setOpaque(false);
        panelBas.add(btnFermer);
        getContentPane().add(panelBas, BorderLayout.SOUTH);

        setVisible(true);
    }
    
    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 32));
        label.setForeground(textColor);
        label.setPreferredSize(new Dimension(getWidth(), 60));
        label.setOpaque(false);
        return label;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dessiner le fond du bouton
                GradientPaint gradient = new GradientPaint(
                    0, 0, primaryColor.darker(), 
                    0, getHeight(), primaryColor
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                // Bordure
                g2.setColor(primaryColor.brighter());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                // Texte
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(text, g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                
                g2.setColor(Color.BLACK);
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(200, 50));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effets de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, 17));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, 16));
            }
        });
        
        return button;
    }

    private JPanel creerPanneauStats() throws SQLException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        // Ajout du résumé global en haut
        JPanel resumePanel = createResumePanel();
        panel.add(resumePanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Titre des parties
        JLabel partiesLabel = new JLabel("HISTORIQUE DES PARTIES");
        partiesLabel.setFont(new Font("Arial", Font.BOLD, 22));
        partiesLabel.setForeground(textColor);
        partiesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(partiesLabel);
        panel.add(Box.createVerticalStrut(15));

        // Récupération des données des parties
        String sql = "SELECT * FROM statistiques_parties WHERE email = ? ORDER BY date_partie DESC";
        connexionDB db = new connexionDB();
        Connection conn = db.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, emailUtilisateur);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            long date = rs.getTimestamp("date_partie").getTime();
            int scoreJoueur = rs.getInt("score_joueur");
            int scoreOrdinateur = rs.getInt("score_ordi");
            int coupsJoueur = rs.getInt("coups_joueur");
            int coupsOrdinateur = rs.getInt("coups_ordi");
            int capturesJoueur = rs.getInt("captures_joueur");
            int capturesOrdinateur = rs.getInt("captures_ordi");
            long dureeMs = rs.getLong("duree_ms");

            JPanel partiePanel = createPartiePanel(
                new java.util.Date(date), 
                scoreJoueur, scoreOrdinateur,
                coupsJoueur, coupsOrdinateur,
                capturesJoueur, capturesOrdinateur,
                dureeMs
            );
            
            panel.add(partiePanel);
            panel.add(Box.createVerticalStrut(10));
        }

        rs.close();
        pstmt.close();
        conn.close();

        return panel;
    }
    
    private JPanel createResumePanel() throws SQLException {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryColor, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(new Color(30, 30, 30, 200));
        panel.setMaximumSize(new Dimension(800, 250));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Calcul des statistiques globales
        String sql = "SELECT * FROM statistiques_parties WHERE email = ?";
        connexionDB db = new connexionDB();
        Connection conn = db.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, emailUtilisateur);
        ResultSet rs = pstmt.executeQuery();

        int totalVictoires = 0;
        int totalDefaites = 0;
        int totalNulles = 0;
        int totalCoupsJoueur = 0;
        int totalCoupsOrdinateur = 0;
        int totalCapturesJoueur = 0;
        int totalCapturesOrdinateur = 0;
        long totalDuree = 0;
        int nombreParties = 0;

        while (rs.next()) {
            nombreParties++;
            int scoreJoueur = rs.getInt("score_joueur");
            int scoreOrdinateur = rs.getInt("score_ordi");
            totalCoupsJoueur += rs.getInt("coups_joueur");
            totalCoupsOrdinateur += rs.getInt("coups_ordi");
            totalCapturesJoueur += rs.getInt("captures_joueur");
            totalCapturesOrdinateur += rs.getInt("captures_ordi");
            totalDuree += rs.getLong("duree_ms");

            if (scoreJoueur > scoreOrdinateur) {
                totalVictoires++;
            } else if (scoreJoueur < scoreOrdinateur) {
                totalDefaites++;
            } else {
                totalNulles++;
            }
        }
        
        rs.close();
        pstmt.close();
        conn.close();
        
        // Calcul des statistiques additionnelles
        double dureeMoyenne = nombreParties > 0 ? totalDuree / nombreParties / 1000.0 : 0;
        double tauxVictoire = nombreParties > 0 ? (double) totalVictoires / nombreParties * 100 : 0;
        double capturesMoyennes = nombreParties > 0 ? (double) totalCapturesJoueur / nombreParties : 0;
        
        // Titre
        JLabel titleLabel = new JLabel("RÉSUMÉ DE VOS PERFORMANCES", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);
        panel.add(titleLabel);
        panel.add(new JLabel(""));  // Cellule vide pour compléter la ligne
        
        // Ajout des statistiques avec style
        addStatRow(panel, "Parties jouées", String.valueOf(nombreParties));
        addStatRow(panel, "Victoires", totalVictoires + " (" + String.format("%.1f", tauxVictoire) + "%)");
        addStatRow(panel, "Défaites", String.valueOf(totalDefaites));
        addStatRow(panel, "Parties nulles", String.valueOf(totalNulles));
        addStatRow(panel, "Total de vos captures", String.valueOf(totalCapturesJoueur));
        addStatRow(panel, "Captures par partie", String.format("%.1f", capturesMoyennes));
        addStatRow(panel, "Total de vos coups", String.valueOf(totalCoupsJoueur));
        addStatRow(panel, "Durée moyenne par partie", String.format("%.1f secondes", dureeMoyenne));
        
        return panel;
    }
    
    private void addStatRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label + " :");
        labelComp.setFont(new Font("Arial", Font.PLAIN, 14));
        labelComp.setForeground(textColor);
        
        JLabel valueComp = new JLabel(value, JLabel.RIGHT);
        valueComp.setFont(new Font("Arial", Font.BOLD, 14));
        valueComp.setForeground(primaryColor);
        
        panel.add(labelComp);
        panel.add(valueComp);
    }
    
    private JPanel createPartiePanel(java.util.Date date, int scoreJoueur, int scoreOrdinateur,
                                    int coupsJoueur, int coupsOrdinateur,
                                    int capturesJoueur, int capturesOrdinateur,
                                    long dureeMs) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryColor, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(40, 40, 40, 180));
        panel.setMaximumSize(new Dimension(800, 120));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Date et résultat
        String resultat = scoreJoueur > scoreOrdinateur ? "VICTOIRE" : 
                         (scoreJoueur < scoreOrdinateur ? "DÉFAITE" : "MATCH NUL");
        Color resultatColor = scoreJoueur > scoreOrdinateur ? new Color(50, 200, 50) : 
                             (scoreJoueur < scoreOrdinateur ? accentColor : Color.YELLOW);
        
        JLabel dateLabel = new JLabel(date.toString());
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        dateLabel.setForeground(textColor);
        
        JLabel resultatLabel = new JLabel(resultat, JLabel.RIGHT);
        resultatLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultatLabel.setForeground(resultatColor);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(dateLabel, BorderLayout.WEST);
        topPanel.add(resultatLabel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Score et statistiques
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 0, 3));
        statsPanel.setOpaque(false);
        
        addInfoRow(statsPanel, "Score", scoreJoueur + " - " + scoreOrdinateur);
        addInfoRow(statsPanel, "Coups joués", coupsJoueur + " (vous), " + coupsOrdinateur + " (ordi)");
        addInfoRow(statsPanel, "Captures", capturesJoueur + " (vous), " + capturesOrdinateur + " (ordi)");
        addInfoRow(statsPanel, "Durée", String.format("%.1f secondes", dureeMs / 1000.0));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label + " :");
        labelComp.setFont(new Font("Arial", Font.PLAIN, 13));
        labelComp.setForeground(textColor);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.BOLD, 13));
        valueComp.setForeground(Color.WHITE);
        
        panel.add(labelComp);
        panel.add(valueComp);
    }
    
    // Classe interne pour créer un fond en damier
    private class DamierPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Fond gris foncé
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Damier
            int taille = 70;  // Taille des cases
            for (int i = 0; i < getWidth() / taille + 1; i++) {
                for (int j = 0; j < getHeight() / taille + 1; j++) {
                    if ((i + j) % 2 == 0) {
                        // Cases grises claires
                        g2d.setColor(Color.GRAY);
                    } else {
                        // Cases noires
                        g2d.setColor(Color.BLACK);
                    }
                    g2d.fillRect(i * taille, j * taille, taille, taille);
                    
                    // Ajout de cases rouges sur les bords (comme dans l'image)
                    if (i == 0 || i == getWidth() / taille || j == 0 || j == getHeight() / taille) {
                        if ((i + j) % 3 == 0) {
                            g2d.setColor(accentColor);
                            g2d.fillRect(i * taille, j * taille, taille, taille);
                        }
                    }
                }
            }
            
            // Overlay semi-transparent pour améliorer la lisibilité
            g2d.setColor(new Color(20, 20, 20, 100));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}