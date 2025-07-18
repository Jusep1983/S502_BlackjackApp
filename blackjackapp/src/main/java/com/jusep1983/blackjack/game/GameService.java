package com.jusep1983.blackjack.game;

import reactor.core.publisher.Mono;

public interface GameService {
    Mono<Game> createGame(String playerName);
    Mono<Game> getGameById(String id);
    Mono<Void> deleteGameById(String id);
    Mono<Game> playerHit(String gameId);
    Mono<Game> playerStand(String gameId);
}
