package com.jusep1983.blackjack.game;

import reactor.core.publisher.Mono;

public interface GameService {
    Mono<Game> createGame(String playerName);

    Mono<Game> getGameById(String id, String userName);

    Mono<Void> deleteGameById(String id, String userName);

    Mono<Game> playerHit(String gameId, String userName);

    public Mono<Game> playerStand(String gameId, String userName);
}
