package com.monentreprise.monprojet.gui;

import com.monentreprise.monprojet.dao.TheatreDAO;
import com.monentreprise.monprojet.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchPanel extends JPanel {
    private TheatreDAO dao;
    private JTabbedPane searchTabs;
    private JTextField txtSearchMembres, txtSearchPieces, txtSearchRepresentations;
    private JTable tableSearchMembres, tableSearchPieces, tableSearchRepresentations;
    private DefaultTableModel modelSearchMembres, modelSearchPieces, modelSearchRepresentations;
    private JScrollPane scrollMembres, scrollPieces, scrollRepresentations; // Références directes
    
    public SearchPanel() {
        dao = new TheatreDAO();
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        searchTabs = new JTabbedPane();
        
        // Onglet Recherche Membres
        JPanel panelMembres = createMembreSearchPanel();
        scrollMembres = (JScrollPane) ((BorderLayout) panelMembres.getLayout()).getLayoutComponent(panelMembres, BorderLayout.CENTER);
        searchTabs.addTab("Membres", panelMembres);
        
        // Onglet Recherche Pièces
        JPanel panelPieces = createPieceSearchPanel();
        scrollPieces = (JScrollPane) ((BorderLayout) panelPieces.getLayout()).getLayoutComponent(panelPieces, BorderLayout.CENTER);
        searchTabs.addTab("Pièces", panelPieces);
        
        // Onglet Recherche Représentations
        JPanel panelRepresentations = createRepresentationSearchPanel();
        scrollRepresentations = (JScrollPane) ((BorderLayout) panelRepresentations.getLayout()).getLayoutComponent(panelRepresentations, BorderLayout.CENTER);
        searchTabs.addTab("Représentations", panelRepresentations);
        
        add(searchTabs, BorderLayout.CENTER);
        
        // Charger les données initiales
        rechercherMembres();
        rechercherPieces();
        rechercherRepresentations();
    }
    
    private JPanel createMembreSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Barre de recherche
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Rechercher:"));
        txtSearchMembres = new JTextField(30);
        txtSearchMembres.addActionListener(e -> rechercherMembres());
        searchBar.add(txtSearchMembres);
        
        JButton btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(e -> rechercherMembres());
        searchBar.add(btnSearch);
        
        JButton btnReset = new JButton("Tout afficher");
        btnReset.addActionListener(e -> {
            txtSearchMembres.setText("");
            rechercherMembres();
        });
        searchBar.add(btnReset);
        
        panel.add(searchBar, BorderLayout.NORTH);
        
        // Tableau des résultats
        String[] columns = {"ID", "Nom", "Prénom", "Rôle", "Email", "Téléphone", "Compétences"};
        modelSearchMembres = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableSearchMembres = new JTable(modelSearchMembres);
        JScrollPane scrollPane = new JScrollPane(tableSearchMembres);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Résultats"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPieceSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Barre de recherche
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Rechercher:"));
        txtSearchPieces = new JTextField(30);
        txtSearchPieces.addActionListener(e -> rechercherPieces());
        searchBar.add(txtSearchPieces);
        
        JButton btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(e -> rechercherPieces());
        searchBar.add(btnSearch);
        
        JButton btnReset = new JButton("Tout afficher");
        btnReset.addActionListener(e -> {
            txtSearchPieces.setText("");
            rechercherPieces();
        });
        searchBar.add(btnReset);
        
        panel.add(searchBar, BorderLayout.NORTH);
        
        // Tableau des résultats
        String[] columns = {"ID", "Titre", "Auteur", "Durée", "Genre", "Description", "Membres"};
        modelSearchPieces = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableSearchPieces = new JTable(modelSearchPieces);
        JScrollPane scrollPane = new JScrollPane(tableSearchPieces);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Résultats"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRepresentationSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Barre de recherche
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Rechercher:"));
        txtSearchRepresentations = new JTextField(30);
        txtSearchRepresentations.addActionListener(e -> rechercherRepresentations());
        searchBar.add(txtSearchRepresentations);
        
        JButton btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(e -> rechercherRepresentations());
        searchBar.add(btnSearch);
        
        JButton btnReset = new JButton("Tout afficher");
        btnReset.addActionListener(e -> {
            txtSearchRepresentations.setText("");
            rechercherRepresentations();
        });
        searchBar.add(btnReset);
        
        panel.add(searchBar, BorderLayout.NORTH);
        
        // Tableau des résultats
        String[] columns = {"ID", "Pièce", "Date", "Heure", "Lieu", "Prix", "Places dispo", "Recette"};
        modelSearchRepresentations = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableSearchRepresentations = new JTable(modelSearchRepresentations);
        JScrollPane scrollPane = new JScrollPane(tableSearchRepresentations);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Résultats"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void rechercherMembres() {
        modelSearchMembres.setRowCount(0);
        String critere = txtSearchMembres.getText().trim();
        List<Membre> membres = dao.rechercherMembres(critere);
        
        for (Membre m : membres) {
            modelSearchMembres.addRow(new Object[]{
                m.getId(),
                m.getNom(),
                m.getPrenom(),
                m.getRole(),
                m.getEmail(),
                m.getTelephone(),
                String.join(", ", m.getCompetences())
            });
        }
        
        // Mettre à jour le titre
        if (scrollMembres != null) {
            scrollMembres.setBorder(BorderFactory.createTitledBorder(
                "Résultats (" + membres.size() + " membres trouvés)"));
        }
    }
    
    private void rechercherPieces() {
        modelSearchPieces.setRowCount(0);
        String critere = txtSearchPieces.getText().trim();
        List<Piece> pieces = dao.rechercherPieces(critere);
        
        for (Piece p : pieces) {
            modelSearchPieces.addRow(new Object[]{
                p.getId(),
                p.getTitre(),
                p.getAuteur(),
                p.getDureeFormatee(),
                p.getGenre(),
                (p.getDescription().length() > 50 ? 
                 p.getDescription().substring(0, 50) + "..." : p.getDescription()),
                p.getMembres().size()
            });
        }
        
        // Mettre à jour le titre
        if (scrollPieces != null) {
            scrollPieces.setBorder(BorderFactory.createTitledBorder(
                "Résultats (" + pieces.size() + " pièces trouvées)"));
        }
    }
    
    private void rechercherRepresentations() {
        modelSearchRepresentations.setRowCount(0);
        String critere = txtSearchRepresentations.getText().trim();
        List<Representation> representations = dao.rechercherRepresentations(critere);
        
        for (Representation r : representations) {
            modelSearchRepresentations.addRow(new Object[]{
                r.getId(),
                r.getPiece().getTitre(),
                r.getDateHeure().toLocalDate().toString(),
                r.getDateHeure().toLocalTime().toString(),
                r.getLieu(),
                String.format("%.2f€", r.getPrixBillet()),
                r.getPlacesDisponibles(),
                String.format("%.2f€", r.getRecetteTotale())
            });
        }
        
        // Mettre à jour le titre
        if (scrollRepresentations != null) {
            scrollRepresentations.setBorder(BorderFactory.createTitledBorder(
                "Résultats (" + representations.size() + " représentations trouvées)"));
        }
    }
}