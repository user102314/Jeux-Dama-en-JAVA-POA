package damier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class JeuDeDames extends JFrame {
    
    private static final int TAILLE = 10;
    private static final int TAILLE_CASE = 70;
    
    private Case[][] plateau = new Case[TAILLE][TAILLE];
    private int joueurActuel = 1; // 1 = joueur, 2 = ordinateur
    private Case caseSelectionnee = null;
    private List<Mouvement> mouvementsPossibles = new ArrayList<>();
    private JLabel statusBar;
    private boolean jeuTermine = false;
    
    private int scoreJoueur = 0;
    private int scoreOrdinateur = 0;
    private JLabel scoreJoueurLabel;
    private JLabel scoreOrdinateurLabel;
    private JPanel scorePanel;
    
    private static final int POINTS_PION = 1;
    private static final int POINTS_DAME = 3;
    
    public JeuDeDames() {
        setTitle("Jeu de Dames");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel plateauPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerPlateau(g);
            }
        };
        plateauPanel.setPreferredSize(new Dimension(TAILLE * TAILLE_CASE, TAILLE * TAILLE_CASE));
        plateauPanel.addMouseListener(new GestionnaireClic());
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        
        statusBar = new JLabel("À votre tour");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setHorizontalAlignment(JLabel.CENTER);
        statusBar.setFont(new Font("Arial", Font.BOLD, 14));
        
        scorePanel = new JPanel(new GridLayout(1, 2));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Score"));
        
        JPanel joueurScorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        joueurScorePanel.add(new JLabel("Vous: "));
        scoreJoueurLabel = new JLabel("0");
        scoreJoueurLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreJoueurLabel.setForeground(Color.RED);
        joueurScorePanel.add(scoreJoueurLabel);
        
        JPanel ordinateurScorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ordinateurScorePanel.add(new JLabel("Ordinateur: "));
        scoreOrdinateurLabel = new JLabel("0");
        scoreOrdinateurLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreOrdinateurLabel.setForeground(Color.BLUE);
        ordinateurScorePanel.add(scoreOrdinateurLabel);
        
        scorePanel.add(joueurScorePanel);
        scorePanel.add(ordinateurScorePanel);
        
        infoPanel.add(statusBar, BorderLayout.NORTH);
        infoPanel.add(scorePanel, BorderLayout.CENTER);
        
        JButton nouvellePartie = new JButton("Nouvelle Partie");
        nouvellePartie.addActionListener(e -> initialiserJeu());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nouvellePartie);
        
        mainPanel.add(plateauPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        initialiserJeu();
    }
    
    private void initialiserJeu() {
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                plateau[ligne][colonne] = new Case(ligne, colonne);
                
                if ((ligne + colonne) % 2 == 1) {
                    if (ligne < 4) {
                        plateau[ligne][colonne].piece = 2;
                    } else if (ligne >= TAILLE - 4) {
                        plateau[ligne][colonne].piece = 1;
                    }
                }
            }
        }
        
        joueurActuel = 1;
        caseSelectionnee = null;
        mouvementsPossibles.clear();
        jeuTermine = false;
        
        scoreJoueur = 0;
        scoreOrdinateur = 0;
        mettreAJourScoreLabel();
        
        statusBar.setText("À votre tour");
        repaint();
    }
    
    private void dessinerPlateau(Graphics g) {
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                if ((ligne + colonne) % 2 == 0) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(new Color(50, 50, 50));
                }
                g.fillRect(colonne * TAILLE_CASE, ligne * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
                
                Case currentCase = plateau[ligne][colonne];
                if (currentCase.piece > 0) {
                    if (currentCase.piece == 1) {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(Color.GRAY);
                    }
                    
                    g.fillOval(
                        colonne * TAILLE_CASE + 10,
                        ligne * TAILLE_CASE + 10,
                        TAILLE_CASE - 20,
                        TAILLE_CASE - 20
                    );
                    
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
                
                if (caseSelectionnee == currentCase) {
                    g.setColor(Color.YELLOW);
                    g.drawRect(
                        colonne * TAILLE_CASE + 2,
                        ligne * TAILLE_CASE + 2,
                        TAILLE_CASE - 4,
                        TAILLE_CASE - 4
                    );
                }
                
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
    
    public void executerMouvement(Mouvement mouvement, boolean changerJoueur) {
        Case source = mouvement.source;
        Case destination = mouvement.destination;
        
        destination.piece = source.piece;
        destination.estDame = source.estDame;
        source.piece = 0;
        source.estDame = false;
        
        if (mouvement.capture != null) {
            mouvement.capture.piece = 0;
            mouvement.capture.estDame = false;
        }
        
        if ((destination.piece == 1 && destination.ligne == 0) ||
            (destination.piece == 2 && destination.ligne == TAILLE - 1)) {
            destination.estDame = true;
        }
        
        if (changerJoueur) {
            joueurActuel = (joueurActuel == 1) ? 2 : 1;
        }
    }
    
    private void mettreAJourScoreLabel() {
        scoreJoueurLabel.setText(String.valueOf(scoreJoueur));
        scoreOrdinateurLabel.setText(String.valueOf(scoreOrdinateur));
    }
    
    private void calculerScores() {
        scoreJoueur = 0;
        scoreOrdinateur = 0;
        
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                Case c = plateau[ligne][colonne];
                if (c.piece == 1) {
                    scoreJoueur += c.estDame ? POINTS_DAME : POINTS_PION;
                } else if (c.piece == 2) {
                    scoreOrdinateur += c.estDame ? POINTS_DAME : POINTS_PION;
                }
            }
        }
        
        mettreAJourScoreLabel();
    }
    
    private void afficherEcranFinDePartie() {
        JDialog finPartie = new JDialog(this, "Fin de partie", true);
        finPartie.setSize(400, 300);
        finPartie.setLocationRelativeTo(this);
        finPartie.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        String message;
        Color couleurFond;
        
        if (scoreJoueur > scoreOrdinateur) {
            message = "VOUS AVEZ GAGNÉ !";
            couleurFond = new Color(200, 255, 200);
        } else if (scoreOrdinateur > scoreJoueur) {
            message = "VOUS AVEZ PERDU";
            couleurFond = new Color(255, 200, 200);
        } else {
            message = "MATCH NUL";
            couleurFond = new Color(230, 230, 230);
        }
        
        panel.setBackground(couleurFond);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 28));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(messageLabel, BorderLayout.NORTH);
        
        JLabel scoreResultat = new JLabel("Score final: Vous " + scoreJoueur + " - Ordinateur " + scoreOrdinateur);
        scoreResultat.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreResultat.setHorizontalAlignment(JLabel.CENTER);
        panel.add(scoreResultat, BorderLayout.CENTER);
        
        JPanel boutonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        boutonPanel.setOpaque(false);
        
        JButton rejouerBtn = new JButton("Rejouer");
        rejouerBtn.setFont(new Font("Arial", Font.BOLD, 16));
        rejouerBtn.setPreferredSize(new Dimension(120, 40));
        rejouerBtn.addActionListener(e -> {
            finPartie.dispose();
            initialiserJeu();
        });
        
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
    
    private class GestionnaireClic extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (jeuTermine || joueurActuel != 1) return;
            
            int colonne = e.getX() / TAILLE_CASE;
            int ligne = e.getY() / TAILLE_CASE;
            
            if (ligne >= 0 && ligne < TAILLE && colonne >= 0 && colonne < TAILLE) {
                traiterClic(ligne, colonne);
            }
        }
    }
    
    private void traiterClic(int ligne, int colonne) {
        Case casecliquee = plateau[ligne][colonne];
        
        List<List<Mouvement>> capturesPossibles = trouverToutesCaptures(joueurActuel);
        boolean captureObligatoire = !capturesPossibles.isEmpty();
        
        if (caseSelectionnee != null) {
            for (Mouvement mouvement : mouvementsPossibles) {
                if (mouvement.destination == casecliquee) {
                    boolean estCapture = mouvement.capture != null;
                    executerMouvement(mouvement, false);
                    
                    calculerScores();
                    
                    if (estCapture) {
                        List<Mouvement> capturesSupplementaires = trouverMouvementsPossibles(casecliquee);
                        capturesSupplementaires.removeIf(m -> m.capture == null);
                        
                        if (!capturesSupplementaires.isEmpty()) {
                            caseSelectionnee = casecliquee;
                            mouvementsPossibles = capturesSupplementaires;
                            statusBar.setText("Continuez la prise!");
                            repaint();
                            return;
                        }
                    }
                    
                    caseSelectionnee = null;
                    mouvementsPossibles.clear();
                    joueurActuel = 2;
                    
                    if (verifierFinDeJeu()) {
                        jeuTermine = true;
                        SwingUtilities.invokeLater(() -> afficherEcranFinDePartie());
                    } else {
                        statusBar.setText("Tour de l'ordinateur...");
                        repaint();
                        
                        SwingUtilities.invokeLater(() -> {
                            try {
                                Thread.sleep(1000);
                                tourOrdinateur();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                    
                    return;
                }
            }
            
            caseSelectionnee = null;
            mouvementsPossibles.clear();
        }
        
        if (casecliquee.piece == joueurActuel) {
            caseSelectionnee = casecliquee;
            mouvementsPossibles = trouverMouvementsPossibles(casecliquee);
        }
        
        repaint();
    }
    
    private void tourOrdinateur() {
        new Ordinateur(this, plateau, TAILLE).jouerTour();
    }
    
    public void finTourOrdinateur() {
        joueurActuel = 1;
        calculerScores();
        
        if (verifierFinDeJeu()) {
            jeuTermine = true;
            SwingUtilities.invokeLater(() -> afficherEcranFinDePartie());
        } else {
            statusBar.setText("À votre tour");
        }
        repaint();
    }
    
    public boolean estJeuTermine() {
        return jeuTermine;
    }
    
    public void setJeuTermine(boolean termine) {
        this.jeuTermine = termine;
    }
    
    private List<Mouvement> trouverMouvementsPossibles(Case source) {
        List<Mouvement> mouvements = new ArrayList<>();
        List<Mouvement> captures = new ArrayList<>();
        int joueur = source.piece;
        
        if (joueur == 0) return mouvements;
        
        int[] directions;
        if (source.estDame) {
            directions = new int[]{-1, 1};
        } else if (joueur == 1) {
            directions = new int[]{-1};
        } else {
            directions = new int[]{1};
        }
        
        for (int dirLigne : directions) {
            for (int dirColonne : new int[]{-1, 1}) {
                if (source.estDame) {
                    for (int distance = 1; distance < TAILLE; distance++) {
                        int nouvelleLigne = source.ligne + dirLigne * distance;
                        int nouvelleColonne = source.colonne + dirColonne * distance;
                        
                        if (!estDansPlateau(nouvelleLigne, nouvelleColonne)) break;
                        
                        Case caseIntermediaire = plateau[nouvelleLigne][nouvelleColonne];
                        if (caseIntermediaire.piece == 0) continue;
                        
                        if (caseIntermediaire.piece != joueur) {
                            for (int sautDistance = 1; sautDistance < TAILLE; sautDistance++) {
                                int ligneSaut = nouvelleLigne + dirLigne * sautDistance;
                                int colonneSaut = nouvelleColonne + dirColonne * sautDistance;
                                
                                if (!estDansPlateau(ligneSaut, colonneSaut)) break;
                                
                                if (plateau[ligneSaut][colonneSaut].piece == 0) {
                                    captures.add(new Mouvement(source, plateau[ligneSaut][colonneSaut], caseIntermediaire));
                                } else {
                                    break;
                                }
                            }
                        }
                        break;
                    }
                } else {
                    int nouvelleLigne = source.ligne + dirLigne * 1;
                    int nouvelleColonne = source.colonne + dirColonne * 1;
                    
                    if (estDansPlateau(nouvelleLigne, nouvelleColonne)) {
                        Case caseVoisine = plateau[nouvelleLigne][nouvelleColonne];
                        
                        if (caseVoisine.piece != 0 && caseVoisine.piece != joueur) {
                            int ligneSaut = nouvelleLigne + dirLigne;
                            int colonneSaut = nouvelleColonne + dirColonne;
                            
                            if (estDansPlateau(ligneSaut, colonneSaut) && 
                                plateau[ligneSaut][colonneSaut].piece == 0) {
                                captures.add(new Mouvement(source, plateau[ligneSaut][colonneSaut], caseVoisine));
                            }
                        }
                    }
                }
            }
        }
        
        if (!captures.isEmpty()) return captures;
        
        for (int dirLigne : directions) {
            for (int dirColonne : new int[]{-1, 1}) {
                if (source.estDame) {
                    for (int distance = 1; distance < TAILLE; distance++) {
                        int nouvelleLigne = source.ligne + dirLigne * distance;
                        int nouvelleColonne = source.colonne + dirColonne * distance;
                        
                        if (!estDansPlateau(nouvelleLigne, nouvelleColonne)) break;
                        
                        Case destination = plateau[nouvelleLigne][nouvelleColonne];
                        if (destination.piece == 0) {
                            mouvements.add(new Mouvement(source, destination, null));
                        } else {
                            break;
                        }
                    }
                } else {
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
        
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                Case caseActuelle = plateau[ligne][colonne];
                if (caseActuelle.piece == joueur) {
                    List<Mouvement> captures = trouverMouvementsPossibles(caseActuelle);
                    captures.removeIf(m -> m.capture == null);
                    
                    if (!captures.isEmpty()) {
                        toutesCaptures.add(captures);
                    }
                }
            }
        }
        
        return toutesCaptures;
    }
    
    private boolean estDansPlateau(int ligne, int colonne) {
        return ligne >= 0 && ligne < TAILLE && colonne >= 0 && colonne < TAILLE;
    }
    
    private boolean verifierFinDeJeu() {
        boolean joueur1Existe = false;
        boolean joueur2Existe = false;
        
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                int piece = plateau[ligne][colonne].piece;
                if (piece == 1) joueur1Existe = true;
                if (piece == 2) joueur2Existe = true;
                
                if (joueur1Existe && joueur2Existe) return false;
            }
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JeuDeDames jeu = new JeuDeDames();
            jeu.setVisible(true);
        });
    }
}
