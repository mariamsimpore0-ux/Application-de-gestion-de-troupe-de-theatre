package com.monentreprise.monprojet.gui;

import com.monentreprise.monprojet.dao.TheatreDAO;
import com.monentreprise.monprojet.exceptions.TheatreException;
import com.monentreprise.monprojet.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RepresentationPanel extends JPanel {
    private TheatreDAO dao;
    private JTable tableRepresentations;
    private DefaultTableModel tableModel;
    private JComboBox<Piece> comboPiece;
    private JTextField txtLieu, txtPrix, txtCapacite;
    private JFormattedTextField txtDate, txtHeure;
    private JButton btnAjouter, btnModifier, btnSupprimer;
    
    public RepresentationPanel() {
        dao = new TheatreDAO();
        initUI();
        chargerRepresentations();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel supérieur: Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Nouvelle représentation"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Pièce
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Pièce:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        comboPiece = new JComboBox<>();
        chargerPieces();
        formPanel.add(comboPiece, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Date (jj/mm/aaaa):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtDate = new JFormattedTextField(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        txtDate.setColumns(10);
        formPanel.add(txtDate, gbc);
        
        // Heure
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Heure (hh:mm):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtHeure = new JFormattedTextField(DateTimeFormatter.ofPattern("HH:mm"));
        txtHeure.setColumns(10);
        formPanel.add(txtHeure, gbc);
        
        // Lieu
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Lieu:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtLieu = new JTextField(20);
        formPanel.add(txtLieu, gbc);
        
        // Prix
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Prix billet (€):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtPrix = new JTextField(20);
        formPanel.add(txtPrix, gbc);
        
        // Capacité
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Capacité max:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtCapacite = new JTextField(20);
        formPanel.add(txtCapacite, gbc);
        
        // Boutons
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> ajouterRepresentation());
        
        btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(e -> modifierRepresentation());
        btnModifier.setEnabled(false);
        
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerRepresentation());
        btnSupprimer.setEnabled(false);
        
        JButton btnVider = new JButton("Vider");
        btnVider.addActionListener(e -> viderFormulaire());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnVider);
        
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Tableau des représentations
        String[] columns = {"ID", "Pièce", "Date", "Heure", "Lieu", "Prix", "Capacité", "Places dispo", "Recette"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableRepresentations = new JTable(tableModel);
        tableRepresentations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableRepresentations.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = tableRepresentations.getSelectedRow() != -1;
                btnModifier.setEnabled(hasSelection);
                btnSupprimer.setEnabled(hasSelection);
                
                if (hasSelection) {
                    selectionnerRepresentation();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableRepresentations);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Représentations programmées"));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques"));
        JLabel lblStats = new JLabel();
        statsPanel.add(lblStats);
        add(statsPanel, BorderLayout.SOUTH);
        
        // Mettre à jour les statistiques
        SwingUtilities.invokeLater(() -> {
            majStats();
        });
    }
    
    private void chargerPieces() {
        comboPiece.removeAllItems();
        List<Piece> pieces = dao.getPieces();
        for (Piece p : pieces) {
            comboPiece.addItem(p);
        }
    }
    
    private void chargerRepresentations() {
        tableModel.setRowCount(0);
        List<Representation> representations = dao.getRepresentations();
        for (Representation r : representations) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPiece().getTitre(),
                r.getDateHeure().toLocalDate().toString(),
                r.getDateHeure().toLocalTime().toString(),
                r.getLieu(),
                String.format("%.2f€", r.getPrixBillet()),
                r.getCapaciteMax(),
                r.getPlacesDisponibles(),
                String.format("%.2f€", r.getRecetteTotale())
            });
        }
        majStats();
    }
    
    private void ajouterRepresentation() {
        try {
            Piece piece = (Piece) comboPiece.getSelectedItem();
            if (piece == null) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une pièce",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String dateStr = txtDate.getText().trim();
            String heureStr = txtHeure.getText().trim();
            String lieu = txtLieu.getText().trim();
            String prixStr = txtPrix.getText().trim();
            String capaciteStr = txtCapacite.getText().trim();
            
            // Validation
            if (dateStr.isEmpty() || heureStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "La date et l'heure sont obligatoires",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (lieu.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Le lieu est obligatoire",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double prix;
            try {
                prix = Double.parseDouble(prixStr);
                if (prix <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Le prix doit être un nombre positif",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int capacite;
            try {
                capacite = Integer.parseInt(capaciteStr);
                if (capacite <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "La capacité doit être un nombre positif",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parser la date et l'heure
            LocalDateTime dateHeure;
            try {
                String[] dateParts = dateStr.split("/");
                String[] heureParts = heureStr.split(":");
                
                int jour = Integer.parseInt(dateParts[0]);
                int mois = Integer.parseInt(dateParts[1]);
                int annee = Integer.parseInt(dateParts[2]);
                int heure = Integer.parseInt(heureParts[0]);
                int minute = Integer.parseInt(heureParts[1]);
                
                dateHeure = LocalDateTime.of(annee, mois, jour, heure, minute);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Format de date/heure invalide. Utilisez jj/mm/aaaa et hh:mm",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Representation representation = new Representation(0, dateHeure, lieu, prix, capacite, piece);
            dao.ajouterRepresentation(representation);
            chargerRepresentations();
            viderFormulaire();
            
            JOptionPane.showMessageDialog(this,
                "Représentation ajoutée avec succès\nID: " + representation.getId(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (TheatreException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierRepresentation() {
        int selectedRow = tableRepresentations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une représentation à modifier",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this,
            "Fonctionnalité à implémenter",
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void supprimerRepresentation() {
        int selectedRow = tableRepresentations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une représentation à supprimer",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String piece = (String) tableModel.getValueAt(selectedRow, 1);
        String date = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer la représentation :\n" +
            piece + " du " + date + " (ID: " + id + ")?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "Fonctionnalité à implémenter",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void selectionnerRepresentation() {
        int selectedRow = tableRepresentations.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Representation representation = dao.getRepresentationById(id);
            
            if (representation != null) {
                comboPiece.setSelectedItem(representation.getPiece());
                txtDate.setText(String.format("%02d/%02d/%04d",
                    representation.getDateHeure().getDayOfMonth(),
                    representation.getDateHeure().getMonthValue(),
                    representation.getDateHeure().getYear()));
                txtHeure.setText(String.format("%02d:%02d",
                    representation.getDateHeure().getHour(),
                    representation.getDateHeure().getMinute()));
                txtLieu.setText(representation.getLieu());
                txtPrix.setText(String.valueOf(representation.getPrixBillet()));
                txtCapacite.setText(String.valueOf(representation.getCapaciteMax()));
            }
        }
    }
    
    private void viderFormulaire() {
        if (comboPiece.getItemCount() > 0) {
            comboPiece.setSelectedIndex(0);
        }
        txtDate.setText("");
        txtHeure.setText("");
        txtLieu.setText("");
        txtPrix.setText("");
        txtCapacite.setText("");
        tableRepresentations.clearSelection();
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }
    
    private void majStats() {
        int total = dao.getTotalRepresentations();
        int futures = dao.getRepresentationsFutures().size();
        double recetteTotale = dao.getRecetteTotale();
        
        JPanel statsPanel = (JPanel) getComponent(2);
        JLabel lblStats = (JLabel) statsPanel.getComponent(0);
        lblStats.setText("Total: " + total + " représentations | Futures: " + futures + 
                        " | Recette totale: " + String.format("%.2f€", recetteTotale));
    }
}