package vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import damier.JeuDeDames;
import util.EtatPartie;

public class ChargerPartieView extends JFrame {

    private String currentEmail;
    private ObjectMapper mapper = new ObjectMapper();
    
    // Couleurs thématiques du jeu
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color ALTERNATE_COLOR = new Color(80, 80, 80);
    private static final Color ACCENT_COLOR = Color.RED;
    private static final Color BUTTON_COLOR = new Color(0, 204, 204); // Cyan/turquoise
    private static final Color TEXT_COLOR = Color.WHITE;
    
    public ChargerPartieView(String email, MainView mainView) {
        this.currentEmail = email;
                
        setTitle("Charger une Partie");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Configuration du panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // En-tête
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("CHARGER UNE PARTIE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel subLabel = new JLabel("Sélectionnez une partie sauvegardée");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subLabel.setForeground(TEXT_COLOR);
        subLabel.setAlignmentX(CENTER_ALIGNMENT);
        headerPanel.add(subLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panneau central pour la liste des parties
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        try {
            File dir = new File("sauvegardes");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, currentEmail + "_parties.json");

            if (!file.exists() || !file.canRead()) {
                JLabel aucun = createMessageLabel("Aucune partie sauvegardée trouvée");
                centerPanel.add(aucun);
            } else {
                List<EtatPartie> parties = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, EtatPartie.class));

                if (parties.isEmpty()) {
                    JLabel aucun = createMessageLabel("Aucune partie sauvegardée trouvée");
                    centerPanel.add(aucun);
                } else {
                    for (int i = 0; i < parties.size(); i++) {
                        EtatPartie p = parties.get(i);
                        final int index = i;

                        JButton btn = createGameButton((index + 1) + ". " + p.toString());
                        btn.addActionListener(e -> {
                            chargerEtFermer(parties, index, file, mainView, parties.get(index));
                        });
                        centerPanel.add(btn);
                        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    }
                }
            }
        } catch (Exception ex) {
            JLabel erreur = createMessageLabel("Erreur lors du chargement des parties");
            erreur.setForeground(ACCENT_COLOR);
            centerPanel.add(erreur);
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau du bas avec le bouton de fermeture
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(ALTERNATE_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JButton fermerBtn = createStyledButton("retour au menu");
        fermerBtn.addActionListener(e -> dispose());
        bottomPanel.add(fermerBtn);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Ajouter des bandes colorées sur les côtés
        JPanel eastPanel = createSidePanel();
        JPanel westPanel = createSidePanel();
        mainPanel.add(eastPanel, BorderLayout.EAST);
        mainPanel.add(westPanel, BorderLayout.WEST);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel(new GridLayout(8, 1));
        panel.setPreferredSize(new Dimension(30, 0));
        
        for (int i = 0; i < 8; i++) {
            JPanel cell = new JPanel();
            cell.setBackground(i % 2 == 0 ? ACCENT_COLOR : ALTERNATE_COLOR);
            panel.add(cell);
        }
        
        return panel;
    }
    
    private JLabel createMessageLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }
    
    private JButton createGameButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 40));
        button.setPreferredSize(new Dimension(400, 40));
        
        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_COLOR.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text.toUpperCase());
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        
        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_COLOR.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }

    private void chargerEtFermer(List<EtatPartie> parties, int index, File file, MainView mainView, EtatPartie partie) {
        // Création d'une boîte de dialogue personnalisée
        JDialog confirmDialog = new JDialog(this, "Confirmation", true);
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JLabel message = new JLabel("Voulez-vous garder cette sauvegarde?");
        message.setForeground(TEXT_COLOR);
        message.setFont(new Font("Arial", Font.BOLD, 16));
        message.setHorizontalAlignment(JLabel.CENTER);
        message.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(ALTERNATE_COLOR);
        
        JButton keepBtn = createStyledButton("Garder");
        keepBtn.setPreferredSize(new Dimension(150, 40));
        keepBtn.addActionListener(e -> {
            chargerPartie(mainView, partie);
            confirmDialog.dispose();
        });
        
        JButton deleteBtn = createStyledButton("Supprimer");
        deleteBtn.setPreferredSize(new Dimension(150, 40));
        deleteBtn.addActionListener(e -> {
            try {
                parties.remove(index);
                mapper.writeValue(file, parties);
                chargerPartie(mainView, partie);
                confirmDialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        buttonPanel.add(keepBtn);
        buttonPanel.add(deleteBtn);
        
        confirmDialog.add(message, BorderLayout.CENTER);
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        SwingUtilities.invokeLater(() -> confirmDialog.setVisible(true));
    }
    
    private void chargerPartie(MainView mainView, EtatPartie partie) {
        SwingUtilities.invokeLater(() -> {
            mainView.dispose();
            JeuDeDames jeu = new JeuDeDames(currentEmail, partie);
            jeu.setVisible(true);
            this.dispose();
        });
    }
}