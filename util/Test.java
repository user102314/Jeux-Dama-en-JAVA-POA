package util;

import controler.utilisateurControler;
import vue.AuthFrame;
import vue.InscriFrame;

public class Test {

    public static void main(String[] args) {
        InscriFrame inscription = new InscriFrame();
        AuthFrame login = new AuthFrame();

        new utilisateurControler(login, inscription);

        login.setVisible(true);    
    }
}
