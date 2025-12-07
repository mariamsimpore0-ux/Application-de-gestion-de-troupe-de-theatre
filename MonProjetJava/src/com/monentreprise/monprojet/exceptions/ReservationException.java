package com.monentreprise.monprojet.exceptions;

public class ReservationException extends TheatreException {
    public ReservationException(String message) {
        super(message);
    }
    
    public ReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}