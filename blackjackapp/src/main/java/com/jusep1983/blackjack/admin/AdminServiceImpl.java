package com.jusep1983.blackjack.admin;

import com.jusep1983.blackjack.player.Player;
import com.jusep1983.blackjack.player.PlayerRepository;
import com.jusep1983.blackjack.game.GameRepository;
import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Override
    public Mono<Player> setRole(String playerId, Role newRole) {
        if (newRole == Role.SUPER_USER) {
            return Mono.error(new IllegalArgumentException("Assigning SUPER_USER role is not allowed via this endpoint"));
        }

        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .flatMap(player -> {
                    player.setRole(newRole);
                    return playerRepository.save(player);
                });
    }

    @Override
    public Mono<Void> deletePlayerAndGames(String userName) {
        return gameRepository.deleteAllByUserName(userName)
                .then(playerRepository.deleteByUserName(userName)).then();

    }
}
