package com.jusep1983.blackjack.player;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends R2dbcRepository<Player, Long> {
    Mono<Player> findByUserName(String userName);

    Mono<Player> findById(String playerId);

    Flux<Player> findAllByOrderByGamesWonDesc();

    Mono<Void> deleteByUserName(String userName);

    Mono<Player> findByAlias(String userName);
}
