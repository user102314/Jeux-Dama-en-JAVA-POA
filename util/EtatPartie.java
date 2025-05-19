package util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import model.Case;

import java.io.Serializable;
import java.util.UUID;

public class EtatPartie implements Serializable {
    private UUID id;
    private Case[][] plateau;
    private int joueurActuel;
    private boolean jeuTermine;
    private String emailUtilisateur;
    private long dateSauvegarde;
    
    public EtatPartie(Case[][] plateau, int joueurActuel, boolean jeuTermine, String emailUtilisateur) {
        this.id = UUID.randomUUID();
        this.plateau = deepCopyPlateau(plateau);
        this.joueurActuel = joueurActuel;
        this.jeuTermine = jeuTermine;
        this.emailUtilisateur = emailUtilisateur;
        this.dateSauvegarde = System.currentTimeMillis();
    }
    
    @JsonCreator
    public EtatPartie(
            @JsonProperty("id") UUID id,
            @JsonProperty("plateau") Case[][] plateau,
            @JsonProperty("joueurActuel") int joueurActuel,
            @JsonProperty("jeuTermine") boolean jeuTermine,
            @JsonProperty("emailUtilisateur") String emailUtilisateur,
            @JsonProperty("dateSauvegarde") long dateSauvegarde) {
        this.id = id != null ? id : UUID.randomUUID();
        this.plateau = deepCopyPlateau(plateau);
        this.joueurActuel = joueurActuel;
        this.jeuTermine = jeuTermine;
        this.emailUtilisateur = emailUtilisateur;
        this.dateSauvegarde = dateSauvegarde > 0 ? dateSauvegarde : System.currentTimeMillis();
    }

    private static Case[][] deepCopyPlateau(Case[][] original) {
        if (original == null) return new Case[0][0];
        int rows = original.length;
        int cols = original[0].length;
        Case[][] copie = new Case[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copie[i][j] = new Case(original[i][j]);
            }
        }
        return copie;
    }

    // Getters et setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public Case[][] getPlateau() {
        return plateau;
    }

    public int getJoueurActuel() {
        return joueurActuel;
    }

    public boolean isJeuTermine() {
        return jeuTermine;
    }

    public String getEmailUtilisateur() {
        return emailUtilisateur;
    }

    public long getDateSauvegarde() {
        return dateSauvegarde;
    }
}