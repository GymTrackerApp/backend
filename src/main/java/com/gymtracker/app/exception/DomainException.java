package com.gymtracker.app.exception;

import org.springframework.http.HttpStatus;

public class DomainException extends BaseKeyException {
    public DomainException(String subKey, Object... args) {
        super("domain-exception." + subKey, HttpStatus.BAD_REQUEST, args);
    }
}
