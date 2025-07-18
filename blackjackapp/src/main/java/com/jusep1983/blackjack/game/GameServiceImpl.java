package com.jusep1983.blackjack.game;

import com.jusep1983.blackjack.deck.Card;
import com.jusep1983.blackjack.deck.Deck;
import com.jusep1983.blackjack.hand.Hand;
import com.jusep1983.blackjack.player.Player;
import com.jusep1983.blackjack.deck.DeckService;
import com.jusep1983.blackjack.hand.HandService;
import com.jusep1983.blackjack.player.PlayerService;
import com.jusep1983.blackjack.shared.enums.GameResult;
import com.jusep1983.blackjack.shared.enums.GameStatus;
import com.jusep1983.blackjack.shared.exception.GameAlreadyFinishedException;
import com.jusep1983.blackjack.shared.exception.GameNotFoundException;

import lombok.RequiredArgsConstructor;
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
    public Mono<Game> createGame(String playerName) {
        return playerService.getByName(playerName)
                .switchIfEmpty(
                        playerService.createPlayer(new Player(
                                null, playerName, 0, 0, 0, 0, LocalDateTime.now()
                        ))
                )
                .flatMap(player -> {
                    Game game = new Game();
                    game.setPlayerName(playerName);
                    game.setGameStatus(GameStatus.NEW);
                    game.setCreatedAt(LocalDateTime.now());

                    // Crear y barajar la baraja
                    Deck deck = deckService.createDeck();
                    deckService.shuffleDeck(deck);
                    game.setDeck(deck);

                    // Inicializar manos

                    game.setPlayerHand(new Hand());
                    game.setDealerHand(new Hand());
                    // Repartir dos cartas a jugador y dealer
                    handService.addCardToHand(game.getPlayerHand(), deckService.drawCard(deck));
                    handService.addCardToHand(game.getPlayerHand(), deckService.drawCard(deck));
                    handService.addCardToHand(game.getDealerHand(), deckService.drawCard(deck));
                    handService.addCardToHand(game.getDealerHand(), deckService.drawCard(deck));
                    int playerPoints = handService.calculatePoints(game.getPlayerHand());
                    int dealerPoints = handService.calculatePoints(game.getDealerHand());
                    game.setPlayerPoints(playerPoints);
                    game.setDealerPoints(dealerPoints);
                    // Guardar en MongoDB
                    return gameRepository.save(game);
                });
    }

    @Override
    public Mono<Game> getGameById(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with id: " + id)));
    }

    @Override
    public Mono<Void> deleteGameById(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with id: " + id)))
                .flatMap(game -> gameRepository.deleteById(id));
    }

    @Override
    public Mono<Game> playerHit(String gameId) {
        return getGameById(gameId)
                .flatMap(game -> {
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
                                .flatMap(
                                        g -> playerService.updateStats(g.getPlayerName(), GameResult.DEALER_WIN).thenReturn(g)
                                );
                    } else {
                        game.setGameStatus(GameStatus.IN_PROGRESS);
                        return gameRepository.save(game);
                    }
                });
    }

    @Override
    public Mono<Game> playerStand(String gameId) {
        return getGameById(gameId)
                .flatMap(game -> {
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
                    // Al terminar, marcamos FINISHED
                    game.setGameStatus(GameStatus.FINISHED);
                    GameResult result = determineResult(playerPoints, dealerPoints);
                    game.setGameResult(result);
                    return gameRepository.save(game)
                            .flatMap(savedGame -> playerService.updateStats(savedGame.getPlayerName(), result)
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
