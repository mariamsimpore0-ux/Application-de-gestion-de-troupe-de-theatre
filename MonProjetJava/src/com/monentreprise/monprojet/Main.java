package com.monentreprise.monprojet;

import com.monentreprise.monprojet.gui.MainWindow;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Application de Gestion de Théâtre ===");
        System.out.println("Version Java: " + System.getProperty("java.version"));
        
        // Mode console simple si argument
        if (args.length > 0 && args[0].equals("-console")) {
            System.out.println("Mode console activé");
            testConsoleMode();
            return;
        }
        
        // Mode graphique par défaut
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Erreur Look and Feel: " + e.getMessage());
            }
            new MainWindow().setVisible(true);
        });
    }
    
    private static void testConsoleMode() {
        System.out.println("\n=== TEST CONSOLE ===");
        
        // Test des classes de base
        com.monentreprise.monprojet.model.Membre membre = 
            new com.monentreprise.monprojet.model.Membre(1, "Dupont", "Jean", "Acteur");
        membre.addCompetence("Comédie");
        membre.addCompetence("Chant");
        
        com.monentreprise.monprojet.model.Piece piece = 
            new com.monentreprise.monprojet.model.Piece(1, "Le Malade Imaginaire", 
                "Molière", 120, com.monentreprise.monprojet.model.GenrePiece.COMEDIE);
        
        System.out.println("Membre: " + membre);
        System.out.println("Pièce: " + piece);
        System.out.println("\nApplication prête pour le développement GUI!");
    }
}