package com.jusep1983.blackjack.game;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {
    Flux<Game> findAllByUserNameOrderByCreatedAtAsc(String userName);
    Mono<Void> deleteAllByUserName(String userName);
    
}
