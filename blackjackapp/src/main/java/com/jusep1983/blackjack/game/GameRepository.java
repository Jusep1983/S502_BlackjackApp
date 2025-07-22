package com.jusep1983.blackjack.game;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {
    Flux<Game> findAllByUserNameOrderByCreatedAtAsc(String userName);
}
