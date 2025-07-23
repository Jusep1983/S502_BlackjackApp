package com.jusep1983.blackjack.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jusep1983.blackjack.shared.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private static final int EXPIRATION_MINUTES = 60;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    /**
     * Genera un JWT con username y rol como claims
     */
    public String generateToken(String username, Role role) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role.name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES)))
                .sign(algorithm);
    }

    /**
     * Verifica el token y devuelve el username (lanza JWTVerificationException si falla)
     */
    public String validateTokenAndGetUsername(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }

    /**
     * Obtiene el rol del usuario desde el claim "role"
     */
    public Role getRoleFromToken(String token) {
        String roleStr = JWT.require(algorithm)
                .build()
                .verify(token)
                .getClaim("role")
                .asString();
        return Role.valueOf(roleStr);
    }

    public Mono<String> getCurrentUserNameReactive() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName);
    }

}
