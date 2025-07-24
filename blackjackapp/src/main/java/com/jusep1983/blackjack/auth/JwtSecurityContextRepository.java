package com.jusep1983.blackjack.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {
    private final JwtAuthenticationManager authenticationManager;

    @Autowired
    public JwtSecurityContextRepository(JwtAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Metodo principal: se ejecuta en cada request.
     * Extrae el token del header Authorization y lo usa para crear un SecurityContext.
     */
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("Received JWT token for authentication");

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(token, token);

            return authenticationManager.authenticate(authToken)
                    .doOnNext(auth -> log.info("Token authentication succeeded for user '{}'", auth.getName()))
                    .doOnError(error -> log.warn("Token authentication failed: {}", error.getMessage()))
                    .map(SecurityContextImpl::new);
        }
        log.debug("No valid Authorization header found, skipping authentication");
        return Mono.empty();
    }

    /**
     * No se guarda nada. JWT es stateless.
     */
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty(); // No persistimos el contexto
    }

}
