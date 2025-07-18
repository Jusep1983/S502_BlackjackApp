package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.shared.enums.GameResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerService {

    Mono<Player> getById(long id);
    Mono<Player> createPlayer(Player player);
    Mono<Player> updateName(Long id, String newName);
    Mono<Player> getByName(String name);
    Mono<Player> updateStats(String playerName, GameResult gameResult);
    Flux<PlayerRankingDTO> getRanking();
}
