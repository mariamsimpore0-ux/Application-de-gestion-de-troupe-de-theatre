package com.monentreprise.monprojet.model;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private int id;
    private String titre;
    private String auteur;
    private int duree; // en minutes
    private GenrePiece genre;
    private String description;
    private List<Membre> membres;
    
    public Piece(int id, String titre, String auteur, int duree, GenrePiece genre) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.duree = duree;
        this.genre = genre;
        this.description = "";
        this.membres = new ArrayList<>();
    }
    
    public Piece(int id, String titre, String auteur, int duree, 
                 GenrePiece genre, String description) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.duree = duree;
        this.genre = genre;
        this.description = description;
        this.membres = new ArrayList<>();
    }
    
    // Getters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getAuteur() { return auteur; }
    public int getDuree() { return duree; }
    public GenrePiece getGenre() { return genre; }
    public String getDescription() { return description; }
    public List<Membre> getMembres() { return new ArrayList<>(membres); }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public void setDuree(int duree) { this.duree = duree; }
    public void setGenre(GenrePiece genre) { this.genre = genre; }
    public void setDescription(String description) { this.description = description; }
    
    // Méthodes
    public void addMembre(Membre membre) {
        if (!membres.contains(membre)) {
            membres.add(membre);
        }
    }
    
    public void removeMembre(Membre membre) {
        membres.remove(membre);
    }
    
    public boolean hasMembre(Membre membre) {
        return membres.contains(membre);
    }
    
    public String getDureeFormatee() {
        int heures = duree / 60;
        int minutes = duree % 60;
        return heures + "h" + (minutes < 10 ? "0" : "") + minutes;
    }
    
    @Override
    public String toString() {
        return titre + " de " + auteur + " (" + genre + ", " + getDureeFormatee() + ")";
    }
    
    public String getInfosCompletes() {
        return "ID: " + id + ", Titre: " + titre + ", Auteur: " + auteur + 
               ", Durée: " + getDureeFormatee() + ", Genre: " + genre + 
               ", Membres: " + membres.size() + ", Description: " + 
               (description.length() > 50 ? description.substring(0, 50) + "..." : description);
    }
}