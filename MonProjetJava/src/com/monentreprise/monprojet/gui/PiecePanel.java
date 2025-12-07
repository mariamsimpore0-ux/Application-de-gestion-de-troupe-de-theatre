package com.monentreprise.monprojet.gui;

import com.monentreprise.monprojet.dao.TheatreDAO;
import com.monentreprise.monprojet.exceptions.TheatreException;
import com.monentreprise.monprojet.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PiecePanel extends JPanel {
    private TheatreDAO dao;
    private JTable tablePieces;
    private DefaultTableModel tableModel;
    private JTextField txtTitre, txtAuteur, txtDuree;
    private JComboBox<GenrePiece> comboGenre;
    private JTextArea txtDescription;
    private JList<Membre> listMembresDispo, listMembresSelectionnes;
    private DefaultListModel<Membre> modelMembresDispo, modelMembresSelectionnes;
    
    public PiecePanel() {
        dao = new TheatreDAO();
        initUI();
        chargerPieces();
        chargerMembres();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel gauche: Formulaire
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Gestion des pièces"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Titre
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Titre:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTitre = new JTextField(20);
        formPanel.add(txtTitre, gbc);
        
        // Auteur
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Auteur:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtAuteur = new JTextField(20);
        formPanel.add(txtAuteur, gbc);
        
        // Durée
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Durée (min):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtDuree = new JTextField(20);
        formPanel.add(txtDuree, gbc);
        
        // Genre
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        comboGenre = new JComboBox<>(GenrePiece.values());
        formPanel.add(comboGenre, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        JScrollPane scrollDescription = new JScrollPane(txtDescription);
        formPanel.add(scrollDescription, gbc);
        
        // Boutons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> ajouterPiece());
        
        JButton btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(e -> modifierPiece());
        
        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerPiece());
        
        JButton btnVider = new JButton("Vider");
        btnVider.addActionListener(e -> viderFormulaire());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnVider);
        
        formPanel.add(buttonPanel, gbc);
        
        leftPanel.add(formPanel, BorderLayout.NORTH);
        
        // Panel de sélection des membres
        JPanel membresPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        membresPanel.setBorder(BorderFactory.createTitledBorder("Membres associés"));
        
        // Membres disponibles
        modelMembresDispo = new DefaultListModel<>();
        listMembresDispo = new JList<>(modelMembresDispo);
        listMembresDispo.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollDispo = new JScrollPane(listMembresDispo);
        scrollDispo.setBorder(BorderFactory.createTitledBorder("Membres disponibles"));
        
        // Boutons de transfert
        JPanel transferPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton btnAjouterMembre = new JButton(">>");
        btnAjouterMembre.addActionListener(e -> ajouterMembresSelectionnes());
        
        JButton btnRetirerMembre = new JButton("<<");
        btnRetirerMembre.addActionListener(e -> retirerMembresSelectionnes());
        
        transferPanel.add(btnAjouterMembre);
        transferPanel.add(btnRetirerMembre);
        
        // Membres sélectionnés
        modelMembresSelectionnes = new DefaultListModel<>();
        listMembresSelectionnes = new JList<>(modelMembresSelectionnes);
        listMembresSelectionnes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollSelectionnes = new JScrollPane(listMembresSelectionnes);
        scrollSelectionnes.setBorder(BorderFactory.createTitledBorder("Membres dans la pièce"));
        
        membresPanel.add(scrollDispo);
        membresPanel.add(transferPanel);
        membresPanel.add(scrollSelectionnes);
        
        leftPanel.add(membresPanel, BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.WEST);
        
        // Panel droit: Tableau des pièces
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Liste des pièces"));
        
        String[] columns = {"ID", "Titre", "Auteur", "Durée", "Genre", "Membres"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablePieces = new JTable(tableModel);
        tablePieces.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePieces.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectionnerPiece();
            }
        });
        
        JScrollPane scrollTable = new JScrollPane(tablePieces);
        rightPanel.add(scrollTable, BorderLayout.CENTER);
        
        // Panel de statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques"));
        JLabel lblStats = new JLabel();
        statsPanel.add(lblStats);
        rightPanel.add(statsPanel, BorderLayout.SOUTH);
        
        add(rightPanel, BorderLayout.CENTER);
        
        // Mettre à jour les statistiques
        SwingUtilities.invokeLater(() -> {
            majStats();
        });
    }
    
    private void chargerPieces() {
        tableModel.setRowCount(0);
        List<Piece> pieces = dao.getPieces();
        for (Piece p : pieces) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getTitre(),
                p.getAuteur(),
                p.getDureeFormatee(),
                p.getGenre(),
                p.getMembres().size()
            });
        }
        majStats();
    }
    
    private void chargerMembres() {
        modelMembresDispo.clear();
        List<Membre> membres = dao.getMembres();
        for (Membre m : membres) {
            modelMembresDispo.addElement(m);
        }
    }
    
    private void ajouterPiece() {
        try {
            String titre = txtTitre.getText().trim();
            String auteur = txtAuteur.getText().trim();
            String dureeStr = txtDuree.getText().trim();
            GenrePiece genre = (GenrePiece) comboGenre.getSelectedItem();
            String description = txtDescription.getText().trim();
            
            if (titre.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Le titre est obligatoire",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (auteur.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "L'auteur est obligatoire",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int duree;
            try {
                duree = Integer.parseInt(dureeStr);
                if (duree <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "La durée doit être un nombre positif",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Piece piece = new Piece(0, titre, auteur, duree, genre, description);
            
            // Ajouter les membres sélectionnés
            for (int i = 0; i < modelMembresSelectionnes.size(); i++) {
                piece.addMembre(modelMembresSelectionnes.getElementAt(i));
            }
            
            dao.ajouterPiece(piece);
            chargerPieces();
            viderFormulaire();
            
            JOptionPane.showMessageDialog(this,
                "Pièce ajoutée avec succès\nID: " + piece.getId(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (TheatreException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierPiece() {
        int selectedRow = tablePieces.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une pièce à modifier",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Piece piece = dao.getPieceById(id);
            
            if (piece == null) {
                JOptionPane.showMessageDialog(this,
                    "Pièce non trouvée",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            piece.setTitre(txtTitre.getText().trim());
            piece.setAuteur(txtAuteur.getText().trim());
            
            try {
                int duree = Integer.parseInt(txtDuree.getText().trim());
                piece.setDuree(duree);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Durée invalide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            piece.setGenre((GenrePiece) comboGenre.getSelectedItem());
            piece.setDescription(txtDescription.getText().trim());
            
            // Mettre à jour les membres
            piece.getMembres().clear();
            for (int i = 0; i < modelMembresSelectionnes.size(); i++) {
                piece.addMembre(modelMembresSelectionnes.getElementAt(i));
            }
            
            dao.modifierPiece(piece);
            chargerPieces();
            
            JOptionPane.showMessageDialog(this,
                "Pièce modifiée avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (TheatreException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerPiece() {
        int selectedRow = tablePieces.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une pièce à supprimer",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String titre = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer la pièce :\n" +
            titre + " (ID: " + id + ")?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            // Note: Implémenter la suppression dans TheatreDAO
            JOptionPane.showMessageDialog(this,
                "Fonctionnalité à implémenter",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void selectionnerPiece() {
        int selectedRow = tablePieces.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Piece piece = dao.getPieceById(id);
            
            if (piece != null) {
                txtTitre.setText(piece.getTitre());
                txtAuteur.setText(piece.getAuteur());
                txtDuree.setText(String.valueOf(piece.getDuree()));
                comboGenre.setSelectedItem(piece.getGenre());
                txtDescription.setText(piece.getDescription());
                
                // Mettre à jour les listes de membres
                modelMembresSelectionnes.clear();
                for (Membre m : piece.getMembres()) {
                    modelMembresSelectionnes.addElement(m);
                }
                
                // Recharger les membres disponibles
                chargerMembres();
                
                // Retirer les membres déjà sélectionnés
                for (int i = 0; i < modelMembresSelectionnes.size(); i++) {
                    Membre m = modelMembresSelectionnes.getElementAt(i);
                    for (int j = 0; j < modelMembresDispo.size(); j++) {
                        if (modelMembresDispo.getElementAt(j).getId() == m.getId()) {
                            modelMembresDispo.remove(j);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void viderFormulaire() {
        txtTitre.setText("");
        txtAuteur.setText("");
        txtDuree.setText("");
        comboGenre.setSelectedIndex(0);
        txtDescription.setText("");
        modelMembresSelectionnes.clear();
        chargerMembres();
        tablePieces.clearSelection();
    }
    
    private void ajouterMembresSelectionnes() {
        List<Membre> selection = listMembresDispo.getSelectedValuesList();
        for (Membre m : selection) {
            modelMembresSelectionnes.addElement(m);
            modelMembresDispo.removeElement(m);
        }
    }
    
    private void retirerMembresSelectionnes() {
        List<Membre> selection = listMembresSelectionnes.getSelectedValuesList();
        for (Membre m : selection) {
            modelMembresDispo.addElement(m);
            modelMembresSelectionnes.removeElement(m);
        }
    }
    
    private void majStats() {
        int total = dao.getTotalPieces();
        long comedies = dao.getPieces().stream()
            .filter(p -> p.getGenre() == GenrePiece.COMEDIE)
            .count();
        long dureeMoyenne = (long) dao.getPieces().stream()
            .mapToInt(Piece::getDuree)
            .average()
            .orElse(0);
        
        JPanel rightPanel = (JPanel) getComponent(1);
        JPanel statsPanel = (JPanel) rightPanel.getComponent(1);
        JLabel lblStats = (JLabel) statsPanel.getComponent(0);
        lblStats.setText("Total: " + total + " pièces | Comédies: " + comedies + 
                        " | Durée moyenne: " + dureeMoyenne + " min");
    }
}