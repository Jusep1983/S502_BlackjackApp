package com.jusep1983.blackjack.auth;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final AuthService authService;

    @Autowired
    public JwtAuthenticationManager(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        // Aquí usamos el JwtService para validar el token y extraer info
        try {
            String userName = authService.validateTokenAndGetUserName(token);
            String role = String.valueOf(authService.getRoleFromToken(token));
            log.debug("Token validated successfully for user '{}'", userName);
            // Creamos un Authentication válido con el userName y rol
            return Mono.just(
                    new UsernamePasswordAuthenticationToken(
                            userName,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    )
            );
        } catch (TokenExpiredException ex) {
            log.warn("Token expired: {}", ex.getMessage());
        } catch (SignatureVerificationException ex) {
            log.warn("Invalid token signature: {}", ex.getMessage());
        } catch (JWTDecodeException ex) {
            log.warn("Malformed token: {}", ex.getMessage());
        } catch (JWTVerificationException ex) {
            log.warn("JWT verification failed: {}", ex.getMessage());
        }
        return Mono.empty();
    }

}
