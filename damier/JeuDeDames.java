package damier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class JeuDeDames extends JFrame {
    
    private static final int TAILLE = 10;  // Taille du plateau (10x10)
    private static final int TAILLE_CASE = 70;  // Taille d'une case en pixels
    
    private Case[][] plateau = new Case[TAILLE][TAILLE];
    private int joueurActuel = 1;  // 1 = joueur, 2 = ordinateur
    private Case caseSelectionnee = null;
    private List<Mouvement> mouvementsPossibles = new ArrayList<>();
    private JLabel statusBar;
    private boolean jeuTermine = false;
    
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
    public JeuDeDames() {
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
        
        // Ajout des composants
        mainPanel.add(plateauPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        initialiserJeu();
    }
    
    // Initialisation du jeu
    private void initialiserJeu() {
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
        JButton quitterBtn = new JButton("Quitter");
        quitterBtn.setFont(new Font("Arial", Font.BOLD, 16));
        quitterBtn.setPreferredSize(new Dimension(120, 40));
        quitterBtn.addActionListener(e -> System.exit(0));
        
        boutonPanel.add(rejouerBtn);
        boutonPanel.add(quitterBtn);
        
        panel.add(boutonPanel, BorderLayout.SOUTH);
        
        finPartie.add(panel);
        finPartie.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        finPartie.setVisible(true);
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
    private void traiterClic(int ligne, int colonne) {
        Case casecliquee = plateau[ligne][colonne];
        
        // Si une pièce est déjà sélectionnée
        if (caseSelectionnee != null) {
            // Vérifier si le clic est sur un mouvement possible
            for (Mouvement mouvement : mouvementsPossibles) {
                if (mouvement.destination == casecliquee) {
                    // Exécuter le mouvement
                    executerMouvement(mouvement);
                    caseSelectionnee = null;
                    mouvementsPossibles.clear();
                    
                    // Mettre à jour les scores après chaque mouvement
                    calculerScores();
                    
                    // Vérifier si le jeu est terminé
                    if (verifierFinDeJeu()) {
                        jeuTermine = true;
                        // Utiliser SwingUtilities.invokeLater pour éviter les problèmes de thread
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
            mouvementsPossibles = trouverMouvementsPossibles(casecliquee);
        }
        
        repaint();
    }
    
    // Exécuter un mouvement
    private void executerMouvement(Mouvement mouvement) {
        Case source = mouvement.source;
        Case destination = mouvement.destination;
        
        // Déplacer la pièce
        destination.piece = source.piece;
        destination.estDame = source.estDame;
        source.piece = 0;
        source.estDame = false;
        
        // Supprimer les pièces capturées
        if (mouvement.capture != null) {
            mouvement.capture.piece = 0;
            mouvement.capture.estDame = false;
        }
        
        // Promotion en dame
        if ((destination.piece == 1 && destination.ligne == 0) ||
            (destination.piece == 2 && destination.ligne == TAILLE - 1)) {
            destination.estDame = true;
        }
        
        // Changer de joueur
        joueurActuel = (joueurActuel == 1) ? 2 : 1;
    }
    
    // Tour de l'ordinateur
    private void tourOrdinateur() {
        if (jeuTermine) return;
        
        // Trouver tous les mouvements possibles pour l'ordinateur
        List<Mouvement> tousLesMouvements = new ArrayList<>();
        List<Mouvement> mouvementsDeCapture = new ArrayList<>();
        
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                Case caseActuelle = plateau[ligne][colonne];
                if (caseActuelle.piece == joueurActuel) {
                    List<Mouvement> mouvements = trouverMouvementsPossibles(caseActuelle);
                    tousLesMouvements.addAll(mouvements);
                    
                    // Séparer les mouvements de capture
                    for (Mouvement m : mouvements) {
                        if (m.capture != null) {
                            mouvementsDeCapture.add(m);
                        }
                    }
                }
            }
        }
        
        // Choisir un mouvement (priorité aux captures)
        if (!mouvementsDeCapture.isEmpty()) {
            // Choisir une capture aléatoire
            int index = (int)(Math.random() * mouvementsDeCapture.size());
            executerMouvement(mouvementsDeCapture.get(index));
        } else if (!tousLesMouvements.isEmpty()) {
            // Choisir un mouvement aléatoire
            int index = (int)(Math.random() * tousLesMouvements.size());
            executerMouvement(tousLesMouvements.get(index));
        } else {
            // L'ordinateur ne peut pas jouer
            jeuTermine = true;
            SwingUtilities.invokeLater(() -> afficherEcranFinDePartie());
            repaint();
            return;
        }
        
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
    
    // Trouver les mouvements possibles pour une pièce
    private List<Mouvement> trouverMouvementsPossibles(Case source) {
        List<Mouvement> mouvements = new ArrayList<>();
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
        
        boolean capturePossible = false;
        
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
                            int ligneSaut = nouvelleLigne + dirLigne;
                            int colonneSaut = nouvelleColonne + dirColonne;
                            
                            if (estDansPlateau(ligneSaut, colonneSaut) && 
                                plateau[ligneSaut][colonneSaut].piece == 0) {
                                // Capture possible
                                mouvements.add(new Mouvement(source, plateau[ligneSaut][colonneSaut], caseIntermediaire));
                                capturePossible = true;
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
                                mouvements.add(new Mouvement(source, plateau[ligneSaut][colonneSaut], caseVoisine));
                                capturePossible = true;
                            }
                        }
                    }
                }
            }
        }
        
        // Si des captures sont possibles, ignorer les déplacements simples
        if (capturePossible) return mouvements;
        
        // Déplacements simples
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
    
    // Classe représentant une case du plateau
    private class Case {
        int ligne, colonne;
        int piece;  // 0 = vide, 1 = joueur, 2 = ordinateur
        boolean estDame;
        
        public Case(int ligne, int colonne) {
            this.ligne = ligne;
            this.colonne = colonne;
            this.piece = 0;
            this.estDame = false;
        }
    }
    
    // Classe représentant un mouvement
    private class Mouvement {
        Case source;
        Case destination;
        Case capture;  // La pièce capturée (null si pas de capture)
        
        public Mouvement(Case source, Case destination, Case capture) {
            this.source = source;
            this.destination = destination;
            this.capture = capture;
        }
    }
    
    // Point d'entrée du programme
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JeuDeDames jeu = new JeuDeDames();
            jeu.setVisible(true);
        });
    }
}