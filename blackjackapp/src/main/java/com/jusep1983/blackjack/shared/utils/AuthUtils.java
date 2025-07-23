package com.jusep1983.blackjack.shared.utils;

import com.jusep1983.blackjack.player.Player;
import com.jusep1983.blackjack.player.PlayerRepository;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

@Slf4j
public class AuthUtils {
    private AuthUtils() {
    }

    public static Mono<String> getCurrentUserName() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .doOnNext(user -> log.debug("Reactive context resolved user '{}'", user));
    }

    public static Mono<Player> getCurrentPlayer(PlayerRepository playerRepository) {
        return getCurrentUserName()
                .flatMap(userName ->
                        playerRepository.findByUserName(userName)
                                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found: " + userName)))
                );
    }

}
