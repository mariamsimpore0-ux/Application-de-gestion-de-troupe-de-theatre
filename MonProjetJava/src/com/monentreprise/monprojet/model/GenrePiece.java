package com.monentreprise.monprojet.model;

public enum GenrePiece {
    COMEDIE("Comédie"),
    DRAME("Drame"),
    TRAGEDIE("Tragédie"),
    TRAGICOMEDIE("Tragi-comédie"),
    FARCE("Farce"),
    MELODRAME("Mélodrame"),
    MUSICAL("Musical");
    
    private final String nom;
    
    private GenrePiece(String nom) {
        this.nom = nom;
    }
    
    public String getNom() {
        return nom;
    }
    
    @Override
    public String toString() {
        return nom;
    }
}