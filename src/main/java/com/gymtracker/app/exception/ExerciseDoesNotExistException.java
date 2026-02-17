package com.gymtracker.app.exception;

public class ExerciseDoesNotExistException extends DomainException {
    public ExerciseDoesNotExistException(String key) {
        super(key);
    }
}
