package com.jusep1983.blackjack.shared.exception;

public class UsernameAlreadyExistsException extends IllegalArgumentException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
