package controler;

import model.utilisateurDAO;
import vue.AuthFrame;
import vue.InscriFrame;
import vue.MainView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class utilisateurControler {
	
    private final AuthFrame authFrame;
    private final InscriFrame inscriFrame;
    private final utilisateurDAO utilisateurDao;

    public utilisateurControler(AuthFrame authFrame, InscriFrame inscriFrame) {
        this.authFrame = authFrame;
        this.inscriFrame = inscriFrame;
        this.utilisateurDao = new utilisateurDAO();

        // Configuration des écouteurs pour l'interface d'authentification
        this.authFrame.addLoginListener(new LoginListener());
        this.authFrame.addQuitListener(e -> System.exit(0));
        this.authFrame.addGoToInscriListener(e -> showInscriptionFrame());

        // Configuration des écouteurs pour l'interface d'inscription
        this.inscriFrame.addInscriptionListener(new InscriptionListener());
        this.inscriFrame.addQuitterListener(e -> {
            inscriFrame.setVisible(false);
            authFrame.setVisible(true);
        });

        // Affichage initial
        authFrame.setVisible(true);
        inscriFrame.setVisible(false);
    }

    private void showInscriptionFrame() {
        authFrame.setVisible(false);
        inscriFrame.setVisible(true);
        // Réinitialisation des champs
        inscriFrame.setEmail("");
        inscriFrame.setPassword("");
        inscriFrame.setNom("");
        inscriFrame.setPrenom("");
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = authFrame.getEmail();
            String password = authFrame.getPassword();

            if (email.isEmpty() || password.isEmpty()) {
                authFrame.showMessage("Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ResultSet user = utilisateurDao.getUser(email);
                if (user != null && user.next()) {
                    String storedPassword = user.getString("mdp");
                    if (storedPassword.equals(password)) {
                        authFrame.showMessage("Connexion réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Fermer la fenêtre d'authentification
                        authFrame.dispose();
                        
                        // Lancer la MainView dans l'EDT (Event Dispatch Thread)
                        SwingUtilities.invokeLater(() -> {
                            MainView mainView = new MainView();
                            new MainController(mainView, email); // Associer le contrôleur
                            mainView.setVisible(true);
                        });
                        
                    } else {
                        authFrame.showMessage("Mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    authFrame.showMessage("Utilisateur introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                authFrame.showMessage("Erreur de base de données: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private class InscriptionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = inscriFrame.getEmail();
            String password = inscriFrame.getPassword();
            String nom = inscriFrame.getNom();
            String prenom = inscriFrame.getPrenom();

            if (email.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                inscriFrame.showMessage("Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validation de l'email
            if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                inscriFrame.showMessage("Format d'email invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validation du mot de passe (au moins 6 caractères)
            if (password.length() < 6) {
                inscriFrame.showMessage("Le mot de passe doit contenir au moins 6 caractères", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ResultSet existingUser = utilisateurDao.getUser(email);
                if (existingUser != null && existingUser.next()) {
                    inscriFrame.showMessage("Cet email est déjà utilisé", "Erreur", JOptionPane.ERROR_MESSAGE);
                } else {
                    utilisateurDao.insertUser(email, password, nom, prenom);
                    inscriFrame.showMessage("Inscription réussie! Vous pouvez maintenant vous connecter.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    showInscriptionFrame(); // Retour à l'écran de connexion
                }
            } catch (SQLException ex) {
                inscriFrame.showMessage("Erreur de base de données: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}