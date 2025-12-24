package com.gymtracker.app.exception;

public class PlanWithSameNameAlreadyExistsException extends DomainException {
    public PlanWithSameNameAlreadyExistsException(String message) {
        super(message);
    }
}
