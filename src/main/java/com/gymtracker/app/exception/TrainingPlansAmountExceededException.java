package com.gymtracker.app.exception;

public class TrainingPlansAmountExceededException extends DomainException {
    public TrainingPlansAmountExceededException(String message) {
        super(message);
    }
}
