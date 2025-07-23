package com.jusep1983.blackjack.auth;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtService jwtService;

    @Autowired
    public JwtAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        // Aquí usamos el JwtService para validar el token y extraer info
        try {
            String userName = jwtService.validateTokenAndGetUsername(token);
            String role = String.valueOf(jwtService.getRoleFromToken(token));
            // Creamos un Authentication válido con el userName y rol
            return Mono.just(
                    new UsernamePasswordAuthenticationToken(
                            userName,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    )
            );
        } catch (TokenExpiredException ex) {
            System.out.println("Token expired: " + ex.getMessage());
        } catch (SignatureVerificationException ex) {
            System.out.println("Invalid token signature: " + ex.getMessage());
        } catch (JWTDecodeException ex) {
            System.out.println("Malformed token: " + ex.getMessage());
        } catch (JWTVerificationException ex) {
            System.out.println("JWT verification failed: " + ex.getMessage());
        }
        return Mono.empty();
    }

}
