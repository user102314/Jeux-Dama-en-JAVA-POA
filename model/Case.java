package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Représente une case du plateau de jeu.
 * Cette classe est utilisée pour le jeu de dames.
 */
public class Case implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // Coordonnées de la case sur le plateau
    @JsonProperty("ligne")
	public int ligne;

    @JsonProperty("colonne")
	public int colonne;

    // Type de pièce : 0 = vide, 1 = joueur, 2 = ordinateur
    @JsonProperty("piece")
	public int piece;

    // Indique si la pièce est une dame
    @JsonProperty("estDame")
	public boolean estDame;

    /**
     * Constructeur par défaut requis par Jackson
     */
    public Case() {
        this.ligne = -1;
        this.colonne = -1;
        this.piece = 0;
        this.estDame = false;
    }

    /**
     * Constructeur principal
     */
    public Case(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.piece = 0;  // Vide au départ
        this.estDame = false;
    }

    /**
     * Constructeur de copie
     */
    public Case(Case autre) {
        this.ligne = autre.ligne;
        this.colonne = autre.colonne;
        this.piece = autre.piece;
        this.estDame = autre.estDame;
    }

    /**
     * Méthode de copie profonde
     */
    public Case copy() {
        return new Case(ligne, colonne);
    }

    // Getters et setters pour Jackson et JavaBeans

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public int getColonne() {
        return colonne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    public int getPiece() {
        return piece;
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }

    public boolean isEstDame() {
        return estDame;
    }

    public void setEstDame(boolean estDame) {
        this.estDame = estDame;
    }

    @Override
    public String toString() {
        return "Case{" +
                "ligne=" + ligne +
                ", colonne=" + colonne +
                ", piece=" + piece +
                ", estDame=" + estDame +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}