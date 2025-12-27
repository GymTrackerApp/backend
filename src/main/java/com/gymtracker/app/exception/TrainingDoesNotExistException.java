package com.gymtracker.app.exception;

public class TrainingDoesNotExistException extends DomainException {
    public TrainingDoesNotExistException(String message) {
        super(message);
    }
}
