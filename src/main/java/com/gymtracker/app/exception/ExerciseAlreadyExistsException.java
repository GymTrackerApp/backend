package com.gymtracker.app.exception;

public class ExerciseAlreadyExistsException extends DomainException {
    public ExerciseAlreadyExistsException(String message) {
        super(message);
    }
}
