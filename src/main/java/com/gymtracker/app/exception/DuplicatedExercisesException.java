package com.gymtracker.app.exception;

public class DuplicatedExercisesException extends DomainException {
    public DuplicatedExercisesException(String message) {
        super(message);
    }
}
