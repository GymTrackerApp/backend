package com.gymtracker.app.exception;

public class UserDoesNotExistException extends DomainException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
