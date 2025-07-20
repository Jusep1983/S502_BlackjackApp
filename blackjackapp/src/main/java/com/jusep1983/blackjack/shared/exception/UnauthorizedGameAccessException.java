package com.jusep1983.blackjack.shared.exception;

public class UnauthorizedGameAccessException extends RuntimeException {
    public UnauthorizedGameAccessException(String message) {
        super(message);
    }
}
