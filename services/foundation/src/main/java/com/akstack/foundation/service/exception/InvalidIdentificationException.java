package com.akstack.foundation.service.exception;

public class InvalidIdentificationException extends RuntimeException {
    
    public InvalidIdentificationException(String message) {
        super(message);
    }
    
    public InvalidIdentificationException(String identificationType, String identificationNumber) {
        super(String.format("Invalid %s: %s", identificationType, identificationNumber));
    }
}
