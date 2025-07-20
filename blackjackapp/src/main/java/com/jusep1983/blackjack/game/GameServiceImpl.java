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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final DeckService deckService;
    private final HandService handService;
    private final PlayerService playerService;

    @Override
    public Mono<Game> createGame(String userName) {
        return playerService.getByName(userName)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found: " + userName)))
                .flatMap(player -> {
                    Game game = new Game();
                    game.setUserName(userName);  // ahora usamos userName, no playerName
                    game.setGameStatus(GameStatus.NEW);
                    game.setCreatedAt(LocalDateTime.now());

                    Deck deck = deckService.createDeck();
                    deckService.shuffleDeck(deck);
                    game.setDeck(deck);

                    game.setPlayerHand(new Hand());
                    game.setDealerHand(new Hand());

                    handService.addCardToHand(game.getPlayerHand(), deckService.drawCard(deck));
                    handService.addCardToHand(game.getPlayerHand(), deckService.drawCard(deck));
                    handService.addCardToHand(game.getDealerHand(), deckService.drawCard(deck));
                    handService.addCardToHand(game.getDealerHand(), deckService.drawCard(deck));

                    int playerPoints = handService.calculatePoints(game.getPlayerHand());
                    int dealerPoints = handService.calculatePoints(game.getDealerHand());
                    game.setPlayerPoints(playerPoints);
                    game.setDealerPoints(dealerPoints);

                    return gameRepository.save(game);
                });
    }

//    @Override
//    public Mono<Game> getGameById(String id) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        return gameRepository.findById(id)
//                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with id: " + id)))
//                .flatMap(game -> {
//                    if (!game.getUserName().equals(userName)) {
//                        return Mono.error(new AccessDeniedException("This is not your game"));
//                    }
//                    return Mono.just(game);
//                });
//    }

    public Mono<Game> getGameById(String id, String userName) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with id: " + id)))
                .flatMap(game -> {
                    if (!game.getUserName().equals(userName)) {
                        return Mono.error(new AccessDeniedException("This is not your game"));
                    }
                    return Mono.just(game);
                });
    }

//    @Override
//    public Mono<Void> deleteGameById(String id) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        return gameRepository.findById(id)
//                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with id: " + id)))
//                .flatMap(game -> {
//                    if (!game.getUserName().equals(userName)) {
//                        return Mono.error(new UnauthorizedGameAccessException("You are not the owner of this game"));
//                    }
//                    return gameRepository.deleteById(id);
//                });
//    }

    @Override
    public Mono<Void> deleteGameById(String gameId, String userName) {
        return getGameById(gameId, userName) // esto ya verifica que es el propietario
                .flatMap(gameRepository::delete);
    }

    @Override
    public Mono<Game> playerHit(String gameId, String userName) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with id: " + gameId)))
                .flatMap(game -> {
                    if (!game.getUserName().equals(userName)) {
                        return Mono.error(new UnauthorizedGameAccessException("You are not the owner of this game"));
                    }

                    if (game.getGameStatus() == GameStatus.FINISHED) {
                        return Mono.error(new GameAlreadyFinishedException("Game has already finished"));
                    }

                    Deck deck = game.getDeck();
                    Card card = deckService.drawCard(deck);
                    handService.addCardToHand(game.getPlayerHand(), card);

                    int points = handService.calculatePoints(game.getPlayerHand());
                    game.setPlayerPoints(points);

                    if (points > 21) {
                        game.setGameStatus(GameStatus.FINISHED);
                        game.setGameResult(GameResult.DEALER_WIN);
                        return gameRepository.save(game)
                                .flatMap(g -> playerService.updateStats(g.getUserName(), GameResult.DEALER_WIN).thenReturn(g));
                    } else {
                        game.setGameStatus(GameStatus.IN_PROGRESS);
                        return gameRepository.save(game);
                    }
                });
    }

//    @Override
//    public Mono<Game> playerStand(String gameId) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        return getGameById(gameId)
//                .flatMap(game -> {
//
//                    // Verifica que el usuario autenticado sea el propietario de la partida
//                    if (!game.getUserName().equals(userName)) {
//                        return Mono.error(new UnauthorizedGameAccessException("You are not the owner of this game"));
//                    }
//
//                    Deck deck = game.getDeck();
//
//                    // Dealer golpea hasta al menos 17 puntos
//                    while (handService.calculatePoints(game.getDealerHand()) < 17) {
//                        Card card = deckService.drawCard(deck);
//                        handService.addCardToHand(game.getDealerHand(), card);
//                    }
//
//                    int playerPoints = handService.calculatePoints(game.getPlayerHand());
//                    int dealerPoints = handService.calculatePoints(game.getDealerHand());
//
//                    game.setPlayerPoints(playerPoints);
//                    game.setDealerPoints(dealerPoints);
//                    game.setGameStatus(GameStatus.FINISHED);
//
//                    GameResult result = determineResult(playerPoints, dealerPoints);
//                    game.setGameResult(result);
//
//                    return gameRepository.save(game)
//                            .flatMap(savedGame -> playerService.updateStats(savedGame.getUserName(), result)
//                                    .thenReturn(savedGame));
//                });
//    }

    @Override
    public Mono<Game> playerStand(String gameId, String userName) {
        return getGameById(gameId, userName)
                .flatMap(game -> {
                    // Verifica que el usuario autenticado sea el propietario de la partida
                    if (!game.getUserName().equals(userName)) {
                        return Mono.error(new UnauthorizedGameAccessException("You are not the owner of this game"));
                    }

                    Deck deck = game.getDeck();

                    // Dealer golpea hasta al menos 17 puntos
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
                            .flatMap(savedGame -> playerService.updateStats(savedGame.getUserName(), result)
                                    .thenReturn(savedGame));
                });
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

}
