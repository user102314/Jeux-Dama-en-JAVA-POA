package damier;

import java.util.List;

public class Mouvement {
    public Case source;
    public Case destination;
    public Case capture;
    public List<Case> capturesMultiples;
    
    // Constructeur pour mouvement sans capture
    public Mouvement(Case source, Case destination) {
        this.source = source;
        this.destination = destination;
        this.capture = null;
        this.capturesMultiples = null;
    }
    
    // Constructeur pour capture simple
    public Mouvement(Case source, Case destination, Case capture) {
        this.source = source;
        this.destination = destination;
        this.capture = capture;
        this.capturesMultiples = null;
    }
    
    // Constructeur pour captures multiples
    public Mouvement(Case source, Case destination, List<Case> capturesMultiples) {
        this.source = source;
        this.destination = destination;
        this.capturesMultiples = capturesMultiples;
        this.capture = (capturesMultiples != null && !capturesMultiples.isEmpty()) ? 
                       capturesMultiples.get(0) : null;
    }
}
