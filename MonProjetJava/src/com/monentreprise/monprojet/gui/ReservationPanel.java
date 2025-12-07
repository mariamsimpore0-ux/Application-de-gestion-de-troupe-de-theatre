package com.monentreprise.monprojet.gui;

import com.monentreprise.monprojet.dao.TheatreDAO;
import com.monentreprise.monprojet.exceptions.ReservationException;
import com.monentreprise.monprojet.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReservationPanel extends JPanel {
    private TheatreDAO dao;
    private JTable tableReservations;
    private DefaultTableModel tableModel;
    private JComboBox<Representation> comboRepresentation;
    private JTextField txtNomClient, txtEmailClient, txtNombreBillets;
    private JButton btnReserver, btnAnnuler, btnConfirmer;
    
    public ReservationPanel() {
        dao = new TheatreDAO();
        initUI();
        chargerReservations();
        chargerRepresentations();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel gauche: Nouvelle réservation
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Nouvelle réservation"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Représentation
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Représentation:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        comboRepresentation = new JComboBox<>();
        comboRepresentation.addActionListener(e -> updateRepresentationInfo());
        formPanel.add(comboRepresentation, gbc);
        
        // Info représentation
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        JLabel lblInfoRep = new JLabel("Sélectionnez une représentation");
        lblInfoRep.setForeground(Color.BLUE);
        formPanel.add(lblInfoRep, gbc);
        
        // Nom client
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Nom client:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNomClient = new JTextField(20);
        formPanel.add(txtNomClient, gbc);
        
        // Email client
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Email client:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtEmailClient = new JTextField(20);
        formPanel.add(txtEmailClient, gbc);
        
        // Nombre de billets
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Nombre de billets:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNombreBillets = new JTextField(20);
        formPanel.add(txtNombreBillets, gbc);
        
        // Bouton Réserver
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnReserver = new JButton("Réserver");
        btnReserver.addActionListener(e -> faireReservation());
        formPanel.add(btnReserver, gbc);
        
        leftPanel.add(formPanel, BorderLayout.NORTH);
        
        // Panel d'information
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informations"));
        JTextArea txtInfo = new JTextArea();
        txtInfo.setEditable(false);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        infoPanel.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
        
        leftPanel.add(infoPanel, BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.WEST);
        
        // Panel droit: Liste des réservations
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Réservations existantes"));
        
        String[] columns = {"ID", "Client", "Email", "Représentation", "Date", "Billets", "Montant", "État"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableReservations = new JTable(tableModel);
        tableReservations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableReservations.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = tableReservations.getSelectedRow() != -1;
                btnAnnuler.setEnabled(hasSelection);
                btnConfirmer.setEnabled(hasSelection);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableReservations);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de boutons pour les réservations
        JPanel reservationButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnAnnuler = new JButton("Annuler réservation");
        btnAnnuler.setEnabled(false);
        btnAnnuler.addActionListener(e -> annulerReservation());
        
        btnConfirmer = new JButton("Confirmer réservation");
        btnConfirmer.setEnabled(false);
        btnConfirmer.addActionListener(e -> confirmerReservation());
        
        reservationButtonsPanel.add(btnAnnuler);
        reservationButtonsPanel.add(btnConfirmer);
        
        rightPanel.add(reservationButtonsPanel, BorderLayout.NORTH);
        
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
    
    private void chargerRepresentations() {
        comboRepresentation.removeAllItems();
        List<Representation> representations = dao.getRepresentationsAvecPlaces();
        for (Representation r : representations) {
            comboRepresentation.addItem(r);
        }
        
        if (comboRepresentation.getItemCount() > 0) {
            comboRepresentation.setSelectedIndex(0);
        }
    }
    
    private void chargerReservations() {
        tableModel.setRowCount(0);
        List<Reservation> reservations = dao.getReservations();
        for (Reservation r : reservations) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getNomClient(),
                r.getEmailClient(),
                r.getRepresentation().getPiece().getTitre(),
                r.getDateReservationFormatee(),
                r.getNombreBillets(),
                String.format("%.2f€", r.getMontantTotal()),
                r.getEtat()
            });
        }
        majStats();
    }
    
    private void updateRepresentationInfo() {
        Representation rep = (Representation) comboRepresentation.getSelectedItem();
        if (rep != null) {
            JPanel leftPanel = (JPanel) getComponent(0);
            JPanel infoPanel = (JPanel) leftPanel.getComponent(1);
            JTextArea txtInfo = (JTextArea) ((JScrollPane) infoPanel.getComponent(0)).getViewport().getView();
            
            String info = "Pièce: " + rep.getPiece().getTitre() + "\n" +
                         "Date: " + rep.getDateFormatee() + "\n" +
                         "Lieu: " + rep.getLieu() + "\n" +
                         "Prix billet: " + String.format("%.2f€", rep.getPrixBillet()) + "\n" +
                         "Places disponibles: " + rep.getPlacesDisponibles() + "/" + rep.getCapaciteMax() + "\n" +
                         "Durée: " + rep.getPiece().getDureeFormatee();
            
            txtInfo.setText(info);
        }
    }
    
    private void faireReservation() {
        try {
            Representation representation = (Representation) comboRepresentation.getSelectedItem();
            if (representation == null) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une représentation",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String nomClient = txtNomClient.getText().trim();
            String emailClient = txtEmailClient.getText().trim();
            String nombreBilletsStr = txtNombreBillets.getText().trim();
            
            if (nomClient.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Le nom du client est obligatoire",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (emailClient.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "L'email du client est obligatoire",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int nombreBillets;
            try {
                nombreBillets = Integer.parseInt(nombreBilletsStr);
                if (nombreBillets <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Le nombre de billets doit être un entier positif",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Reservation reservation = new Reservation(nomClient, emailClient, nombreBillets, representation);
            dao.ajouterReservation(reservation);
            chargerReservations();
            chargerRepresentations(); // Recharger car places disponibles ont changé
            viderFormulaire();
            
            double montant = reservation.getMontantTotal();
            
            JOptionPane.showMessageDialog(this,
                "Réservation effectuée avec succès!\n\n" +
                "Numéro de réservation: " + reservation.getId() + "\n" +
                "Client: " + nomClient + "\n" +
                "Pièce: " + representation.getPiece().getTitre() + "\n" +
                "Date: " + representation.getDateFormatee() + "\n" +
                "Nombre de billets: " + nombreBillets + "\n" +
                "Montant total: " + String.format("%.2f€", montant) + "\n\n" +
                "Un email de confirmation a été envoyé à: " + emailClient,
                "Confirmation de réservation",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (ReservationException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Erreur de réservation",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void annulerReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une réservation à annuler",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String client = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir annuler la réservation :\n" +
            "ID: " + id + "\nClient: " + client + "?",
            "Confirmation d'annulation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                dao.annulerReservation(id);
                chargerReservations();
                chargerRepresentations();
                
                JOptionPane.showMessageDialog(this,
                    "Réservation annulée avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (ReservationException e) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void confirmerReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une réservation à confirmer",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String client = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Confirmer la réservation :\n" +
            "ID: " + id + "\nClient: " + client + "?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                dao.confirmerReservation(id);
                chargerReservations();
                
                JOptionPane.showMessageDialog(this,
                    "Réservation confirmée avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (ReservationException e) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viderFormulaire() {
        txtNomClient.setText("");
        txtEmailClient.setText("");
        txtNombreBillets.setText("");
        if (comboRepresentation.getItemCount() > 0) {
            comboRepresentation.setSelectedIndex(0);
        }
        tableReservations.clearSelection();
        btnAnnuler.setEnabled(false);
        btnConfirmer.setEnabled(false);
    }
    
    private void majStats() {
        int total = dao.getTotalReservations();
        long confirmees = dao.getReservations().stream()
            .filter(r -> r.getEtat().equals("CONFIRMEE"))
            .count();
        long annulees = dao.getReservations().stream()
            .filter(r -> r.getEtat().equals("ANNULEE"))
            .count();
        double recette = dao.getRecetteTotale();
        
        JPanel rightPanel = (JPanel) getComponent(1);
        JPanel statsPanel = (JPanel) rightPanel.getComponent(2);
        JLabel lblStats = (JLabel) statsPanel.getComponent(0);
        lblStats.setText("Total: " + total + " réservations | Confirmées: " + confirmees + 
                        " | Annulées: " + annulees + " | Recette: " + String.format("%.2f€", recette));
    }
}