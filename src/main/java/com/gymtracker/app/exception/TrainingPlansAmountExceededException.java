package com.gymtracker.app.exception;

public class TrainingPlansAmountExceededException extends DomainException {
    public TrainingPlansAmountExceededException(String key, Object... args) {
        super(key, args);
    }
}
