package com.monentreprise.monprojet.exceptions;

public class TheatreException extends Exception {
    public TheatreException(String message) {
        super(message);
    }
    
    public TheatreException(String message, Throwable cause) {
        super(message, cause);
    }
}