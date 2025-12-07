package com.monentreprise.monprojet.dao;

import com.monentreprise.monprojet.model.*;
import com.monentreprise.monprojet.exceptions.*;
import java.util.List;
import java.util.stream.Collectors;

public class TheatreDAO {
    private TheatreData data;
    
    public TheatreDAO() {
        data = TheatreData.getInstance();
    }
    
    // ========== GESTION DES MEMBRES ==========
    public void ajouterMembre(Membre membre) throws MemberException {
        if (membre.getNom() == null || membre.getNom().trim().isEmpty()) {
            throw new MemberException("Le nom du membre est obligatoire");
        }
        if (membre.getPrenom() == null || membre.getPrenom().trim().isEmpty()) {
            throw new MemberException("Le prénom du membre est obligatoire");
        }
        data.addMembre(membre);
    }
    
    public void modifierMembre(Membre membre) throws MemberException {
        Membre existing = data.findMembreById(membre.getId());
        if (existing != null) {
            existing.setNom(membre.getNom());
            existing.setPrenom(membre.getPrenom());
            existing.setEmail(membre.getEmail());
            existing.setTelephone(membre.getTelephone());
            existing.setRole(membre.getRole());
            existing.setCompetences(membre.getCompetences());
            existing.setDisponibilites(membre.getDisponibilites());
        } else {
            throw new MemberException("Membre non trouvé avec ID: " + membre.getId());
        }
    }
    
    public void supprimerMembre(int id) throws MemberException {
        Membre membre = data.findMembreById(id);
        if (membre != null) {
            // Retirer le membre de toutes les pièces
            data.getPieces().forEach(p -> p.removeMembre(membre));
            // Supprimer le membre
            data.getMembres().remove(membre);
        } else {
            throw new MemberException("Membre non trouvé avec ID: " + id);
        }
    }
    
    // ========== GESTION DES PIÈCES ==========
    public void ajouterPiece(Piece piece) throws TheatreException {
        if (piece.getTitre() == null || piece.getTitre().trim().isEmpty()) {
            throw new TheatreException("Le titre de la pièce est obligatoire");
        }
        if (piece.getAuteur() == null || piece.getAuteur().trim().isEmpty()) {
            throw new TheatreException("L'auteur de la pièce est obligatoire");
        }
        if (piece.getDuree() <= 0) {
            throw new TheatreException("La durée doit être positive");
        }
        data.addPiece(piece);
    }
    
    public void modifierPiece(Piece piece) throws TheatreException {
        Piece existing = data.findPieceById(piece.getId());
        if (existing != null) {
            existing.setTitre(piece.getTitre());
            existing.setAuteur(piece.getAuteur());
            existing.setDuree(piece.getDuree());
            existing.setGenre(piece.getGenre());
            existing.setDescription(piece.getDescription());
        } else {
            throw new TheatreException("Pièce non trouvée avec ID: " + piece.getId());
        }
    }
    
    // ========== GESTION DES REPRÉSENTATIONS ==========
    public void ajouterRepresentation(Representation representation) throws TheatreException {
        if (representation.getPiece() == null) {
            throw new TheatreException("Une représentation doit être associée à une pièce");
        }
        if (representation.getDateHeure().isBefore(java.time.LocalDateTime.now())) {
            throw new TheatreException("La date de représentation ne peut pas être dans le passé");
        }
        if (representation.getPrixBillet() <= 0) {
            throw new TheatreException("Le prix du billet doit être positif");
        }
        if (representation.getCapaciteMax() <= 0) {
            throw new TheatreException("La capacité maximale doit être positive");
        }
        data.addRepresentation(representation);
    }
    
    // ========== GESTION DES RÉSERVATIONS ==========
    public void ajouterReservation(Reservation reservation) throws ReservationException {
        Representation rep = reservation.getRepresentation();
        if (rep.estComplet()) {
            throw new ReservationException("La représentation est complète");
        }
        if (reservation.getNombreBillets() > rep.getPlacesDisponibles()) {
            throw new ReservationException("Nombre de places disponibles insuffisant. " + 
                                          "Places disponibles: " + rep.getPlacesDisponibles());
        }
        if (reservation.getNomClient() == null || reservation.getNomClient().trim().isEmpty()) {
            throw new ReservationException("Le nom du client est obligatoire");
        }
        data.addReservation(reservation);
    }
    
    public void annulerReservation(String reservationId) throws ReservationException {
        Reservation reservation = data.findReservationById(reservationId);
        if (reservation != null) {
            reservation.annuler();
        } else {
            throw new ReservationException("Réservation non trouvée avec ID: " + reservationId);
        }
    }
    
    public void confirmerReservation(String reservationId) throws ReservationException {
        Reservation reservation = data.findReservationById(reservationId);
        if (reservation != null) {
            reservation.confirmer();
        } else {
            throw new ReservationException("Réservation non trouvée avec ID: " + reservationId);
        }
    }
    
    // ========== RECHERCHES ==========
    public List<Membre> rechercherMembres(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            return data.getMembres();
        }
        String critereLower = critere.toLowerCase();
        return data.getMembres().stream()
            .filter(m -> m.getNom().toLowerCase().contains(critereLower) ||
                        m.getPrenom().toLowerCase().contains(critereLower) ||
                        m.getRole().toLowerCase().contains(critereLower) ||
                        m.getEmail().toLowerCase().contains(critereLower))
            .collect(Collectors.toList());
    }
    
    public List<Piece> rechercherPieces(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            return data.getPieces();
        }
        String critereLower = critere.toLowerCase();
        return data.getPieces().stream()
            .filter(p -> p.getTitre().toLowerCase().contains(critereLower) ||
                        p.getAuteur().toLowerCase().contains(critereLower) ||
                        p.getGenre().toString().toLowerCase().contains(critereLower) ||
                        p.getDescription().toLowerCase().contains(critereLower))
            .collect(Collectors.toList());
    }
    
    public List<Representation> rechercherRepresentations(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            return data.getRepresentations();
        }
        String critereLower = critere.toLowerCase();
        return data.getRepresentations().stream()
            .filter(r -> r.getPiece().getTitre().toLowerCase().contains(critereLower) ||
                        r.getLieu().toLowerCase().contains(critereLower))
            .collect(Collectors.toList());
    }
    
    // ========== GETTERS ==========
    public List<Membre> getMembres() { return data.getMembres(); }
    public List<Piece> getPieces() { return data.getPieces(); }
    public List<Representation> getRepresentations() { return data.getRepresentations(); }
    public List<Reservation> getReservations() { return data.getReservations(); }
    
    public Membre getMembreById(int id) { return data.findMembreById(id); }
    public Piece getPieceById(int id) { return data.findPieceById(id); }
    public Representation getRepresentationById(int id) { return data.findRepresentationById(id); }
    public Reservation getReservationById(String id) { return data.findReservationById(id); }
    
    // ========== FILTRES SPÉCIFIQUES ==========
    public List<Membre> getMembresByRole(String role) {
        return data.findMembresByRole(role);
    }
    
    public List<Piece> getPiecesByGenre(GenrePiece genre) {
        return data.findPiecesByGenre(genre);
    }
    
    public List<Representation> getRepresentationsFutures() {
        return data.getRepresentations().stream()
            .filter(r -> r.getDateHeure().isAfter(java.time.LocalDateTime.now()))
            .collect(Collectors.toList());
    }
    
    public List<Representation> getRepresentationsAvecPlaces() {
        return data.getRepresentations().stream()
            .filter(r -> !r.estComplet())
            .collect(Collectors.toList());
    }
    
    // ========== STATISTIQUES ==========
    public int getTotalMembres() { return data.getTotalMembres(); }
    public int getTotalPieces() { return data.getTotalPieces(); }
    public int getTotalRepresentations() { return data.getTotalRepresentations(); }
    public int getTotalReservations() { return data.getTotalReservations(); }
    public double getRecetteTotale() { return data.getRecetteTotale(); }
    
    // ========== VALIDATIONS ==========
    public boolean membreExists(int id) {
        return data.findMembreById(id) != null;
    }
    
    public boolean pieceExists(int id) {
        return data.findPieceById(id) != null;
    }
    
    public boolean representationExists(int id) {
        return data.findRepresentationById(id) != null;
    }
}