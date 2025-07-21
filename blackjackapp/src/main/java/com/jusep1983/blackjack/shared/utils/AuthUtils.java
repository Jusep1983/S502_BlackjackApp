package com.jusep1983.blackjack.shared.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

public class AuthUtils {
    private AuthUtils() {

    }

    public static Mono<String> getCurrentUserName() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName);
    }
}
