package com.jusep1983.blackjack.admin;

import com.jusep1983.blackjack.player.Player;
import com.jusep1983.blackjack.shared.enums.Role;
import reactor.core.publisher.Mono;

public interface AdminService {
    Mono<Player> setRole(String playerId, Role newRole);
    Mono<Void> deletePlayerAndGames(String userName);
}
