package com.jusep1983.blackjack.admin;

import com.jusep1983.blackjack.player.Player;
import com.jusep1983.blackjack.player.PlayerRepository;
import com.jusep1983.blackjack.game.GameRepository;
import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Override
    @PreAuthorize("hasRole('SUPER_USER')")
    public Mono<Player> setRole(String playerId, Role newRole) {
        log.info("Request to change role of player with ID '{}' to '{}'", playerId, newRole);

        if (newRole == Role.SUPER_USER) {
            log.warn("Attempt to assign forbidden role SUPER_USER to player ID '{}'", playerId);
            return Mono.error(new IllegalArgumentException("Assigning SUPER_USER role is not allowed via this endpoint"));
        }

        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Player with ID '{}' not found for role update", playerId);
                    return Mono.error(new PlayerNotFoundException(playerId));
                }))
                .flatMap(player -> {
                    player.setRole(newRole);
                    log.info("Updating role for player '{}' to '{}'", player.getUserName(), newRole);
                    return playerRepository.save(player);
                });
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public Mono<Void> deletePlayerAndGames(String userName) {
        log.info("Deleting player '{}' and all their games", userName);

        return gameRepository.deleteAllByUserName(userName)
                .doOnSuccess(v -> log.debug("All games for player '{}' deleted", userName))
                .then(playerRepository.deleteByUserName(userName))
                .doOnSuccess(v -> log.info("Player '{}' deleted", userName))
                .doOnError(e -> log.error("Error deleting player '{}' or their games: {}", userName, e.getMessage()))
                .then();
    }
}
