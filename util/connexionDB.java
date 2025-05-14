package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/JeuxDeTable"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "";
    private Connection connection;

    public connexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion réussie à la base de données");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Pilote JDBC introuvable !");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur : Problème lors de la connexion à la base de données");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée avec succès");
            }
        } catch (SQLException e) {
            System.err.println("Erreur : Impossible de fermer la connexion");
            e.printStackTrace();
        }
    }
}
