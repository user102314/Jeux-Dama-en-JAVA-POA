package model;

import java.sql.*;

import util.connexionDB;

public class utilisateurDAO {

    private final connexionDB db;

    public utilisateurDAO() {
        this.db = new connexionDB();
    }

    public ResultSet getUser(String email) {
        try {
            Connection conn = db.getConnection();
            String query = "SELECT * FROM utilisateur WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insertUser(String email, String mdp, String nom, String prenom) {
        try {
            ResultSet user = getUser(email);
            if (user != null && user.next()) {
                System.out.println("Utilisateur déjà existant");
                return false;
            }

            Connection conn = db.getConnection();
            String query = "INSERT INTO utilisateur (email, mdp, nom, prenom) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, mdp);
            stmt.setString(3, nom);
            stmt.setString(4, prenom);
            stmt.executeUpdate();
            System.out.println("Utilisateur inséré avec succès");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateUser(String email, String newMdp, String newNom, String newPrenom) {
        try {
            Connection conn = db.getConnection();
            String query = "UPDATE utilisateur SET mdp = ?, nom = ?, prenom = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newMdp);
            stmt.setString(2, newNom);
            stmt.setString(3, newPrenom);
            stmt.setString(4, email);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
