package com.monentreprise.monprojet.exceptions;

public class MemberException extends TheatreException {
    public MemberException(String message) {
        super(message);
    }
    
    public MemberException(String message, Throwable cause) {
        super(message, cause);
    }
}