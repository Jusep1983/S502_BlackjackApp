package com.jusep1983.blackjack.passwordCheck;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        String rawPassword = "1111";
        String hash = new BCryptPasswordEncoder().encode(rawPassword);
        System.out.println("Nuevo hash: " + hash);
    }
}
