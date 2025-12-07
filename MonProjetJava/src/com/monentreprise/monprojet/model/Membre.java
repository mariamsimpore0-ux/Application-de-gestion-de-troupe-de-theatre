package com.monentreprise.monprojet.model;

import java.util.ArrayList;
import java.util.List;

public class Membre {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role; // acteur, metteur en scène, technicien
    private List<String> competences;
    private List<String> disponibilites;
    
    public Membre(int id, String nom, String prenom, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.email = "";
        this.telephone = "";
        this.competences = new ArrayList<>();
        this.disponibilites = new ArrayList<>();
    }
    
    // Constructeur complet
    public Membre(int id, String nom, String prenom, String email, 
                  String telephone, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.role = role;
        this.competences = new ArrayList<>();
        this.disponibilites = new ArrayList<>();
    }
    
    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getRole() { return role; }
    public List<String> getCompetences() { return competences; }
    public List<String> getDisponibilites() { return disponibilites; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setRole(String role) { this.role = role; }
    public void setCompetences(List<String> competences) { this.competences = competences; }
    public void setDisponibilites(List<String> disponibilites) { this.disponibilites = disponibilites; }
    
    // Méthodes
    public void addCompetence(String competence) {
        if (!competences.contains(competence)) {
            competences.add(competence);
        }
    }
    
    public void addDisponibilite(String date) {
        if (!disponibilites.contains(date)) {
            disponibilites.add(date);
        }
    }
    
    public void removeCompetence(String competence) {
        competences.remove(competence);
    }
    
    public void removeDisponibilite(String date) {
        disponibilites.remove(date);
    }
    
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    @Override
    public String toString() {
        return getNomComplet() + " (" + role + ")";
    }
    
    public String getInfosCompletes() {
        return "ID: " + id + ", Nom: " + getNomComplet() + 
               ", Rôle: " + role + ", Email: " + email + 
               ", Tél: " + telephone + ", Compétences: " + competences.size();
    }
}