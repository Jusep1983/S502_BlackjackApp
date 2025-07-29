package com.jusep1983.blackjack.admin;

import com.jusep1983.blackjack.player.Player;
import com.jusep1983.blackjack.player.PlayerRepository;
import com.jusep1983.blackjack.game.GameRepository;
import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import com.jusep1983.blackjack.shared.exception.UnauthorizedAccessException;
import com.jusep1983.blackjack.shared.utils.AuthUtils;
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
                    if (player.getRole() == Role.SUPER_USER) {
                        log.warn("Attempt to modify role of SUPER_USER '{}'", player.getUserName());
                        return Mono.error(new IllegalArgumentException("Cannot change role of a SUPER_USER"));
                    }
                    player.setRole(newRole);
                    log.info("Updating role for player '{}' to '{}'", player.getUserName(), newRole);
                    return playerRepository.save(player);
                });
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public Mono<Void> deletePlayerAndGames(String targetUserName) {
        return AuthUtils.getCurrentUserName()
                .flatMap(currentUserName ->
                        playerRepository.findByUserName(currentUserName)
                                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Authenticated player not found: " + currentUserName)))
                                .zipWith(
                                        playerRepository.findByUserName(targetUserName)
                                                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Target player not found: " + targetUserName)))
                                )
                )
                .flatMap(tuple -> {
                    Player currentUser = tuple.getT1();
                    Player targetPlayer = tuple.getT2();

                    if (targetPlayer.getRole() == Role.SUPER_USER) {
                        log.warn("Attempt to delete SUPER_USER '{}'", targetUserName);
                        return Mono.error(new IllegalArgumentException("Cannot delete a SUPER_USER"));
                    }

                    if (currentUser.getRole() == Role.ADMIN && targetPlayer.getRole() == Role.ADMIN) {
                        log.warn("ADMIN '{}' attempted to delete another ADMIN '{}'", currentUser.getUserName(), targetUserName);
                        return Mono.error(new UnauthorizedAccessException("Admins cannot delete other Admins"));
                    }

                    log.info("Deleting player '{}' and all their games", targetUserName);

                    return gameRepository.deleteAllByUserName(targetUserName)
                            .then(playerRepository.deleteByUserName(targetUserName))
                            .doOnSuccess(v -> log.info("Player '{}' and all games deleted", targetUserName));
                })
                .doOnError(e -> log.error("Error deleting player '{}': {}", targetUserName, e.getMessage()))
                .then();
    }
}
