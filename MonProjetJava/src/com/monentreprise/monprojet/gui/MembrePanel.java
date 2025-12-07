package com.monentreprise.monprojet.gui;

import com.monentreprise.monprojet.dao.TheatreDAO;
import com.monentreprise.monprojet.exceptions.MemberException;
import com.monentreprise.monprojet.model.Membre;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MembrePanel extends JPanel {
    private TheatreDAO dao;
    private JTable tableMembres;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom, txtEmail, txtTelephone;
    private JComboBox<String> comboRole;
    private JTextArea txtCompetences, txtDisponibilites;
    
    public MembrePanel() {
        dao = new TheatreDAO();
        initUI();
        chargerMembres();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel supérieur pour le formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Ajouter/Modifier un membre"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Ligne 1: Nom
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNom = new JTextField(20);
        formPanel.add(txtNom, gbc);
        
        // Ligne 2: Prénom
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtPrenom = new JTextField(20);
        formPanel.add(txtPrenom, gbc);
        
        // Ligne 3: Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        
        // Ligne 4: Téléphone
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTelephone = new JTextField(20);
        formPanel.add(txtTelephone, gbc);
        
        // Ligne 5: Rôle
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Rôle:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        String[] roles = {"Acteur", "Metteur en scène", "Technicien", "Administratif", "Costumier", "Régisseur"};
        comboRole = new JComboBox<>(roles);
        formPanel.add(comboRole, gbc);
        
        // Ligne 6: Compétences
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Compétences:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        txtCompetences = new JTextArea(3, 20);
        txtCompetences.setLineWrap(true);
        JScrollPane scrollCompetences = new JScrollPane(txtCompetences);
        formPanel.add(scrollCompetences, gbc);
        
        // Ligne 7: Disponibilités
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Disponibilités:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtDisponibilites = new JTextArea(3, 20);
        txtDisponibilites.setLineWrap(true);
        JScrollPane scrollDisponibilites = new JScrollPane(txtDisponibilites);
        formPanel.add(scrollDisponibilites, gbc);
        
        // Ligne 8: Boutons
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> ajouterMembre());
        
        JButton btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(e -> modifierMembre());
        
        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerMembre());
        
        JButton btnVider = new JButton("Vider");
        btnVider.addActionListener(e -> viderFormulaire());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnVider);
        
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Tableau des membres
        String[] columns = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Rôle", "Compétences"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableMembres = new JTable(tableModel);
        tableMembres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMembres.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectionnerMembre();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableMembres);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des membres"));
        
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
    
    private void chargerMembres() {
        tableModel.setRowCount(0);
        List<Membre> membres = dao.getMembres();
        for (Membre m : membres) {
            tableModel.addRow(new Object[]{
                m.getId(),
                m.getNom(),
                m.getPrenom(),
                m.getEmail(),
                m.getTelephone(),
                m.getRole(),
                String.join(", ", m.getCompetences())
            });
        }
        majStats();
    }
    
    private void ajouterMembre() {
        try {
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            String email = txtEmail.getText().trim();
            String telephone = txtTelephone.getText().trim();
            String role = (String) comboRole.getSelectedItem();
            
            if (nom.isEmpty() || prenom.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Le nom et le prénom sont obligatoires",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Membre membre = new Membre(0, nom, prenom, email, telephone, role);
            
            // Ajouter les compétences
            String[] competences = txtCompetences.getText().split("\\n");
            for (String comp : competences) {
                if (!comp.trim().isEmpty()) {
                    membre.addCompetence(comp.trim());
                }
            }
            
            // Ajouter les disponibilités
            String[] disponibilites = txtDisponibilites.getText().split("\\n");
            for (String disp : disponibilites) {
                if (!disp.trim().isEmpty()) {
                    membre.addDisponibilite(disp.trim());
                }
            }
            
            dao.ajouterMembre(membre);
            chargerMembres();
            viderFormulaire();
            
            JOptionPane.showMessageDialog(this,
                "Membre ajouté avec succès\nID: " + membre.getId(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (MemberException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierMembre() {
        int selectedRow = tableMembres.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un membre à modifier",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Membre membre = dao.getMembreById(id);
            
            if (membre == null) {
                JOptionPane.showMessageDialog(this,
                    "Membre non trouvé",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            membre.setNom(txtNom.getText().trim());
            membre.setPrenom(txtPrenom.getText().trim());
            membre.setEmail(txtEmail.getText().trim());
            membre.setTelephone(txtTelephone.getText().trim());
            membre.setRole((String) comboRole.getSelectedItem());
            
            // Mettre à jour les compétences
            membre.getCompetences().clear();
            String[] competences = txtCompetences.getText().split("\\n");
            for (String comp : competences) {
                if (!comp.trim().isEmpty()) {
                    membre.addCompetence(comp.trim());
                }
            }
            
            // Mettre à jour les disponibilités
            membre.getDisponibilites().clear();
            String[] disponibilites = txtDisponibilites.getText().split("\\n");
            for (String disp : disponibilites) {
                if (!disp.trim().isEmpty()) {
                    membre.addDisponibilite(disp.trim());
                }
            }
            
            dao.modifierMembre(membre);
            chargerMembres();
            
            JOptionPane.showMessageDialog(this,
                "Membre modifié avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (MemberException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerMembre() {
        int selectedRow = tableMembres.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un membre à supprimer",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        String prenom = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le membre :\n" +
            prenom + " " + nom + " (ID: " + id + ")?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                dao.supprimerMembre(id);
                chargerMembres();
                viderFormulaire();
                
                JOptionPane.showMessageDialog(this,
                    "Membre supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (MemberException e) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void selectionnerMembre() {
        int selectedRow = tableMembres.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Membre membre = dao.getMembreById(id);
            
            if (membre != null) {
                txtNom.setText(membre.getNom());
                txtPrenom.setText(membre.getPrenom());
                txtEmail.setText(membre.getEmail());
                txtTelephone.setText(membre.getTelephone());
                comboRole.setSelectedItem(membre.getRole());
                txtCompetences.setText(String.join("\n", membre.getCompetences()));
                txtDisponibilites.setText(String.join("\n", membre.getDisponibilites()));
            }
        }
    }
    
    private void viderFormulaire() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtEmail.setText("");
        txtTelephone.setText("");
        comboRole.setSelectedIndex(0);
        txtCompetences.setText("");
        txtDisponibilites.setText("");
        tableMembres.clearSelection();
    }
    private void majStats() {
    int total = dao.getTotalMembres();
    long acteurs = dao.getMembres().stream()
        .filter(m -> m.getRole().equalsIgnoreCase("Acteur"))
        .count();
    long techniciens = dao.getMembres().stream()
        .filter(m -> m.getRole().equalsIgnoreCase("Technicien"))
        .count();
    
    // Méthode simple : parcourir les composants pour trouver le JLabel
    Component[] components = getComponents();
    for (Component comp : components) {
        if (comp instanceof JPanel) {
            JPanel panel = (JPanel) comp;
            if (panel.getBorder() != null && 
                panel.getBorder().toString().contains("Statistiques")) {
                // Chercher le JLabel dans ce panel
                for (Component child : panel.getComponents()) {
                    if (child instanceof JLabel) {
                        ((JLabel) child).setText("Total: " + total + " membres | Acteurs: " + 
                                               acteurs + " | Techniciens: " + techniciens);
                        return;
                    }
                }
            }
        }
    }
}
}