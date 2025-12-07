package com.monentreprise.monprojet.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Representation {
    private int id;
    private LocalDateTime dateHeure;
    private String lieu;
    private double prixBillet;
    private int capaciteMax;
    private Piece piece;
    private List<Reservation> reservations;
    
    public Representation(int id, LocalDateTime dateHeure, String lieu, 
                         double prixBillet, int capaciteMax, Piece piece) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.lieu = lieu;
        this.prixBillet = prixBillet;
        this.capaciteMax = capaciteMax;
        this.piece = piece;
        this.reservations = new ArrayList<>();
    }
    
    // Getters
    public int getId() { return id; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public String getLieu() { return lieu; }
    public double getPrixBillet() { return prixBillet; }
    public int getCapaciteMax() { return capaciteMax; }
    public Piece getPiece() { return piece; }
    public List<Reservation> getReservations() { return new ArrayList<>(reservations); }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public void setPrixBillet(double prixBillet) { this.prixBillet = prixBillet; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }
    public void setPiece(Piece piece) { this.piece = piece; }
    
    // Méthodes
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }
    
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }
    
    public int getPlacesReservees() {
        int total = 0;
        for (Reservation r : reservations) {
            if (r.getEtat().equals("CONFIRMEE")) {
                total += r.getNombreBillets();
            }
        }
        return total;
    }
    
    public int getPlacesDisponibles() {
        return capaciteMax - getPlacesReservees();
    }
    
    public boolean estComplet() {
        return getPlacesDisponibles() <= 0;
    }
    
    public double getRecetteTotale() {
        double total = 0;
        for (Reservation r : reservations) {
            if (r.getEtat().equals("CONFIRMEE")) {
                total += r.calculerMontant(prixBillet);
            }
        }
        return total;
    }
    
    public String getDateFormatee() {
        return dateHeure.getDayOfMonth() + "/" + dateHeure.getMonthValue() + 
               "/" + dateHeure.getYear() + " " + 
               dateHeure.getHour() + "h" + 
               (dateHeure.getMinute() < 10 ? "0" : "") + dateHeure.getMinute();
    }
    
    @Override
    public String toString() {
        return piece.getTitre() + " - " + getDateFormatee() + " à " + lieu + 
               " (" + getPlacesDisponibles() + "/" + capaciteMax + " places)";
    }
    
    public String getInfosCompletes() {
        return "ID: " + id + ", Pièce: " + piece.getTitre() + 
               ", Date: " + getDateFormatee() + ", Lieu: " + lieu + 
               ", Prix: " + prixBillet + "€, Capacité: " + capaciteMax + 
               ", Places dispo: " + getPlacesDisponibles() + 
               ", Recette: " + getRecetteTotale() + "€";
    }
}