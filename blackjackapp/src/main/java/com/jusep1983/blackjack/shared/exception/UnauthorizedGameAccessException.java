package com.jusep1983.blackjack.shared.exception;

import org.springframework.security.access.AccessDeniedException;

public class UnauthorizedGameAccessException extends AccessDeniedException {
    public UnauthorizedGameAccessException(String message) {
        super(message);
    }
}
