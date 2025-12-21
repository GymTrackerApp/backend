package com.gymtracker.app.exception;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }}
