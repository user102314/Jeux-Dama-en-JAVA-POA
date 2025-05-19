package model;


import java.util.ArrayList;
import java.util.List;




public class Mouvement {
    public Case source;
    public Case destination;
    public Case capture;  // La pièce capturée (null si pas de capture)
    public List<Case> capturesMultiples;  // Pour les rafles (captures multiples)
    
    public Mouvement(Case source, Case destination, Case capture) {
        this.source = source;
        this.destination = destination;
        this.capture = capture;
        this.capturesMultiples = new ArrayList<>();
        if (capture != null) {
            this.capturesMultiples.add(capture);
        }
    }
    
    // Ajouter une capture à la liste
    public void ajouterCapture(Case capture) {
        if (capture != null && !capturesMultiples.contains(capture)) {
            capturesMultiples.add(capture);
        }
    }
    
    // Fusionner avec un autre mouvement (pour les rafles)
    public void fusionnerMouvement(Mouvement autre) {
        if (autre != null && autre.capturesMultiples != null) {
            for (Case c : autre.capturesMultiples) {
                ajouterCapture(c);
            }
        }
    }
}