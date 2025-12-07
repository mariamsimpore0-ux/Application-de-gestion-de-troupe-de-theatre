package com.monentreprise.monprojet.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private String id;
    private String nomClient;
    private String emailClient;
    private int nombreBillets;
    private LocalDateTime dateReservation;
    private String etat; // CONFIRMEE, ANNULEE
    private Representation representation;
    
    public Reservation(String nomClient, String emailClient, 
                      int nombreBillets, Representation representation) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.nomClient = nomClient;
        this.emailClient = emailClient;
        this.nombreBillets = nombreBillets;
        this.dateReservation = LocalDateTime.now();
        this.etat = "CONFIRMEE";
        this.representation = representation;
    }
    
    // Getters
    public String getId() { return id; }
    public String getNomClient() { return nomClient; }
    public String getEmailClient() { return emailClient; }
    public int getNombreBillets() { return nombreBillets; }
    public LocalDateTime getDateReservation() { return dateReservation; }
    public String getEtat() { return etat; }
    public Representation getRepresentation() { return representation; }
    
    // Setters
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public void setEmailClient(String emailClient) { this.emailClient = emailClient; }
    public void setNombreBillets(int nombreBillets) { this.nombreBillets = nombreBillets; }
    public void setEtat(String etat) { this.etat = etat; }
    public void setRepresentation(Representation representation) { this.representation = representation; }
    
    // Méthodes
    public void annuler() {
        this.etat = "ANNULEE";
    }
    
    public void confirmer() {
        this.etat = "CONFIRMEE";
    }
    
    public double calculerMontant(double prixBillet) {
        return nombreBillets * prixBillet;
    }
    
    public double getMontantTotal() {
        return calculerMontant(representation.getPrixBillet());
    }
    
    public String getDateReservationFormatee() {
        return dateReservation.getDayOfMonth() + "/" + dateReservation.getMonthValue() + 
               "/" + dateReservation.getYear() + " " + 
               dateReservation.getHour() + "h" + 
               (dateReservation.getMinute() < 10 ? "0" : "") + dateReservation.getMinute();
    }
    
    @Override
    public String toString() {
        return "Réservation #" + id + " - " + nomClient + 
               " (" + nombreBillets + " billets) - " + etat + 
               " - " + getMontantTotal() + "€";
    }
    
    public String getInfosCompletes() {
        return "ID: " + id + ", Client: " + nomClient + 
               ", Email: " + emailClient + ", Billets: " + nombreBillets + 
               ", Date résa: " + getDateReservationFormatee() + 
               ", État: " + etat + ", Montant: " + getMontantTotal() + "€" +
               ", Représentation: " + representation.getPiece().getTitre();
    }
}