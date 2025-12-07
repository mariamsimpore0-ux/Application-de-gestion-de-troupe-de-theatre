package com.monentreprise.monprojet.gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTabbedPane tabbedPane;
    
    public MainWindow() {
        setTitle("Gestion de Troupe de Théâtre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        // Création des onglets
        tabbedPane = new JTabbedPane();
        
        // Onglets temporaires (à implémenter)
        tabbedPane.addTab("Accueil", createAccueilPanel());
    tabbedPane.addTab("Membres", new MembrePanel());
    tabbedPane.addTab("Pièces", new PiecePanel());
    tabbedPane.addTab("Représentations", new RepresentationPanel());
    tabbedPane.addTab("Réservations", new ReservationPanel());
    tabbedPane.addTab("Recherche", new SearchPanel());
    
        
        add(tabbedPane);
        
        // Menu
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem itemQuitter = new JMenuItem("Quitter");
        itemQuitter.addActionListener(e -> System.exit(0));
        menuFichier.add(itemQuitter);
        
        JMenu menuAide = new JMenu("Aide");
        JMenuItem itemAPropos = new JMenuItem("À propos");
        itemAPropos.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Application de Gestion de Troupe de Théâtre\n" +
                "Version 1.0\n" +
                "Java " + System.getProperty("java.version") + "\n" +
                "© 2024 MonEntreprise",
                "À propos",
                JOptionPane.INFORMATION_MESSAGE);
        });
        menuAide.add(itemAPropos);
        
        menuBar.add(menuFichier);
        menuBar.add(menuAide);
        setJMenuBar(menuBar);
    }
    
    private JPanel createAccueilPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // En-tête
        JLabel titleLabel = new JLabel("Application de Gestion de Théâtre", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Panel d'information
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        infoPanel.add(createInfoCard("🎭", "Gestion des Membres", 
            "Ajouter, modifier et supprimer les membres de la troupe"));
        infoPanel.add(createInfoCard("📜", "Gestion des Pièces", 
            "Gérer le répertoire des pièces de théâtre"));
        infoPanel.add(createInfoCard("🎪", "Gestion des Représentations", 
            "Planifier les représentations et gérer les lieux"));
        infoPanel.add(createInfoCard("🎫", "Gestion des Réservations", 
            "Gérer les réservations de billets et les clients"));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoCard(String icon, String title, String description) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.DARK_GRAY);
        
        textPanel.add(titleLabel);
        textPanel.add(descLabel);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title + " - En cours de développement", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 16));
        label.setForeground(Color.GRAY);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainWindow().setVisible(true);
        });
    }
}