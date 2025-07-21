package com.jusep1983.blackjack.game;

import reactor.core.publisher.Mono;

public interface GameService {

    Mono<Game> createGame();

    Mono<Game> getGameById(String id);

    Mono<Game> playerHit(String gameId);

    Mono<Game> playerStand(String gameId);

    Mono<Void> deleteGameById(String id);
}
