package com.jusep1983.blackjack.auth;

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
    }
}
