package com.monentreprise.monprojet.dao;

import com.monentreprise.monprojet.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TheatreData {
    private static TheatreData instance;
    private List<Membre> membres;
    private List<Piece> pieces;
    private List<Representation> representations;
    private List<Reservation> reservations;
    
    private TheatreData() {
        membres = new ArrayList<>();
        pieces = new ArrayList<>();
        representations = new ArrayList<>();
        reservations = new ArrayList<>();
        initialiserDonneesTest();
    }
    
    public static synchronized TheatreData getInstance() {
        if (instance == null) {
            instance = new TheatreData();
        }
        return instance;
    }
    
    private void initialiserDonneesTest() {
        // Membres
        Membre m1 = new Membre(1, "Dupont", "Jean", "jean.dupont@email.com", 
                              "0123456789", "Acteur");
        m1.addCompetence("Comédie");
        m1.addCompetence("Chant");
        m1.addDisponibilite("2024-12-10");
        m1.addDisponibilite("2024-12-15");
        
        Membre m2 = new Membre(2, "Martin", "Sophie", "sophie.martin@email.com", 
                              "0987654321", "Metteur en scène");
        m2.addCompetence("Direction d'acteurs");
        m2.addCompetence("Scénographie");
        
        Membre m3 = new Membre(3, "Bernard", "Pierre", "pierre.bernard@email.com", 
                              "0654321987", "Technicien");
        m3.addCompetence("Lumières");
        m3.addCompetence("Son");
        
        membres.add(m1);
        membres.add(m2);
        membres.add(m3);
        
        // Pièces
        Piece p1 = new Piece(1, "Le Malade Imaginaire", "Molière", 120, 
                            GenrePiece.COMEDIE, "Une comédie sur la médecine et les médecins");
        p1.addMembre(m1);
        p1.addMembre(m2);
        
        Piece p2 = new Piece(2, "Antigone", "Jean Anouilh", 90, 
                            GenrePiece.TRAGEDIE, "Adaptation moderne de la tragédie grecque");
        p2.addMembre(m1);
        
        pieces.add(p1);
        pieces.add(p2);
        
        // Représentations
        Representation r1 = new Representation(1, 
            LocalDateTime.now().plusDays(7).withHour(20).withMinute(0),
            "Théâtre Municipal", 25.0, 100, p1);
        
        Representation r2 = new Representation(2,
            LocalDateTime.now().plusDays(14).withHour(19).withMinute(30),
            "Salle des Arts", 30.0, 80, p2);
            
        representations.add(r1);
        representations.add(r2);
        
        // Réservations
        Reservation res1 = new Reservation("Client 1", "client1@email.com", 2, r1);
        Reservation res2 = new Reservation("Client 2", "client2@email.com", 4, r1);
        Reservation res3 = new Reservation("Client 3", "client3@email.com", 1, r2);
        
        reservations.add(res1);
        reservations.add(res2);
        reservations.add(res3);
        
        r1.addReservation(res1);
        r1.addReservation(res2);
        r2.addReservation(res3);
    }
    
    // Getters
    public List<Membre> getMembres() { return new ArrayList<>(membres); }
    public List<Piece> getPieces() { return new ArrayList<>(pieces); }
    public List<Representation> getRepresentations() { return new ArrayList<>(representations); }
    public List<Reservation> getReservations() { return new ArrayList<>(reservations); }
    
    // Méthodes d'ajout
    public void addMembre(Membre membre) {
        membre.setId(generateMembreId());
        membres.add(membre);
    }
    
    public void addPiece(Piece piece) {
        piece.setId(generatePieceId());
        pieces.add(piece);
    }
    
    public void addRepresentation(Representation representation) {
        representation.setId(generateRepresentationId());
        representations.add(representation);
    }
    
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.getRepresentation().addReservation(reservation);
    }
    
    // Méthodes de recherche
    public Membre findMembreById(int id) {
        return membres.stream()
            .filter(m -> m.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public Piece findPieceById(int id) {
        return pieces.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public Representation findRepresentationById(int id) {
        return representations.stream()
            .filter(r -> r.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public Reservation findReservationById(String id) {
        return reservations.stream()
            .filter(r -> r.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    // Méthodes de filtrage
    public List<Membre> findMembresByRole(String role) {
        return membres.stream()
            .filter(m -> m.getRole().equalsIgnoreCase(role))
            .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Piece> findPiecesByGenre(GenrePiece genre) {
        return pieces.stream()
            .filter(p -> p.getGenre() == genre)
            .collect(java.util.stream.Collectors.toList());
    }
    
    // Génération d'IDs
    private int generateMembreId() {
        return membres.stream().mapToInt(Membre::getId).max().orElse(0) + 1;
    }
    
    private int generatePieceId() {
        return pieces.stream().mapToInt(Piece::getId).max().orElse(0) + 1;
    }
    
    private int generateRepresentationId() {
        return representations.stream().mapToInt(Representation::getId).max().orElse(0) + 1;
    }
    
    // Statistiques
    public int getTotalMembres() {
        return membres.size();
    }
    
    public int getTotalPieces() {
        return pieces.size();
    }
    
    public int getTotalRepresentations() {
        return representations.size();
    }
    
    public int getTotalReservations() {
        return reservations.size();
    }
    
    public double getRecetteTotale() {
        return representations.stream()
            .mapToDouble(Representation::getRecetteTotale)
            .sum();
    }
}