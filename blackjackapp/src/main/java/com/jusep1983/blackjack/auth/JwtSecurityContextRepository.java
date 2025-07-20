package com.jusep1983.blackjack.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {
    private final JwtAuthenticationManager authenticationManager;

    @Autowired
    public JwtSecurityContextRepository(JwtAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Método principal: se ejecuta en cada request.
     * Extrae el token del header Authorization y lo usa para crear un SecurityContext.
     */
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Esperamos un token tipo: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // quitamos "Bearer "

            // Creamos un Authentication fake para pasarlo al authManager
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(token, token);

            // El manager validará el token y devolverá un Authentication válido
            return authenticationManager.authenticate(authToken)
                    .map(SecurityContextImpl::new);
        }
        // Si no hay token válido, no autenticamos
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
