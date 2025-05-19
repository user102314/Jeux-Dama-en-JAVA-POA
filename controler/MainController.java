package controler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;
import damier.JeuDeDames;
import model.utilisateurDAO;
import util.EtatPartie;
import vue.ChargerPartieView;
import vue.MainView;
import vue.ModificationProfilView;
import vue.StatistiquesView;

public class MainController {
    private MainView view;
    private utilisateurDAO userDao;
    private String currentEmail;

    public MainController(MainView view, String userEmail) {
        this.view = view;
        this.currentEmail = userEmail;
        this.userDao = new utilisateurDAO();
        
        attachListeners();
    }

    private void attachListeners() {
        // Nouvelle partie
        view.addNouvellePartieListener(e -> lancerJeuDeDames());
        
        // Reprendre
        view.addReprendreListener(e -> chargerPartie());//voici l'apelle de la fonction 
        
        // Statistiques
        view.addStatistiquesListener(e -> {new StatistiquesView(currentEmail);});
        
        // Modifier profil
        view.addModifierProfilListener(e -> modifierProfilUtilisateur());
        
        // Quitter
        view.addQuitterJeuListener(e -> quitterApplication());
        
    }
    private void chargerPartie() {
        new ChargerPartieView(currentEmail, view);
    }

    private void lancerJeuDeDames() {
        view.dispose();
        JeuDeDames jeu = new JeuDeDames(currentEmail);
        jeu.setVisible(true);
    }
    
    
    private void modifierProfilUtilisateur() {
        try {
            ResultSet user = userDao.getUser(currentEmail);
            if (user.next()) {
                ModificationProfilView modifView = new ModificationProfilView(
                    currentEmail,
                    user.getString("nom"),
                    user.getString("prenom")
                );
                modifView.addRetourListener(e ->{
                	modifView.dispose();
                });
                
                modifView.addSauvegarderListener(e -> {
                    String newMdp = modifView.getMdp();
                    String newNom = modifView.getNom();
                    String newPrenom = modifView.getPrenom();
                    
                    if (newMdp.isEmpty()) {
                        try {
							newMdp = user.getString("mdp");
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    }
                    
                    if (userDao.updateUser(currentEmail, newMdp, newNom, newPrenom)) {
                        modifView.showMessage("Profil mis à jour!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        modifView.dispose();
                    } else {
                        modifView.showMessage("Échec de la mise à jour", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                modifView.setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        }
    }

    private void quitterApplication() {
        int confirm = JOptionPane.showConfirmDialog(
            view, 
            "Voulez-vous vraiment quitter?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    

   
}