package com.jusep1983.blackjack.player;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends R2dbcRepository<Player,Long> {
    Mono<Player> findByUserName(String userName);
    Mono<Player> findById(String playerId);
    Flux<Player> findAllByOrderByGamesWonDesc();
    Mono<Player> deleteByUserName(String userName);
}
