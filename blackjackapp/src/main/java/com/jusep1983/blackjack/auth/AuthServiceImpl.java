package com.jusep1983.blackjack.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jusep1983.blackjack.shared.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final Algorithm algorithm;
    private static final int EXPIRATION_MINUTES = 60;

    public AuthServiceImpl(@Value("${jwt.secret}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    /**
     * Genera un JWT con userName y rol como claims
     */
    @Override
    public String generateToken(String userName, Role role) {
        Instant now = Instant.now();
        String token = JWT.create()
                .withSubject(userName)
                .withClaim("role", role.name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES)))
                .sign(algorithm);
        log.info("JWT generated for user '{}', expires in {} minutes", userName, EXPIRATION_MINUTES);
        return token;
    }

    /**
     * Verifica el token y devuelve el userName (lanza JWTVerificationException si falla)
     */
    @Override
    public String validateTokenAndGetUserName(String token) {
        String userName = JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
        log.debug("Token validated successfully for user '{}'", userName);
        return userName;
    }

    /**
     * Obtiene el rol del usuario desde el claim "role"
     */
    @Override
    public Role getRoleFromToken(String token) {
        String roleStr = JWT.require(algorithm)
                .build()
                .verify(token)
                .getClaim("role")
                .asString();
        log.debug("Extracted role '{}' from token", roleStr);
        return Role.valueOf(roleStr);
    }

}
