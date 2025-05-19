package damier;

import javax.swing.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controler.MainController;
import model.Case;
import model.Mouvement;
import util.EtatPartie;
import util.connexionDB;
import vue.MainView;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JeuDeDames extends JFrame {
	private String currentEmail;
    private static final int TAILLE = 10;  // Taille du plateau (10x10)
    private static final int TAILLE_CASE = 50;  // Taille d'une case en pixels
    private long tempsDebutPartie;
    private Case[][] plateau = new Case[TAILLE][TAILLE];
    private int joueurActuel = 1;  // 1 = joueur, 2 = ordinateur
    private Case caseSelectionnee = null;
    private List<Mouvement> mouvementsPossibles = new ArrayList<>();
    private JLabel statusBar;
    private boolean jeuTermine = false;
    private int coupsJoueur = 0;
    private int coupsOrdinateur = 0;
    
    
    // Variables pour le score
    private int scoreJoueur = 0;
    private int scoreOrdinateur = 0;
    private JLabel scoreLabel;
    
    // Panneau de score amélioré
    private JPanel scorePanel;
    private JLabel scoreJoueurLabel;
    private JLabel scoreOrdinateurLabel;
    
    // Valeurs des pièces pour le calcul du score
    private static final int POINTS_PION = 1;
    private static final int POINTS_DAME = 3;
    
    // Constructeur principal
    public JeuDeDames(String userEmail) {
    	this.currentEmail = userEmail;
        setTitle("Jeu de Dames");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Création du panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Création du plateau de jeu
        JPanel plateauPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerPlateau(g);
            }
        };
        plateauPanel.setPreferredSize(new Dimension(TAILLE * TAILLE_CASE, TAILLE * TAILLE_CASE));
        plateauPanel.addMouseListener(new GestionnaireClic());
        
        // Panneau d'information (statut et score)
        JPanel infoPanel = new JPanel(new BorderLayout());
        
        // Barre de statut
        statusBar = new JLabel("À votre tour");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setHorizontalAlignment(JLabel.CENTER);
        statusBar.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Panneau de score amélioré
        scorePanel = new JPanel(new GridLayout(1, 2));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Score"));
        
        // Score du joueur
        JPanel joueurScorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        joueurScorePanel.add(new JLabel("Vous: "));
        scoreJoueurLabel = new JLabel("0");
        scoreJoueurLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreJoueurLabel.setForeground(Color.RED);
        joueurScorePanel.add(scoreJoueurLabel);
        
        // Score de l'ordinateur
        JPanel ordinateurScorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ordinateurScorePanel.add(new JLabel("Ordinateur: "));
        scoreOrdinateurLabel = new JLabel("0");
        scoreOrdinateurLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreOrdinateurLabel.setForeground(Color.BLUE);
        ordinateurScorePanel.add(scoreOrdinateurLabel);
        
        scorePanel.add(joueurScorePanel);
        scorePanel.add(ordinateurScorePanel);
        
        // Ajouter statut et score au panneau d'information
        infoPanel.add(statusBar, BorderLayout.NORTH);
        infoPanel.add(scorePanel, BorderLayout.CENTER);
        
        // Bouton pour recommencer
        JButton nouvellePartie = new JButton("Nouvelle Partie");
        nouvellePartie.addActionListener(e -> initialiserJeu());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nouvellePartie);
     // Bouton Quitter
        JButton quitter = new JButton("Quitter");
        quitter.addActionListener(e -> {
            int choix = JOptionPane.showConfirmDialog(
                JeuDeDames.this,
                "Voulez-vous sauvegarder la partie avant de quitter ?",
                "Sauvegarder la partie",
                JOptionPane.YES_NO_CANCEL_OPTION
            );
            if (choix == JOptionPane.YES_OPTION) {
                sauvegarderPartie();
                JeuDeDames.this.dispose();  
                SwingUtilities.invokeLater(() -> {
                    MainView mainView = new MainView();
                    new MainController(mainView, currentEmail); // Associer le contrôleur
                    mainView.setVisible(true);
                });
            } else if (choix == JOptionPane.NO_OPTION) {
            	JeuDeDames.this.dispose();  
            	SwingUtilities.invokeLater(() -> {
                    MainView mainView = new MainView();
                    new MainController(mainView, currentEmail); // Associer le contrôleur
                    mainView.setVisible(true);
                });
            }
        });
        buttonPanel.add(quitter);
        
        
        // Ajout des composants
        mainPanel.add(plateauPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        initialiserJeu();
    }
    //sauvegard partie
    private void sauvegarderPartie() {
        String email = currentEmail;
        if (email == null || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vous devez être connecté pour sauvegarder.");
            return;
        }

        try {
            File dir = new File("sauvegardes");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, email + "_parties.json");  // Changement du nom de fichier
            ObjectMapper mapper = new ObjectMapper();
            List<EtatPartie> parties = new ArrayList<>();

            // Vérifier si le fichier existe et n'est pas vide
            if (file.exists() && file.length() > 0) {
                try {
                    parties = mapper.readValue(file, 
                        mapper.getTypeFactory().constructCollectionType(List.class, EtatPartie.class));
                } catch (JsonProcessingException e) {
                    // Si le fichier est corrompu, on crée une nouvelle liste
                    parties = new ArrayList<>();
                    JOptionPane.showMessageDialog(this, 
                        "Une sauvegarde existante était corrompue. Une nouvelle sauvegarde a été créée.",
                        "Avertissement", JOptionPane.WARNING_MESSAGE);
                }
            }

            // Créer la nouvelle partie avec un ID unique
            EtatPartie nouvellePartie = new EtatPartie(plateau, joueurActuel, jeuTermine, email);
            nouvellePartie.setId(UUID.randomUUID()); // Assurez-vous d'avoir un setter pour l'ID

            // Ajouter la nouvelle partie à la liste
            parties.add(nouvellePartie);

            // Limiter le nombre de sauvegardes si nécessaire (ex: 10 max)
            if (parties.size() > 10) {
                parties = parties.subList(parties.size() - 10, parties.size());
                JOptionPane.showMessageDialog(this, 
                    "Seules les 10 dernières sauvegardes sont conservées.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }

            // Sauvegarder toutes les parties
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, parties);
            JOptionPane.showMessageDialog(this, 
                "Partie sauvegardée avec succès !\nTotal des sauvegardes: " + parties.size(),
                "Sauvegarde", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la sauvegarde : " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
 // Constructeur pour charger une partie sauvegardée
    public JeuDeDames(String email, EtatPartie etat) {
        super("Jeu de Dames");
     
        this.currentEmail = email;

        // Charger l'état du plateau
        this.plateau = etat.getPlateau();
        this.joueurActuel = etat.getJoueurActuel();
        this.jeuTermine = etat.isJeuTermine();

        // Initialiser la fenêtre
        setTitle("Jeu de Dames");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Création du panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Création du plateau de jeu
        JPanel plateauPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerPlateau(g);
            }
        };
        plateauPanel.setPreferredSize(new Dimension(TAILLE * TAILLE_CASE, TAILLE * TAILLE_CASE));
        plateauPanel.addMouseListener(new GestionnaireClic());

        // Panneau d'information (statut et score)
        JPanel infoPanel = new JPanel(new BorderLayout());
        // Barre de statut
        statusBar = new JLabel("À votre tour");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setHorizontalAlignment(JLabel.CENTER);
        statusBar.setFont(new Font("Arial", Font.BOLD, 14));

        // Panneau de score amélioré
        scorePanel = new JPanel(new GridLayout(1, 2));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Score"));

        // Score du joueur
        JPanel joueurScorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        joueurScorePanel.add(new JLabel("Vous: "));
        scoreJoueurLabel = new JLabel("0");
        scoreJoueurLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreJoueurLabel.setForeground(Color.RED);
        joueurScorePanel.add(scoreJoueurLabel);

        // Score de l'ordinateur
        JPanel ordinateurScorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ordinateurScorePanel.add(new JLabel("Ordinateur: "));
        scoreOrdinateurLabel = new JLabel("0");
        scoreOrdinateurLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreOrdinateurLabel.setForeground(Color.BLUE);
        ordinateurScorePanel.add(scoreOrdinateurLabel);

        scorePanel.add(joueurScorePanel);
        scorePanel.add(ordinateurScorePanel);

        // Ajouter statut et score au panneau d'information
        infoPanel.add(statusBar, BorderLayout.NORTH);
        infoPanel.add(scorePanel, BorderLayout.CENTER);

        // Bouton pour recommencer
        JButton nouvellePartie = new JButton("Nouvelle Partie");
        nouvellePartie.addActionListener(e -> initialiserJeu());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nouvellePartie);

        // Ajouter le bouton "Quitter"
        JButton quitter = new JButton("Quitter");
        quitter.addActionListener(e -> {
            int choix = JOptionPane.showConfirmDialog(
                JeuDeDames.this,
                "Voulez-vous sauvegarder la partie avant de quitter ?",
                "Sauvegarder la partie",
                JOptionPane.YES_NO_CANCEL_OPTION
            );
            if (choix == JOptionPane.YES_OPTION) {
                sauvegarderPartie();
                JeuDeDames.this.dispose();  
                SwingUtilities.invokeLater(() -> {
                    MainView mainView = new MainView();
                    new MainController(mainView, currentEmail); // Associer le contrôleur
                    mainView.setVisible(true);
                });
            } else if (choix == JOptionPane.NO_OPTION) {
            	JeuDeDames.this.dispose();  
            	SwingUtilities.invokeLater(() -> {
                    MainView mainView = new MainView();
                    new MainController(mainView, currentEmail); // Associer le contrôleur
                    mainView.setVisible(true);
                });
            }
        });
        buttonPanel.add(quitter);

        // Ajout des composants
        mainPanel.add(plateauPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);

        // Mettre à jour les scores
        calculerScores();
        mettreAJourScoreLabel();

        repaint();
    }
    
    public JeuDeDames() {
	// TODO Auto-generated constructor stub
}
	// Initialisation du jeu
    private void initialiserJeu() {
    	tempsDebutPartie = System.currentTimeMillis();
    	scoreJoueur = 0;
        scoreOrdinateur = 0;
        coupsJoueur = 0;
        coupsOrdinateur = 0;
        
        // Création du plateau
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                plateau[ligne][colonne] = new Case(ligne, colonne);
                
                // Placer les pièces initiales
                if ((ligne + colonne) % 2 == 1) {  // Cases noires
                    if (ligne < 4) {
                        plateau[ligne][colonne].piece = 2;  // Pièces de l'ordinateur
                    } else if (ligne >= TAILLE - 4) {
                        plateau[ligne][colonne].piece = 1;  // Pièces du joueur
                    }
                }
            }
        }
        
        joueurActuel = 1;  // Le joueur commence
        caseSelectionnee = null;
        mouvementsPossibles.clear();
        jeuTermine = false;
        
        // Réinitialiser les scores
        scoreJoueur = 0;
        scoreOrdinateur = 0;
        mettreAJourScoreLabel();
        
        statusBar.setText("À votre tour");
        repaint();
    }
    
    // Dessiner le plateau
    private void dessinerPlateau(Graphics g) {
        // Dessiner les cases
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                if ((ligne + colonne) % 2 == 0) {
                    g.setColor(Color.WHITE);  // Cases blanches
                } else {
                    g.setColor(new Color(50, 50, 50));  // Cases noires (gris foncé)
                }
                g.fillRect(colonne * TAILLE_CASE, ligne * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
                
                // Dessiner la pièce si présente
                Case currentCase = plateau[ligne][colonne];
                if (currentCase.piece > 0) {
                    if (currentCase.piece == 1) {
                        g.setColor(Color.RED);  // Pièces du joueur
                    } else {
                        g.setColor(Color.GRAY);  // Pièces de l'ordinateur
                    }
                    
                    g.fillOval(
                        colonne * TAILLE_CASE + 10,
                        ligne * TAILLE_CASE + 10,
                        TAILLE_CASE - 20,
                        TAILLE_CASE - 20
                    );
                    
                    // Indiquer les dames (pièces promues)
                    if (currentCase.estDame) {
                        g.setColor(Color.WHITE);
                        g.drawOval(
                            colonne * TAILLE_CASE + 15,
                            ligne * TAILLE_CASE + 15,
                            TAILLE_CASE - 30,
                            TAILLE_CASE - 30
                        );
                        g.drawOval(
                            colonne * TAILLE_CASE + 20,
                            ligne * TAILLE_CASE + 20,
                            TAILLE_CASE - 40,
                            TAILLE_CASE - 40
                        );
                    }
                }
                
                // Mettre en évidence la case sélectionnée
                if (caseSelectionnee == currentCase) {
                    g.setColor(Color.YELLOW);
                    g.drawRect(
                        colonne * TAILLE_CASE + 2,
                        ligne * TAILLE_CASE + 2,
                        TAILLE_CASE - 4,
                        TAILLE_CASE - 4
                    );
                }
                
                // Afficher les mouvements possibles
                for (Mouvement mouvement : mouvementsPossibles) {
                    if (mouvement.destination == currentCase) {
                        g.setColor(Color.GREEN);
                        g.drawRect(
                            colonne * TAILLE_CASE + 5,
                            ligne * TAILLE_CASE + 5,
                            TAILLE_CASE - 10,
                            TAILLE_CASE - 10
                        );
                    }
                }
            }
        }
    }
    
    // Mettre à jour l'affichage du score
    private void mettreAJourScoreLabel() {
        scoreJoueurLabel.setText(String.valueOf(scoreJoueur));
        scoreOrdinateurLabel.setText(String.valueOf(scoreOrdinateur));
    }
    
    // Calculer les scores actuels
    private void calculerScores() {
        scoreJoueur = 0;
        scoreOrdinateur = 0;
        
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                Case c = plateau[ligne][colonne];
                if (c.piece == 1) {  // Pièce du joueur
                    scoreJoueur += c.estDame ? POINTS_DAME : POINTS_PION;
                } else if (c.piece == 2) {  // Pièce de l'ordinateur
                    scoreOrdinateur += c.estDame ? POINTS_DAME : POINTS_PION;
                }
            }
        }
        
        mettreAJourScoreLabel();
    }
    
    // Afficher l'écran de fin de partie
    private void afficherEcranFinDePartie() {
    	sauvegarderStatistiquesDansBase();
        // Créer une nouvelle fenêtre pour le résultat
        JDialog finPartie = new JDialog(this, "Fin de partie", true);
        finPartie.setSize(400, 300);
        finPartie.setLocationRelativeTo(this);
        finPartie.setResizable(false);
        
        // Panneau principal avec fond coloré
        JPanel panel = new JPanel(new BorderLayout());
        
        // Déterminer le message et la couleur en fonction du résultat
        String message;
        Color couleurFond;
        
        if (scoreJoueur > scoreOrdinateur) {
            message = "VOUS AVEZ GAGNÉ !";
            couleurFond = new Color(200, 255, 200); // Vert clair
        } else if (scoreOrdinateur > scoreJoueur) {
            message = "VOUS AVEZ PERDU";
            couleurFond = new Color(255, 200, 200); // Rouge clair
        } else {
            message = "MATCH NUL";
            couleurFond = new Color(230, 230, 230); // Gris clair
        }
        
        panel.setBackground(couleurFond);
        
        // Label pour le message principal
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 28));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(messageLabel, BorderLayout.NORTH);
        
        // Label pour le score
        JLabel scoreResultat = new JLabel("Score final: Vous " + scoreJoueur + " - Ordinateur " + scoreOrdinateur);
        scoreResultat.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreResultat.setHorizontalAlignment(JLabel.CENTER);
        panel.add(scoreResultat, BorderLayout.CENTER);
        
        // Panneau pour les boutons
        JPanel boutonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        boutonPanel.setOpaque(false);
        
        // Bouton pour rejouer
        JButton rejouerBtn = new JButton("Rejouer");
        rejouerBtn.setFont(new Font("Arial", Font.BOLD, 16));
        rejouerBtn.setPreferredSize(new Dimension(120, 40));
        rejouerBtn.addActionListener(e -> {
            finPartie.dispose();
            initialiserJeu();
        });
        
        // Bouton pour quitter
     // Dans votre méthode afficherEcranFinDePartie()
        JButton quitterBtn = new JButton("Quitter");
        quitterBtn.setFont(new Font("Arial", Font.BOLD, 16));
        quitterBtn.setPreferredSize(new Dimension(120, 40));
        quitterBtn.addActionListener(e -> {
            finPartie.dispose();               // Fermer l'écran de fin
            JeuDeDames.this.dispose();          // Fermer le jeu
            
            // Optionnel : ouvrir le menu principal
            SwingUtilities.invokeLater(() -> {
                MainView mainView = new MainView();
                new MainController(mainView, currentEmail); // Associer le contrôleur
                mainView.setVisible(true);
            });
        });
        
        boutonPanel.add(rejouerBtn);
        boutonPanel.add(quitterBtn);
        
        panel.add(boutonPanel, BorderLayout.SOUTH);
        
        finPartie.add(panel);
        finPartie.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        finPartie.setVisible(true);
        
    }
    private void sauvegarderStatistiquesDansBase() {
        long dureeMs = System.currentTimeMillis() - tempsDebutPartie;

        // Mettre à jour les scores
        calculerScores();

        
      
        int capturesJoueur=20-scoreOrdinateur;
        int capturesOrdinateur=20-scoreJoueur;
        int damesJoueur = compterDames(1); // Nombre de dames du joueur
        int damesOrdinateur = compterDames(2); // Nombre de dames de l'ordinateur

        String sql = "INSERT INTO statistiques_parties(email, date_partie, score_joueur, score_ordi, " +
                     "coups_joueur, coups_ordi, captures_joueur, captures_ordi, " +
                     "dames_joueur, dames_ordi, duree_ms) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

            connexionDB db = new connexionDB();
            try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Afficher les valeurs pour débogage
            System.out.println("Email : " + currentEmail);
            System.out.println("Date de la partie : " + new java.sql.Timestamp(System.currentTimeMillis()));
            System.out.println("Score joueur : " + scoreJoueur);
            System.out.println("Score ordinateur : " + scoreOrdinateur);
            System.out.println("Coups joueur : " + coupsJoueur);
            System.out.println("Coups ordinateur : " + coupsOrdinateur);
            System.out.println("Captures joueur : " + capturesJoueur);
            System.out.println("Captures ordinateur : " + capturesOrdinateur);
            System.out.println("Dames joueur : " + damesJoueur);
            System.out.println("Dames ordinateur : " + damesOrdinateur);
            System.out.println("Durée de la partie : " + dureeMs);

            pstmt.setString(1,currentEmail);
            pstmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setInt(3, scoreJoueur);
            pstmt.setInt(4, scoreOrdinateur);
            pstmt.setInt(5, coupsJoueur);
            pstmt.setInt(6, coupsOrdinateur);
            pstmt.setInt(7, capturesJoueur);
            pstmt.setInt(8, capturesOrdinateur);
            pstmt.setInt(9, damesJoueur);
            pstmt.setInt(10, damesOrdinateur);
            pstmt.setLong(11, dureeMs);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement dans la base : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private int compterDames(int joueur) {
        int count = 0;
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                Case c = plateau[i][j];
                if (c.piece == joueur && c.estDame) {
                    count++;
                }
            }
        }
        return count;
    }
    // Gestionnaire de clic de souris
    private class GestionnaireClic extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (jeuTermine || joueurActuel != 1) return;  // Ne pas réagir si ce n'est pas au tour du joueur
            
            int colonne = e.getX() / TAILLE_CASE;
            int ligne = e.getY() / TAILLE_CASE;
            
            if (ligne >= 0 && ligne < TAILLE && colonne >= 0 && colonne < TAILLE) {
                traiterClic(ligne, colonne);
            }
        }
    }
    
    // Traitement du clic
 // 1. Modification de traiterClic pour gérer les captures multiples
    private void traiterClic(int ligne, int colonne) {
        Case casecliquee = plateau[ligne][colonne];
        
        // Trouver toutes les captures possibles pour le joueur
        List<List<Mouvement>> capturesPossibles = trouverToutesCaptures(joueurActuel);
        boolean captureObligatoire = !capturesPossibles.isEmpty();
        
        // Si une pièce est déjà sélectionnée
        if (caseSelectionnee != null) {
            // Vérifier si le clic est sur un mouvement possible
            for (Mouvement mouvement : mouvementsPossibles) {
                if (mouvement.destination == casecliquee) {
                    // Vérifier si le mouvement est valide
                    boolean estCapture = mouvement.capture != null;
                    executerMouvement(mouvement, false); // Ne pas changer de joueur automatiquement
                    
                     
                 
                    if(joueurActuel==1) {
                    	coupsJoueur++;
                    }
                    else {
                    	coupsOrdinateur++;
                    }
                    
                    
                    // Mettre à jour les scores après chaque mouvement
                    calculerScores();
                    
                    // Vérifier les captures supplémentaires possibles avec la même pièce
                    if (estCapture) {
                        List<Mouvement> capturesSupplementaires = trouverMouvementsPossibles(casecliquee);
                        capturesSupplementaires.removeIf(m -> m.capture == null);
                        
                        if (!capturesSupplementaires.isEmpty()) {
                            // Il y a encore des captures possibles avec cette pièce
                            caseSelectionnee = casecliquee;
                            mouvementsPossibles = capturesSupplementaires;
                            statusBar.setText("Continuez la prise!");
                            repaint();
                            return;
                        }
                    }
                    
                    // Fin du tour du joueur (pas de captures supplémentaires)
                    caseSelectionnee = null;
                    mouvementsPossibles.clear();
                    
                    // Changer de joueur
                    joueurActuel = (joueurActuel == 1) ? 2 : 1;
                    
                    // Vérifier si le jeu est terminé
                    if (verifierFinDeJeu()) {
                        jeuTermine = true;
                        SwingUtilities.invokeLater(() -> afficherEcranFinDePartie());
                    } else {
                        // Tour de l'ordinateur
                        statusBar.setText("Tour de l'ordinateur...");
                        repaint();
                        
                        // Faire jouer l'ordinateur après un court délai
                        SwingUtilities.invokeLater(() -> {
                            try {
                                Thread.sleep(1000);  // Attendre 1 seconde
                                tourOrdinateur();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                    
                    return;
                }
            }
            
            // Si le clic n'est pas sur un mouvement valide, désélectionner
            caseSelectionnee = null;
            mouvementsPossibles.clear();
        }
        
        // Sélectionner une pièce si c'est une pièce du joueur
        if (casecliquee.piece == joueurActuel) {
            caseSelectionnee = casecliquee;
            
            // Montrer tous les mouvements possibles pour cette pièce
            mouvementsPossibles = trouverMouvementsPossibles(casecliquee);
        }
        
        repaint();
    }

    // 2. Modification de executerMouvement pour gérer le changement de tour optionnel
    private void executerMouvement(Mouvement mouvement, boolean changerJoueur) {
        Case source = mouvement.source;
        Case destination = mouvement.destination;
        
        // Déplacer la pièce
        destination.piece = source.piece;
        destination.estDame = source.estDame;
        source.piece = 0;
        source.estDame = false;

        // Supprimer toutes les pièces capturées
        if (mouvement.capturesMultiples != null && !mouvement.capturesMultiples.isEmpty()) {
            for (Case capture : mouvement.capturesMultiples) {
                capture.piece = 0;
                capture.estDame = false;

               
            }
        }

        // Si c'est un mouvement normal (sans capture multiple)
        else if (mouvement.capture != null) {
            mouvement.capture.piece = 0;
            mouvement.capture.estDame = false;

            
        }

       

        // Promotion en dame
        if ((destination.piece == 1 && destination.ligne == 0) ||
            (destination.piece == 2 && destination.ligne == TAILLE - 1)) {
            destination.estDame = true;
        }

        // Changer de joueur uniquement si demandé
        if (changerJoueur) {
            joueurActuel = (joueurActuel == 1) ? 2 : 1;
        }
    }

    // 3. Modification de tourOrdinateur pour gérer les captures multiples
    private void tourOrdinateur() {
        if (jeuTermine) return;
        
        boolean continuerTour = true;
        Case positionActuelle = null;
        
        while (continuerTour) {
            continuerTour = false;
            
            // Trouver toutes les captures possibles pour l'ordinateur
            List<List<Mouvement>> toutesCaptures = new ArrayList<>();
            
            if (positionActuelle == null) {
                // Premier mouvement du tour : trouver toutes les captures possibles
                toutesCaptures = trouverToutesCaptures(joueurActuel);
            } else {
                // Suite d'une capture : ne chercher que les captures depuis la position actuelle
                List<Mouvement> capturesSuivantes = trouverMouvementsPossibles(positionActuelle);
                capturesSuivantes.removeIf(m -> m.capture == null);
                if (!capturesSuivantes.isEmpty()) {
                    toutesCaptures.add(capturesSuivantes);
                }
            }
            
            // Si des captures sont possibles
            if (!toutesCaptures.isEmpty()) {
                // Déterminer la séquence avec le plus de captures
                int maxCaptures = 0;
                List<Mouvement> meilleureSequence = null;
                
                for (List<Mouvement> sequence : toutesCaptures) {
                    if (!sequence.isEmpty()) {
                        int captures = calculerNombreCaptures(sequence.get(0));
                        if (captures > maxCaptures) {
                            maxCaptures = captures;
                            meilleureSequence = sequence;
                        }
                    }
                }
                
                // Choisir une capture de la meilleure séquence
                if (meilleureSequence != null && !meilleureSequence.isEmpty()) {
                    int index = (int)(Math.random() * meilleureSequence.size());
                    Mouvement meilleurMouvement = meilleureSequence.get(index);
                    
                    coupsOrdinateur++;
                    
                    // Exécuter le mouvement sans changer de joueur
                    executerMouvement(meilleurMouvement, false);
                    
                    // Mettre à jour la position actuelle
                    positionActuelle = meilleurMouvement.destination;
                    
                    // Vérifier s'il y a des captures supplémentaires possibles
                    List<Mouvement> capturesSuivantes = trouverMouvementsPossibles(positionActuelle);
                    capturesSuivantes.removeIf(m -> m.capture == null);
                    
                    if (!capturesSuivantes.isEmpty()) {
                        continuerTour = true;
                        repaint();
                        try {
                            Thread.sleep(4500); // Pause courte entre les captures
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (positionActuelle == null) {
                // Pas de capture possible et premier mouvement : faire un mouvement normal
                List<Mouvement> mouvementsNormaux = new ArrayList<>();
                
                for (int ligne = 0; ligne < TAILLE; ligne++) {
                    for (int colonne = 0; colonne < TAILLE; colonne++) {
                        Case caseActuelle = plateau[ligne][colonne];
                        if (caseActuelle.piece == joueurActuel) {
                            List<Mouvement> mouvements = trouverMouvementsPossibles(caseActuelle);
                            mouvementsNormaux.addAll(mouvements);
                        }
                    }
                }
                
                if (!mouvementsNormaux.isEmpty()) {
                    int index = (int)(Math.random() * mouvementsNormaux.size());
                    
                    executerMouvement(mouvementsNormaux.get(index), false);
                } else {
                    // L'ordinateur ne peut pas jouer
                    jeuTermine = true;
                    SwingUtilities.invokeLater(() -> afficherEcranFinDePartie());
                    repaint();
                    return;
                }
            }
        }
        
        // Fin du tour de l'ordinateur
        joueurActuel = 1; // Passer au joueur humain
        
        // Mettre à jour les scores après le mouvement de l'ordinateur  
        calculerScores();
        
        // Vérifier si le jeu est terminé
        if (verifierFinDeJeu()) {
            jeuTermine = true;
            SwingUtilities.invokeLater(() -> afficherEcranFinDePartie());
        } else {
            statusBar.setText("À votre tour");
        }
        
        repaint();
    }

    private List<Mouvement> trouverMouvementsPossibles(Case source) {
        List<Mouvement> mouvements = new ArrayList<>();
        List<Mouvement> captures = new ArrayList<>();
        int joueur = source.piece;
        
        if (joueur == 0) return mouvements;  // Case vide
        
        // Directions de déplacement (dépend du joueur sauf pour les dames)
        int[] directions;
        if (source.estDame) {
            directions = new int[]{-1, 1};  // Les dames peuvent aller dans les deux directions
        } else if (joueur == 1) {
            directions = new int[]{-1};  // Joueur: vers le haut
        } else {
            directions = new int[]{1};   // Ordinateur: vers le bas
        }
        
        // Vérifier les captures d'abord
        for (int dirLigne : directions) {
            for (int dirColonne : new int[]{-1, 1}) {  // Diagonales gauche et droite
                if (source.estDame) {
                    // Les dames peuvent capturer à distance
                    for (int distance = 1; distance < TAILLE; distance++) {
                        int nouvelleLigne = source.ligne + dirLigne * distance;
                        int nouvelleColonne = source.colonne + dirColonne * distance;
                        
                        if (!estDansPlateau(nouvelleLigne, nouvelleColonne)) break;
                        
                        Case caseIntermediaire = plateau[nouvelleLigne][nouvelleColonne];
                        if (caseIntermediaire.piece == 0) continue;  // Case vide, continuer
                        
                        if (caseIntermediaire.piece != joueur) {  // Pièce adverse
                            // Chercher une case libre après la pièce adverse
                            for (int sautDistance = 1; sautDistance < TAILLE; sautDistance++) {
                                int ligneSaut = nouvelleLigne + dirLigne * sautDistance;
                                int colonneSaut = nouvelleColonne + dirColonne * sautDistance;
                                
                                if (!estDansPlateau(ligneSaut, colonneSaut)) break;
                                
                                if (plateau[ligneSaut][colonneSaut].piece == 0) {
                                    // Capture possible
                                    captures.add(new Mouvement(source, plateau[ligneSaut][colonneSaut], caseIntermediaire));
                                } else {
                                    break; // Une pièce bloque le chemin
                                }
                            }
                        }
                        
                        break;  // Arrêter après avoir rencontré une pièce
                    }
                } else {
                    // Pièces normales: capture à distance 1
                    int nouvelleLigne = source.ligne + dirLigne * 1;
                    int nouvelleColonne = source.colonne + dirColonne * 1;
                    
                    if (estDansPlateau(nouvelleLigne, nouvelleColonne)) {
                        Case caseVoisine = plateau[nouvelleLigne][nouvelleColonne];
                        
                        if (caseVoisine.piece != 0 && caseVoisine.piece != joueur) {
                            int ligneSaut = nouvelleLigne + dirLigne;
                            int colonneSaut = nouvelleColonne + dirColonne;
                            
                            if (estDansPlateau(ligneSaut, colonneSaut) && 
                                plateau[ligneSaut][colonneSaut].piece == 0) {
                                // Capture possible
                                captures.add(new Mouvement(source, plateau[ligneSaut][colonneSaut], caseVoisine));
                            }
                        }
                    }
                }
            }
        }
        
        // Si des captures sont possibles, ignorer les déplacements simples
        if (!captures.isEmpty()) return captures;
        
        // Déplacements simples (seulement si aucune capture n'est possible)
        for (int dirLigne : directions) {
            for (int dirColonne : new int[]{-1, 1}) {
                if (source.estDame) {
                    // Déplacements de dame (distance variable)
                    for (int distance = 1; distance < TAILLE; distance++) {
                        int nouvelleLigne = source.ligne + dirLigne * distance;
                        int nouvelleColonne = source.colonne + dirColonne * distance;
                        
                        if (!estDansPlateau(nouvelleLigne, nouvelleColonne)) break;
                        
                        Case destination = plateau[nouvelleLigne][nouvelleColonne];
                        if (destination.piece == 0) {
                            mouvements.add(new Mouvement(source, destination, null));
                        } else {
                            break;  // Arrêter au premier obstacle
                        }
                    }
                } else {
                    // Déplacement normal (distance 1)
                    int nouvelleLigne = source.ligne + dirLigne;
                    int nouvelleColonne = source.colonne + dirColonne;
                    
                    if (estDansPlateau(nouvelleLigne, nouvelleColonne)) {
                        Case destination = plateau[nouvelleLigne][nouvelleColonne];
                        if (destination.piece == 0) {
                            mouvements.add(new Mouvement(source, destination, null));
                        }
                    }
                }
            }
        }
        
        return mouvements;
    }
    private List<List<Mouvement>> trouverToutesCaptures(int joueur) {
        List<List<Mouvement>> toutesCaptures = new ArrayList<>();
        
        // Trouver les pièces du joueur actuel qui peuvent capturer
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                Case caseActuelle = plateau[ligne][colonne];
                if (caseActuelle.piece == joueur) {
                    List<Mouvement> captures = trouverMouvementsPossibles(caseActuelle);
                    // Ne garder que les mouvements de capture
                    captures.removeIf(m -> m.capture == null);
                    
                    if (!captures.isEmpty()) {
                        toutesCaptures.add(captures);
                    }
                }
            }
        }
        
        return toutesCaptures;
    }

private int calculerNombreCaptures(Mouvement mouvement) {
    // Si pas de capture, retourne 0
    if (mouvement.capture == null) return 0;
    
    // Simulation du mouvement pour calculer les captures suivantes
    Case source = mouvement.source;
    Case destination = mouvement.destination;
    Case capture = mouvement.capture;
    
    // Sauvegarder l'état avant la simulation
    int pieceCaptureOriginal = capture.piece;
    boolean estDameCaptureOriginal = capture.estDame;
    int pieceSourceOriginal = source.piece;
    boolean estDameSourceOriginal = source.estDame;
    
    // Simuler le mouvement
    destination.piece = source.piece;
    destination.estDame = source.estDame;
    source.piece = 0;
    source.estDame = false;
    capture.piece = 0;
    capture.estDame = false;
    
    // Calculer les captures possibles à partir de la nouvelle position
    List<Mouvement> capturesSuivantes = trouverMouvementsPossibles(destination);
    capturesSuivantes.removeIf(m -> m.capture == null);
    
    int maxCaptures = 1; // Déjà une capture avec ce mouvement
    
    // Trouver le maximum de captures possibles
    for (Mouvement m : capturesSuivantes) {
        int captures = 1 + calculerNombreCaptures(m);
        maxCaptures = Math.max(maxCaptures, captures);
    }
    
    // Restaurer l'état original
    capture.piece = pieceCaptureOriginal;
    capture.estDame = estDameCaptureOriginal;
    source.piece = pieceSourceOriginal;
    source.estDame = estDameSourceOriginal;
    destination.piece = 0;  
    destination.estDame = false;
    
    return maxCaptures;
}
    // Vérifier si la position est dans le plateau
    private boolean estDansPlateau(int ligne, int colonne) {
        return ligne >= 0 && ligne < TAILLE && colonne >= 0 && colonne < TAILLE;
    }
    
    // Vérifier si le jeu est terminé
    private boolean verifierFinDeJeu() {
    	
        boolean joueur1Existe = false;
        boolean joueur2Existe = false;
        
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                int piece = plateau[ligne][colonne].piece;
                if (piece == 1) joueur1Existe = true;
                if (piece == 2) joueur2Existe = true;
                
                // Si les deux types de pièces existent encore, le jeu continue
                if (joueur1Existe && joueur2Existe) return false;
            }
        }
        
        return true;  // Une des deux joueurs n'a plus de pièces
    }
    

    

   
}