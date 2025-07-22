package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.player.dto.CreatePlayerDTO;
import com.jusep1983.blackjack.player.dto.PlayerRankingDTO;
import com.jusep1983.blackjack.shared.enums.GameResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerService {

    Mono<Player> getById(long id);
    Mono<Player> createPlayer(CreatePlayerDTO createPlayerDTO);
    Mono<Player> updateAlias(String newAlias);
    Mono<Player> getByName(String name);
    Mono<Player> getCurrentPlayer();
    Mono<Player> updateStats(String playerName, GameResult gameResult);
    Flux<PlayerRankingDTO> getRanking();
}
