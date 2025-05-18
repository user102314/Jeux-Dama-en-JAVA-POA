package damier;

public class Case {
    public int ligne;
    public int colonne;
    public int piece; // 0 = vide, 1 = joueur, 2 = ordinateur
    public boolean estDame;
    
    public Case(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.piece = 0;
        this.estDame = false;
    }
}


