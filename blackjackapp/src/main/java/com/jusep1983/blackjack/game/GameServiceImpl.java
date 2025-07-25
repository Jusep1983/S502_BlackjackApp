package com.jusep1983.blackjack.game;

import com.jusep1983.blackjack.deck.Card;
import com.jusep1983.blackjack.deck.Deck;
import com.jusep1983.blackjack.hand.Hand;
import com.jusep1983.blackjack.deck.DeckService;
import com.jusep1983.blackjack.hand.HandService;
import com.jusep1983.blackjack.player.PlayerService;
import com.jusep1983.blackjack.shared.enums.GameResult;
import com.jusep1983.blackjack.shared.enums.GameStatus;
import com.jusep1983.blackjack.shared.exception.GameAlreadyFinishedException;
import com.jusep1983.blackjack.shared.exception.GameNotFoundException;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import com.jusep1983.blackjack.shared.exception.UnauthorizedGameAccessException;
import lombok.RequiredArgsConstructor;
import com.jusep1983.blackjack.shared.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final DeckService deckService;
    private final HandService handService;
    private final PlayerService playerService;


    @Override
    public Mono<Game> createGame() {
        return AuthUtils.getCurrentUserName()
                .flatMap(userName ->
                        playerService.getByName(userName)
                                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found: " + userName)))
                                .flatMap(player -> {
                                    log.info("Creating new game for player '{}'", userName); // player found, starting game
                                    Game game = new Game();
                                    game.setUserName(userName);
                                    game.setGameStatus(GameStatus.NEW);
                                    game.setCreatedAt(LocalDateTime.now());
                                    // Crear y preparar mazo
                                    Deck deck = deckService.createDeck();
                                    deckService.shuffleDeck(deck);
                                    game.setDeck(deck);
                                    // Manos iniciales
                                    game.setPlayerHand(new Hand());
                                    game.setDealerHand(new Hand());
                                    // Repartir 2 cartas a jugador y dealer
                                    handService.addCardToHand(game.getPlayerHand(), deckService.drawCard(deck));
                                    handService.addCardToHand(game.getPlayerHand(), deckService.drawCard(deck));
                                    handService.addCardToHand(game.getDealerHand(), deckService.drawCard(deck));
                                    handService.addCardToHand(game.getDealerHand(), deckService.drawCard(deck));
                                    log.debug("Cards dealt to player and dealer for '{}'", userName); // deck and hands ready
                                    // Calcular puntos iniciales
                                    evaluateInitStatus(game);
                                    return gameRepository.save(game)
                                            .doOnSuccess(saved -> log.info("Game '{}' saved for player '{}'", saved.getId(), userName)) // game persisted
                                            .doOnError(error -> log.error("Failed to save game for '{}': {}", userName, error.getMessage())) // save failed
                                            .flatMap(saved -> {
                                                if (saved.getGameStatus() == GameStatus.FINISHED) {
                                                    log.info("Game '{}' finished instantly. Updating stats for '{}'", saved.getId(), userName);
                                                    return playerService.updateStats(userName, saved.getGameResult())
                                                            .thenReturn(saved);
                                                }
                                                return Mono.just(saved);
                                            });
                                })
                );
    }

    private void evaluateInitStatus(Game game) {
        int playerPoints = handService.calculatePoints(game.getPlayerHand());
        int dealerPoints = handService.calculatePoints(game.getDealerHand());
        game.setPlayerPoints(playerPoints);
        game.setDealerPoints(dealerPoints);

        if (playerPoints == 21 && dealerPoints != 21) {
            game.setGameStatus(GameStatus.FINISHED);
            game.setGameResult(GameResult.PLAYER_WIN);
        } else if (dealerPoints == 21 && playerPoints != 21) {
            game.setGameStatus(GameStatus.FINISHED);
            game.setGameResult(GameResult.DEALER_WIN);
        } else if (dealerPoints == 21 && playerPoints == 21) {
            game.setGameStatus(GameStatus.FINISHED);
            game.setGameResult(GameResult.TIE);
        } else {
            game.setGameStatus(GameStatus.IN_PROGRESS);
        }
    }

    @Override
    public Mono<Game> getGameById(String id) {
        return AuthUtils.getCurrentUserName()
                .flatMap(currentUser -> {
                    log.debug("Fetching game '{}' for user '{}'", id, currentUser);
                    return findGameOr404(id)
                            .flatMap(game -> requireOwnership(game, currentUser));
                });
    }

    @Override
    public Mono<Void> deleteGameById(String id) {
        return AuthUtils.getCurrentUserName()
                .flatMap(currentUser -> {
                    log.info("User '{}' requested deletion of game '{}'", currentUser, id);
                    return findGameOr404(id)
                            .flatMap(game -> requireOwnership(game, currentUser))
                            .flatMap(g -> gameRepository.deleteById(id)
                                    .doOnSuccess(unused -> log.info("Game '{}' deleted by user '{}'", id, currentUser))
                                    .doOnError(e -> log.error("Error deleting game '{}': {}", id, e.getMessage())));
                });
    }

    @Override
    public Mono<Game> playerHit(String gameId) {
        return AuthUtils.getCurrentUserName()
                .flatMap(currentUser ->
                        findGameOr404(gameId)
                                .flatMap(game -> requireOwnership(game, currentUser))
                                .flatMap(game -> {
                                    if (game.getGameStatus() == GameStatus.FINISHED) {
                                        log.warn("Player '{}' tried to hit on a finished game '{}'", currentUser, gameId);
                                        return Mono.error(new GameAlreadyFinishedException("Game has already finished"));
                                    }

                                    Deck deck = game.getDeck();
                                    Card card = deckService.drawCard(deck);
                                    handService.addCardToHand(game.getPlayerHand(), card);

                                    log.debug("Player '{}' drew card {} in game '{}'", currentUser, card, gameId);

                                    int points = handService.calculatePoints(game.getPlayerHand());
                                    game.setPlayerPoints(points);

                                    if (points > 21) {
                                        log.info("Player '{}' busted in game '{}'", currentUser, gameId);
                                        game.setGameStatus(GameStatus.FINISHED);
                                        game.setGameResult(GameResult.DEALER_WIN);
                                        return gameRepository.save(game)
                                                .flatMap(g -> playerService.updateStats(g.getUserName(), GameResult.DEALER_WIN)
                                                        .thenReturn(g));
                                    } else {
                                        game.setGameStatus(GameStatus.IN_PROGRESS);
                                        return gameRepository.save(game);
                                    }
                                })
                );
    }

    @Override
    public Mono<Game> playerStand(String gameId) {
        return AuthUtils.getCurrentUserName()
                .flatMap(currentUser ->
                        findGameOr404(gameId)
                                .flatMap(game -> requireOwnership(game, currentUser))
                                .flatMap(game -> {
                                    Deck deck = game.getDeck();
                                    // Dealer roba hasta tener al menos 17
                                    while (handService.calculatePoints(game.getDealerHand()) < 17) {
                                        Card card = deckService.drawCard(deck);
                                        handService.addCardToHand(game.getDealerHand(), card);
                                    }

                                    int playerPoints = handService.calculatePoints(game.getPlayerHand());
                                    int dealerPoints = handService.calculatePoints(game.getDealerHand());

                                    game.setPlayerPoints(playerPoints);
                                    game.setDealerPoints(dealerPoints);
                                    game.setGameStatus(GameStatus.FINISHED);

                                    GameResult result = determineResult(playerPoints, dealerPoints);
                                    game.setGameResult(result);

                                    return gameRepository.save(game)
                                            .flatMap(savedGame ->
                                                    playerService.updateStats(savedGame.getUserName(), result)
                                                            .thenReturn(savedGame)
                                            );
                                })
                );
    }

    private GameResult determineResult(int playerPoints, int dealerPoints) {
        if (playerPoints > 21) {
            return GameResult.DEALER_WIN;
        } else if (dealerPoints > 21) {
            return GameResult.PLAYER_WIN;
        } else if (playerPoints > dealerPoints) {
            return GameResult.PLAYER_WIN;
        } else if (dealerPoints > playerPoints) {
            return GameResult.DEALER_WIN;
        } else {
            return GameResult.TIE;
        }
    }

    private Mono<Game> requireOwnership(Game game, String currentUser) {
        if (!game.getUserName().equals(currentUser)) {
            log.warn("Unauthorized access attempt by '{}' to game '{}'", currentUser, game.getId());
            return Mono.error(new UnauthorizedGameAccessException("This is not your game"));
        }
        return Mono.just(game);
    }

    private Mono<Game> findGameOr404(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Game not found with ID: {}", id);
                    return Mono.error(new GameNotFoundException("Game not found with id: " + id));
                }));
    }

}
