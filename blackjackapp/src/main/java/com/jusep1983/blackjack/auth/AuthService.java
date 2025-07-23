package com.jusep1983.blackjack.auth;

import com.jusep1983.blackjack.shared.enums.Role;

public interface AuthService {
    String generateToken(String username, Role role);
    String validateTokenAndGetUserName(String token);
    Role getRoleFromToken(String token);
}
